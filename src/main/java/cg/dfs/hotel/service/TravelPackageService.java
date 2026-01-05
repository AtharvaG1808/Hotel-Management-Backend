
package cg.dfs.hotel.service;

import cg.dfs.hotel.dto.TravelPackageDTO;
import cg.dfs.hotel.entities.TravelPackage;
import cg.dfs.hotel.entities.User;
import cg.dfs.hotel.exception.ResourceNotFoundException;
import cg.dfs.hotel.exception.UnauthorizedException;
import cg.dfs.hotel.mapper.TravelPackageMapper;
import cg.dfs.hotel.repo.TravelPackageRepository;
import cg.dfs.hotel.repo.UserRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cg.dfs.hotel.exception.ForbiddenOperationException; // custom 403

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;


import java.util.ArrayList;
import java.util.List;

@Service
public class TravelPackageService {

    private final TravelPackageRepository repo;
    private final UserRepository userRepo;
    private final TravelPackageMapper mapper;

    public TravelPackageService(TravelPackageRepository repo, UserRepository userRepo, TravelPackageMapper mapper) {
        this.repo = repo;
        this.userRepo = userRepo;
        this.mapper = mapper;
    }

    @Transactional
    public TravelPackageDTO create(TravelPackageDTO dto) {

        if (dto.getPhoto1() == null || dto.getPhoto1().isBlank()
                || dto.getPhoto2() == null || dto.getPhoto2().isBlank()) {
            throw new IllegalArgumentException("Please provide two photos for the travel package.");
        }
            User creator = getCurrentUser();
        TravelPackage entity = mapper.toEntityForCreate(dto, creator);
        TravelPackage saved = repo.save(entity);
        return mapper.toDto(saved);
    }


    @Transactional(readOnly = true)
    public Page<TravelPackageDTO> myPackages(
            Integer page,
            Integer size,
            String sortBy,
            String sortDir
    ) {
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

        return repo.findAllByCreatedBy_Id(current.getId(), pageable)
                .map(mapper::toDto);
    }



    @Transactional
    public TravelPackageDTO update(Long id, TravelPackageDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User current = getCurrentUser(); // you already have this

        TravelPackage entity = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Travel package not found: " + id));

        boolean isOwner = entity.getCreatedBy() != null &&
                entity.getCreatedBy().getId().equals(current.getId());
        boolean isAdmin = isAdmin(auth);

        if (!isOwner && !isAdmin) {
            throw new ForbiddenOperationException("You can only modify packages you created");
        }

        mapper.mergeForUpdate(entity, dto);
        TravelPackage saved = repo.save(entity);
        return mapper.toDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User current = getCurrentUser();

        TravelPackage entity = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Travel package not found: " + id));

        boolean isOwner = entity.getCreatedBy() != null &&
                entity.getCreatedBy().getId().equals(current.getId());
        boolean isAdmin = isAdmin(auth);

        if (!isOwner && !isAdmin) {
            throw new ForbiddenOperationException("You can only delete packages you created");
        }

        repo.delete(entity);
    }



    @Transactional(readOnly = true)
    public TravelPackageDTO get(Long id) {
        TravelPackage entity = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Travel package not found: " + id));
        return mapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public Page<TravelPackageDTO> search(
            String keyword,
            String destination,
            Integer minDurationDays,
            Integer maxDurationDays,
            Double minPrice,
            Double maxPrice,
            Integer page,
            Integer size,
            String sortBy,
            String sortDir
    ) {
        Specification<TravelPackage> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("description")), like),
                        cb.like(cb.lower(root.get("destination")), like)
                ));
            }

            if (destination != null && !destination.isBlank()) {
                String normalized = destination.trim().toLowerCase();
                predicates.add(cb.equal(cb.lower(root.get("destination")), normalized));
            }
            if (minDurationDays != null) {
                predicates.add(cb.ge(root.get("durationDays"), minDurationDays));
            }
            if (maxDurationDays != null) {
                predicates.add(cb.le(root.get("durationDays"), maxDurationDays));
            }
            if (minPrice != null) {
                predicates.add(cb.ge(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.le(root.get("price"), maxPrice));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC,
                (sortBy == null || sortBy.isBlank()) ? "price" : sortBy
        );

        Pageable pageable = PageRequest.of(
                page == null ? 0 : page,
                size == null ? 10 : size,
                sort
        );

        return repo.findAll(spec, pageable).map(mapper::toDto);
    }

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
        if (auth == null || !auth.isAuthenticated()) return false;
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
    }

}
