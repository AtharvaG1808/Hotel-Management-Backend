
package cg.dfs.hotel.controller;

import cg.dfs.hotel.dto.HotelDTO;
import cg.dfs.hotel.service.HotelRatingService;
import cg.dfs.hotel.service.HotelService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private final HotelService hotelService;
    private final HotelRatingService ratingService;

    public HotelController(HotelService hotelService, HotelRatingService ratingService) {
        this.hotelService = hotelService;
        this.ratingService = ratingService;
    }

    // SEARCH + VIEW (USER/MANAGER/ADMIN)
    @GetMapping
    @PreAuthorize("hasAnyRole('USER','HOTELMANAGER','ADMIN')")
    public Page<HotelDTO> search(@RequestParam(name="q",required = false) String q,
                                 @RequestParam(name="country",required = false) String country,
                                 @RequestParam(name="state",required = false) String state,
                                 @RequestParam(name="minRating",required = false) Double minRating,
                                 @RequestParam(name="page",defaultValue = "0") int page,
                                 @RequestParam(name="size",defaultValue = "20") int size,
                                 @RequestParam(name="sort",defaultValue = "name,asc") String sort) {
        Sort srt = Sort.by(
                sort.contains(",")
                        ? new Sort.Order("desc".equalsIgnoreCase(sort.split(",")[1])
                        ? Sort.Direction.DESC : Sort.Direction.ASC, sort.split(",")[0])
                        : new Sort.Order(Sort.Direction.ASC, "name")
        );
        Pageable pageable = PageRequest.of(page, size, srt);
        return hotelService.search(q, country, state, minRating, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','HOTELMANAGER','ADMIN')")
    public HotelDTO get(@PathVariable("id") Long id) {
        return hotelService.getById(id);
    }

    // CREATE (MANAGER/ADMIN)
    @PostMapping
    @PreAuthorize("hasAnyRole('AGENT','ADMIN')")
    public ResponseEntity<HotelDTO> create(@RequestBody @Valid HotelDTO dto) {
        return ResponseEntity.ok(hotelService.create(dto));
    }

    // UPDATE (MANAGER own / ADMIN any)
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('HOTELMANAGER','ADMIN')")
    public ResponseEntity<HotelDTO> update(@PathVariable("id") Long id,
                                           @RequestBody @Valid HotelDTO dto) {
        return ResponseEntity.ok(hotelService.update(id, dto));
    }

    // DELETE (MANAGER own / ADMIN any)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('HOTELMANAGER','ADMIN','AGENT')")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        hotelService.delete(id);
        return ResponseEntity.ok().build();
    }

    // AMENITIES (view for all)
    @GetMapping("/{id}/amenities")
    @PreAuthorize("hasAnyRole('USER','HOTELMANAGER','ADMIN')")
    public ResponseEntity<?> amenities(@PathVariable("id") Long id) {
        return ResponseEntity.ok(hotelService.listAmenities(id));
    }

    // Optional granular amenities management (MANAGER own / ADMIN any)

    @PostMapping("/{id}/amenities")
    @PreAuthorize("hasAnyRole('HOTELMANAGER','ADMIN')")
    public ResponseEntity<List<String>> addAmenity(
            @PathVariable("id") Long id,
            @RequestParam(name = "amenity") String amenity) {

        List<String> updatedAmenities = hotelService.addAmenity(id, amenity);
        return ResponseEntity.ok(updatedAmenities); // 200 OK with list of strings
    }


    @DeleteMapping("/{id}/amenities")
    @PreAuthorize("hasAnyRole('HOTELMANAGER','ADMIN')")
    public ResponseEntity<Void> removeAmenity(@PathVariable("id") Long id,
                                              @RequestParam("amenity") String amenity) {
        hotelService.removeAmenity(id, amenity);
        return ResponseEntity.ok().build();
    }

    // RATINGS (dynamic)
    @PostMapping("/{id}/rating")
    @PreAuthorize("hasAnyRole('USER','HOTELMANAGER','ADMIN')")
    public ResponseEntity<Double> rate(@PathVariable("id") Long id,
                                       @RequestParam("stars") int stars) {
        double avg = ratingService.rateHotel(id, stars);
        return ResponseEntity.ok(avg);
    }


    @GetMapping("/mine")
    @PreAuthorize("hasAnyRole('USER','HOTELMANAGER','ADMIN')")
    public Page<HotelDTO> mine(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "sortBy", defaultValue = "updatedAt") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir
            ) {
        return hotelService.myHotels(page, size, sortBy, sortDir);
    }

}
