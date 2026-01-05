package cg.dfs.hotel.repo;


import cg.dfs.hotel.entities.HotelAmenity;
import cg.dfs.hotel.entities.HotelAmenityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface HotelAmenityRepository extends JpaRepository<HotelAmenity, HotelAmenityId> {
    List<HotelAmenity> findByIdHotelId(Long hotelId);
    boolean existsByIdHotelIdAndIdAmenity(Long hotelId, String amenity);
    void deleteByIdHotelIdAndIdAmenity(Long hotelId, String amenity);

    // batch load for search pages
    List<HotelAmenity> findByIdHotelIdIn(Collection<Long> hotelIds);

    @Query("select ha.id.amenity from HotelAmenity ha where ha.id.hotelId = :hotelId")
    List<String> findAmenityNamesByHotelId(@Param("hotelId") Long hotelId);

}

