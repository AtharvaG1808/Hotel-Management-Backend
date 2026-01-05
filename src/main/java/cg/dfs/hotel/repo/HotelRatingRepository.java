package cg.dfs.hotel.repo;

import cg.dfs.hotel.entities.HotelRating;
import cg.dfs.hotel.entities.HotelRatingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HotelRatingRepository extends JpaRepository<HotelRating, HotelRatingId> {


    @Query("select avg(r.stars) from HotelRating r where r.hotel.id = :hotelId")
    Double findAverageStarsByHotelId(@Param("hotelId") Long hotelId);


    long countByHotel_Id(Long hotelId);
}
