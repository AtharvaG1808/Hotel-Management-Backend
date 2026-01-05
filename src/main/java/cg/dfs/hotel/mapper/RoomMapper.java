package cg.dfs.hotel.mapper;

import cg.dfs.hotel.dto.RoomCreateUpdateRequest;
import cg.dfs.hotel.dto.RoomDTO;
import cg.dfs.hotel.entities.Hotel;
import cg.dfs.hotel.entities.Room;

public class RoomMapper {

    private RoomMapper() {}

    public static RoomDTO toDto(Room room) {
        if (room == null) return null;
        Long hotelId = (room.getHotel() != null) ? room.getHotel().getId() : null;
        return new RoomDTO(
                room.getId(),
                hotelId,
                room.getType(),
                room.getDescription(),
                room.getCapacity(),
                room.getPrice(),
                room.getInventory(),
                room.getCreatedAt(),
                room.getUpdatedAt()
        );
    }

    public static Room toEntity(RoomCreateUpdateRequest req, Hotel hotel) {
        Room room = new Room();
        room.setHotel(hotel);
        room.setType(req.getType());
        room.setDescription(req.getDescription());
        room.setCapacity(req.getCapacity());
        room.setPrice(req.getPrice());
        room.setInventory(req.getInventory());
        return room;
    }

    /** Copy mutable fields from request into existing entity */
    public static void copy(RoomCreateUpdateRequest req, Room room, Hotel hotel) {
        room.setHotel(hotel);
        room.setType(req.getType());
        room.setDescription(req.getDescription());
        room.setCapacity(req.getCapacity());
        room.setPrice(req.getPrice());
        room.setInventory(req.getInventory());
    }
}
