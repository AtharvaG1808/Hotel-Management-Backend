
package cg.dfs.hotel.security;

import cg.dfs.hotel.entities.Hotel;
import cg.dfs.hotel.entities.Room;
import cg.dfs.hotel.exception.ResourceNotFoundException;
import cg.dfs.hotel.repo.HotelRepository;
import cg.dfs.hotel.repo.RoomRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("roomSecurity")
public class RoomSecurity {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;

    public RoomSecurity(HotelRepository hotelRepository, RoomRepository roomRepository) {
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
    }

    /** Throws AccessDeniedException if the current user cannot manage the given hotel. */
    public void assertCanManageHotel(Long hotelId) {
        if (!canManageHotel(hotelId)) {
            throw new AccessDeniedException("You are not allowed to manage rooms for hotelId=" + hotelId);
        }
    }

    /** Returns true if the current user is the manager (creator) of the hotel and has ROLE_MANAGER. */
    public boolean canManageHotel(Long hotelId) {
        if (hotelId == null) return false;

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found: " + hotelId));

        Long managerUserId = resolveHotelManagerUserId(hotel);
        Long currentUserId = getCurrentUserId();

        return managerUserId != null && managerUserId.equals(currentUserId);
    }

    /** Returns true if the current user can manage the room (i.e., manages the room's hotel). */
    public boolean canManageRoom(Long roomId) {
        if (roomId == null) return false;

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found: " + roomId));

        Long hotelId = room.getHotel().getId();
        return canManageHotel(hotelId);
    }

    // ----------------- Helpers -----------------

    /**
     * Resolve manager's userId from Hotel.
     * In your model, the manager is the creator of the hotel.
     */
    private Long resolveHotelManagerUserId(Hotel hotel) {
        return (hotel.getCreatedBy() != null) ? hotel.getCreatedBy().getId() : null;
    }

    /** Retrieve current authenticated user's id from CustomUserDetails. */
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) return null;
        Object principal = auth.getPrincipal();

        if (principal instanceof CustomUserDetails cud) {
            return cud.getUserId();
        }
        return null;
    }
}
