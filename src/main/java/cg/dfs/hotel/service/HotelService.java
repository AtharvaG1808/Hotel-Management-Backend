
package cg.dfs.hotel.service;

import cg.dfs.hotel.dto.HotelDTO;
import cg.dfs.hotel.entities.Hotel;
import cg.dfs.hotel.entities.HotelAmenity;
import cg.dfs.hotel.entities.User;
import cg.dfs.hotel.exception.ForbiddenOperationException;
import cg.dfs.hotel.exception.ResourceNotFoundException;
import cg.dfs.hotel.exception.UnauthorizedException;
import cg.dfs.hotel.mapper.HotelMapper;
import cg.dfs.hotel.repo.HotelAmenityRepository;
import cg.dfs.hotel.repo.HotelRepository;
import cg.dfs.hotel.repo.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class HotelService {

    private final HotelRepository hotelRepo;
    private final HotelAmenityRepository amenityRepo;
    private final UserRepository userRepo;
    private final HotelMapper mapper;

    public HotelService(HotelRepository hotelRepo,
                        HotelAmenityRepository amenityRepo,
                        UserRepository userRepo,
                        HotelMapper mapper) {
        this.hotelRepo = hotelRepo;
        this.amenityRepo = amenityRepo;
        this.userRepo = userRepo;
        this.mapper = mapper;
    }

    // === View/Search (USER, MANAGER, ADMIN) ===

    @Transactional(readOnly = true)
    public Page<HotelDTO> search(String q, String country, String state, Double minRating, Pageable pageable) {
        String qLower = (q == null || q.isBlank()) ? null : q.toLowerCase(Locale.ROOT).trim();
        String countryLower = (country == null || country.isBlank()) ? null : country.toLowerCase(Locale.ROOT).trim();
        String stateLower = (state == null || state.isBlank()) ? null : state.toLowerCase(Locale.ROOT).trim();

        // Call a repository method that expects already-lowercased inputs
        Page<Hotel> page = hotelRepo.searchNormalized(qLower, countryLower, stateLower, minRating, pageable);

        List<Long> hotelIds = page.getContent().stream().map(Hotel::getId).toList();
        Map<Long, List<String>> amenitiesByHotel = batchAmenities(hotelIds);

        List<HotelDTO> dtos = page.getContent().stream()
                .map(mapper::toDto)
                .peek(dto -> dto.setAmenities(amenitiesByHotel.getOrDefault(dto.getId(), List.of())))
                .toList();

        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }


    @Transactional(readOnly = true)
    public HotelDTO getById(Long id) {
        Hotel h = hotelRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found: " + id));
        HotelDTO dto = mapper.toDto(h);
        dto.setAmenities(listAmenitiesInternal(id));
        return dto;
    }

    // === Create (MANAGER or ADMIN) ===
    @PreAuthorize("hasAnyRole('ADMIN','HOTELMANAGER')")
    public HotelDTO create(HotelDTO dto) {
        User creator = getCurrentUser();
        Hotel h = mapper.toEntityForCreate(dto, creator);
        h.setRating(0.0);
        h.setCreatedAt(LocalDateTime.now());
        h.setUpdatedAt(LocalDateTime.now());
        h = hotelRepo.save(h);

        // reconcile amenities from DTO if provided
        reconcileAmenities(h, dto.getAmenities());

        HotelDTO out = mapper.toDto(h);
        out.setAmenities(listAmenitiesInternal(h.getId()));
        return out;
    }

    // === Update (MANAGER own; ADMIN any) ===
    @PreAuthorize("hasAnyRole('ADMIN','HOTELMANAGER')")
    public HotelDTO update(Long id, HotelDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User current = getCurrentUser();

        Hotel h = hotelRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found: " + id));

        boolean isOwner = h.getCreatedBy() != null && h.getCreatedBy().getId().equals(current.getId());
        boolean isAdmin = isAdmin(auth);

        if (!isOwner && !isAdmin) {
            throw new ForbiddenOperationException("You can only modify hotels you created");
        }

        mapper.mergeForUpdate(h, dto);
        h.setUpdatedAt(LocalDateTime.now());

        // If amenities provided, treat as full replace
        if (dto.getAmenities() != null) {
            reconcileAmenities(h, dto.getAmenities());
        }

        HotelDTO out = mapper.toDto(h);
        out.setAmenities(listAmenitiesInternal(h.getId()));
        return out;
    }

    // === Delete (MANAGER own; ADMIN any) ===
    @PreAuthorize("hasAnyRole('ADMIN','HOTELMANAGER','AGENT')")
    public void delete(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User current = getCurrentUser();

        Hotel h = hotelRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found: " + id));

        boolean isOwner = h.getCreatedBy() != null && h.getCreatedBy().getId().equals(current.getId());
        boolean isAdmin = isAdmin(auth);

        if (!isOwner && !isAdmin) {
            throw new ForbiddenOperationException("You can only delete hotels you created");
        }
        hotelRepo.delete(h);
    }

    // === Amenities (view allowed to all) ===
    @Transactional(readOnly = true)
    public List<String> listAmenities(Long hotelId) {
        return listAmenitiesInternal(hotelId);
    }

    // === Optional granular amenity ops (MANAGER own; ADMIN any) ===



    @PreAuthorize("hasAnyRole('ADMIN','HOTELMANAGER')")
    @Transactional
    public List<String> addAmenity(Long hotelId, String amenity) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User current = getCurrentUser();

        Hotel h = hotelRepo.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found: " + hotelId));

        boolean isOwner = h.getCreatedBy() != null && h.getCreatedBy().getId().equals(current.getId());
        boolean isAdmin = isAdmin(auth);

        if (!isOwner && !isAdmin) {
            throw new ForbiddenOperationException("You can only modify amenities of hotels you created");
        }

        if (!amenityRepo.existsByIdHotelIdAndIdAmenity(hotelId, amenity)) {
            amenityRepo.save(new HotelAmenity(h, amenity));
        }

        // NEW: return the updated list of amenity names
        return amenityRepo.findAmenityNamesByHotelId(hotelId);
    }



    @PreAuthorize("hasAnyRole('ADMIN','HOTELMANAGER')")
    public void removeAmenity(Long hotelId, String amenity) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User current = getCurrentUser();

        Hotel h = hotelRepo.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found: " + hotelId));

        boolean isOwner = h.getCreatedBy() != null && h.getCreatedBy().getId().equals(current.getId());
        boolean isAdmin = isAdmin(auth);

        if (!isOwner && !isAdmin) {
            throw new ForbiddenOperationException("You can only modify amenities of hotels you created");
        }

        amenityRepo.deleteByIdHotelIdAndIdAmenity(hotelId, amenity);
    }


    @Transactional(readOnly = true)
    public Page<HotelDTO> myHotels(
            Integer page,
            Integer size,
            String sortBy,
            String sortDir
    ) {
        // Get current logged-in user (same approach you used for travel packages)
        User current = getCurrentUser();

        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC,
                (sortBy == null || sortBy.isBlank()) ? "updatedAt" : sortBy
        );

        Pageable pageable = PageRequest.of(
                page == null ? 0 : page,
                size == null ? 10 : size,
                sort
        );

        return hotelRepo.findAllByCreatedBy_Id(current.getId(), pageable)
                .map(mapper::toDto);
    }


    // ===== Helpers =====

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("Not authenticated");
        }
        String name = auth.getName(); // username or email depending on your auth
        return userRepo.findByUsername(name)
                .or(() -> userRepo.findByEmail(name))
                .orElseThrow(() -> new UnauthorizedException("Authenticated user not found: " + name));
    }

    private boolean isAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    private List<String> listAmenitiesInternal(Long hotelId) {
        return amenityRepo.findByIdHotelId(hotelId).stream()
                .map(a -> a.getId().getAmenity())
                .sorted(String::compareToIgnoreCase)
                .toList();
    }

    private Map<Long, List<String>> batchAmenities(List<Long> hotelIds) {
        if (hotelIds == null || hotelIds.isEmpty()) return Collections.emptyMap();
        return amenityRepo.findByIdHotelIdIn(hotelIds).stream()
                .collect(Collectors.groupingBy(
                        a -> a.getId().getHotelId(),
                        Collectors.mapping(a -> a.getId().getAmenity(),
                                Collectors.collectingAndThen(Collectors.toList(), list -> {
                                    list.sort(String::compareToIgnoreCase);
                                    return list;
                                }))
                ));
    }

    private void reconcileAmenities(Hotel hotel, List<String> newAmenities) {
        if (newAmenities == null) return;
        Set<String> desired = new LinkedHashSet<>();
        for (String a : newAmenities) {
            if (a != null) {
                String t = a.trim();
                if (!t.isEmpty()) desired.add(t);
            }
        }
        List<String> current = listAmenitiesInternal(hotel.getId());
        Set<String> currentSet = new LinkedHashSet<>(current);

        for (String a : currentSet) {
            if (!desired.contains(a)) {
                amenityRepo.deleteByIdHotelIdAndIdAmenity(hotel.getId(), a);
            }
        }
        for (String a : desired) {
            if (!currentSet.contains(a)) {
                amenityRepo.save(new HotelAmenity(hotel, a));
            }
        }
    }
}
