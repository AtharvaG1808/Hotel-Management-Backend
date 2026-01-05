package cg.dfs.hotel.controller;


import cg.dfs.hotel.dto.TravelPackageDTO;
import cg.dfs.hotel.service.TravelPackageService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/travel-packages")
@CrossOrigin(
        origins = "http://localhost:3000",
        allowedHeaders = {"Authorization", "Content-Type"},
        exposedHeaders = {"Authorization"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
        allowCredentials = "true",
        maxAge = 3600
)
public class TravelPackageController {

    private final TravelPackageService service;

    public TravelPackageController(TravelPackageService service) {
        this.service = service;
    }

    // VIEW: USER, AGENT, ADMIN
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','AGENT','ADMIN')")
    public TravelPackageDTO get(@PathVariable Long id) {
        return service.get(id);
    }

    // SEARCH: USER, AGENT, ADMIN (query params)

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','AGENT','ADMIN')")
    public Page<TravelPackageDTO> search(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "destination", required = false) String destination,
            @RequestParam(name = "minDurationDays", required = false) Integer minDurationDays,
            @RequestParam(name = "maxDurationDays", required = false) Integer maxDurationDays,
            @RequestParam(name = "minPrice", required = false) Double minPrice,
            @RequestParam(name = "maxPrice", required = false) Double maxPrice,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(name = "sortBy", required = false, defaultValue = "price") String sortBy,
            @RequestParam(name = "sortDir", required = false, defaultValue = "asc") String sortDir
    ) {
        return service.search(keyword, destination, minDurationDays, maxDurationDays,
                minPrice, maxPrice, page, size, sortBy, sortDir);
    }


    // CREATE: AGENT, ADMIN
    @PostMapping
    @PreAuthorize("hasAnyRole('AGENT','ADMIN')")
    public TravelPackageDTO create( @RequestBody TravelPackageDTO dto) {
        return service.create(dto);
    }



    // UPDATE: AGENT, ADMIN
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('AGENT','ADMIN')")
    public TravelPackageDTO update(@PathVariable("id") Long id, @RequestBody TravelPackageDTO dto) {
        return service.update(id, dto);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mine")
    public Page<TravelPackageDTO> mine(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "sortBy", defaultValue = "updatedAt") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir
    ) {
        return service.myPackages(page, size, sortBy, sortDir);
    }


}
