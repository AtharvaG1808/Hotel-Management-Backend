package cg.dfs.hotel.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "hotel_ratings",
        uniqueConstraints = @UniqueConstraint(name = "uk_hotel_user", columnNames = {"hotel_id", "user_id"}))
public class HotelRating {

    @EmbeddedId
    private HotelRatingId id;

    @MapsId("hotelId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hotel_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_hotel_ratings_hotel"))
    private Hotel hotel;

    // Assuming you have a User entity
    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_hotel_ratings_user"))
    private User user;

    @Column(name = "stars", nullable = false)
    private Integer stars; // 1..5

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public HotelRating() {}

    public HotelRating(Hotel hotel, User user, Integer stars) {
        this.hotel = hotel;
        this.user = user;
        this.id = new HotelRatingId(hotel.getId(), user.getId());
        this.stars = stars;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public HotelRatingId getId() { return id; }
    public void setId(HotelRatingId id) { this.id = id; }

    public Hotel getHotel() { return hotel; }
    public void setHotel(Hotel hotel) { this.hotel = hotel; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Integer getStars() { return stars; }
    public void setStars(Integer stars) { this.stars = stars; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
