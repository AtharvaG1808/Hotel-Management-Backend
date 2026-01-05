
package cg.dfs.hotel.service;

import cg.dfs.hotel.entities.Hotel;
import cg.dfs.hotel.entities.HotelRating;
import cg.dfs.hotel.entities.HotelRatingId;
import cg.dfs.hotel.entities.User;
import cg.dfs.hotel.exception.ResourceNotFoundException;
import cg.dfs.hotel.exception.UnauthorizedException;
import cg.dfs.hotel.repo.HotelRatingRepository;
import cg.dfs.hotel.repo.HotelRepository;
import cg.dfs.hotel.repo.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class HotelRatingService {

    private final HotelRepository hotelRepo;
    private final HotelRatingRepository ratingRepo;
    private final UserRepository userRepo;

    public HotelRatingService(HotelRepository hotelRepo,
                              HotelRatingRepository ratingRepo,
                              UserRepository userRepo) {
        this.hotelRepo = hotelRepo;
        this.ratingRepo = ratingRepo;
        this.userRepo = userRepo;
    }

    @PreAuthorize("hasAnyRole('USER','HOTELMANAGER','ADMIN')")
    public double rateHotel(Long hotelId, int stars) {
        if (stars < 1 || stars > 5) {
            throw new IllegalArgumentException("Stars must be between 1 and 5");
        }

        User rater = getCurrentUser();

        Hotel hotel = hotelRepo.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found: " + hotelId));

        HotelRatingId id = new HotelRatingId(hotelId, rater.getId());
        HotelRating rating = ratingRepo.findById(id).orElse(null);

        if (rating == null) {
            rating = new HotelRating(hotel, rater, stars);
        } else {
            rating.setStars(stars);
            rating.setUpdatedAt(LocalDateTime.now());
        }

        ratingRepo.save(rating);

        Double avg = ratingRepo.findAverageStarsByHotelId(hotelId);
        hotel.setRating(avg != null ? avg : 0.0);
        hotel.setUpdatedAt(LocalDateTime.now());

        return hotel.getRating();
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("Not authenticated");
        }
        String name = auth.getName();
        return userRepo.findByUsername(name)
                .or(() -> userRepo.findByEmail(name))
                .orElseThrow(() -> new UnauthorizedException("Authenticated user not found: " + name));
    }
}
