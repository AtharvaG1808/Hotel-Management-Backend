package cg.dfs.hotel.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class HotelRatingId implements Serializable {
    @Column(name = "hotel_id", nullable = false)
    private Long hotelId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    public HotelRatingId() {}
    public HotelRatingId(Long hotelId, Long userId) {
        this.hotelId = hotelId; this.userId = userId;
    }

    public Long getHotelId() { return hotelId; }
    public void setHotelId(Long hotelId) { this.hotelId = hotelId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HotelRatingId)) return false;
        HotelRatingId that = (HotelRatingId) o;
        return Objects.equals(hotelId, that.hotelId) &&
                Objects.equals(userId, that.userId);
    }
    @Override public int hashCode() { return Objects.hash(hotelId, userId); }
}
