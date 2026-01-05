package cg.dfs.hotel.repo;


import cg.dfs.hotel.entities.TravelPackage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TravelPackageRepository extends JpaRepository<TravelPackage, Long>, JpaSpecificationExecutor<TravelPackage> {
    Page<TravelPackage> findAllByCreatedBy_Id(Long userId, Pageable pageable);
}
