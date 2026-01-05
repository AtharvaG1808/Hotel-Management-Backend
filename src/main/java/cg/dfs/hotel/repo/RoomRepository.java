package cg.dfs.hotel.repo;

import cg.dfs.hotel.entities.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Page<Room> findByHotel_Id(Long hotelId, Pageable pageable);

    Page<Room> findByHotel_IdAndTypeIgnoreCase(Long hotelId, String type, Pageable pageable);

    Page<Room> findByTypeIgnoreCase(String type, Pageable pageable);
}
