package cg.dfs.hotel.service;

import cg.dfs.hotel.dto.RoomCreateUpdateRequest;
import cg.dfs.hotel.dto.RoomDTO;
import cg.dfs.hotel.entities.Hotel;
import cg.dfs.hotel.entities.Room;
import cg.dfs.hotel.exception.BadRequestException;
import cg.dfs.hotel.exception.ResourceNotFoundException;
import cg.dfs.hotel.mapper.RoomMapper;
import cg.dfs.hotel.repo.HotelRepository;
import cg.dfs.hotel.repo.RoomRepository;
import cg.dfs.hotel.security.RoomSecurity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final RoomSecurity roomSecurity;

    public RoomService(RoomRepository roomRepository,
                       HotelRepository hotelRepository,
                       RoomSecurity roomSecurity) {
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
        this.roomSecurity = roomSecurity;
    }


    public RoomDTO create(RoomCreateUpdateRequest request) {
        // Authorization
        roomSecurity.assertCanManageHotel(request.getHotelId());

        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Hotel not found with id: " + request.getHotelId()));

        Room room = RoomMapper.toEntity(request, hotel);
        Room saved = roomRepository.save(room);
        return RoomMapper.toDto(saved);
    }

    /** Get a room by ID (read-only; generally unrestricted). */
    @Transactional(readOnly = true)
    public RoomDTO getById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
        return RoomMapper.toDto(room);
    }

    /** List rooms with optional filters and pagination (read-only). */
    @Transactional(readOnly = true)
    public Page<RoomDTO> list(Long hotelId, String type, Pageable pageable) {
        Page<Room> page;
        if (hotelId != null && type != null && !type.isBlank()) {
            page = roomRepository.findByHotel_IdAndTypeIgnoreCase(hotelId, type.trim(), pageable);
        } else if (hotelId != null) {
            page = roomRepository.findByHotel_Id(hotelId, pageable);
        } else if (type != null && !type.isBlank()) {
            page = roomRepository.findByTypeIgnoreCase(type.trim(), pageable);
        } else {
            page = roomRepository.findAll(pageable);
        }
        return page.map(RoomMapper::toDto);
    }


     //hotelId cannot change.
     // Only the manager of that hotel can update.
    public RoomDTO update(Long id, RoomCreateUpdateRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));

        Long existingHotelId = room.getHotel().getId();
        Long requestedHotelId = request.getHotelId();

        if (!existingHotelId.equals(requestedHotelId)) {
            throw new BadRequestException("Cannot change hotelId of an existing room. " +
                    "Current: " + existingHotelId + ", requested: " + requestedHotelId);
        }

        // Authorization
        roomSecurity.assertCanManageHotel(existingHotelId);

        Hotel hotel = hotelRepository.findById(existingHotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + existingHotelId));

        RoomMapper.copy(request, room, hotel);
        Room saved = roomRepository.save(room);
        return RoomMapper.toDto(saved);
    }

    /** Partial update: inventory only (PATCH) — only that hotel's manager allowed. */
    public RoomDTO updateInventory(Long id, Integer inventory) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));

        roomSecurity.assertCanManageHotel(room.getHotel().getId());

        room.setInventory(inventory);
        Room saved = roomRepository.save(room);
        return RoomMapper.toDto(saved);
    }

    /** Delete a room — only that hotel's manager allowed. */
    public void delete(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));

        roomSecurity.assertCanManageHotel(room.getHotel().getId());

        roomRepository.delete(room);
    }
}


