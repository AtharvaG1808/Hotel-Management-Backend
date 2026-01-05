
package cg.dfs.hotel.repo;

import cg.dfs.hotel.entities.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HotelRepository extends JpaRepository<Hotel, Long>, JpaSpecificationExecutor<Hotel> {

    Page<Hotel> findAllByCreatedBy_Id(Long userId, Pageable pageable);



    @Query(
            value = """
        select h.* from hotels h
        where (:qLower is null
               or position(cast(:qLower as text) in lower(h.name)) > 0
               or position(cast(:qLower as text) in lower(h.city)) > 0
               or position(cast(:qLower as text) in lower(h.destination)) > 0)
          and (:countryLower is null or lower(h.country) = :countryLower)
          and (:stateLower   is null or lower(h.state)   = :stateLower)
          and (:minRating is null or h.rating >= :minRating)
        """,
            countQuery = """
        select count(*) from hotels h
        where (:qLower is null
               or position(cast(:qLower as text) in lower(h.name)) > 0
               or position(cast(:qLower as text) in lower(h.city)) > 0
               or position(cast(:qLower as text) in lower(h.destination)) > 0)
          and (:countryLower is null or lower(h.country) = :countryLower)
          and (:stateLower   is null or lower(h.state)   = :stateLower)
          and (:minRating is null or h.rating >= :minRating)
        """,
            nativeQuery = true
    )
    Page<Hotel> searchNormalized(@Param("qLower") String qLower,
                                       @Param("countryLower") String countryLower,
                                       @Param("stateLower") String stateLower,
                                       @Param("minRating") Double minRating,
                                       Pageable pageable);

}
