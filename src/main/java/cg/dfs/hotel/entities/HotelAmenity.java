package cg.dfs.hotel.entities;


import jakarta.persistence.*;

@Entity
@Table(name = "hotel_amenities")
public class HotelAmenity {

    @EmbeddedId
    private HotelAmenityId id;

    @MapsId("hotelId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hotel_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_hotel_amenities_hotel"))
    private Hotel hotel;

    public HotelAmenity() {}

    public HotelAmenity(Hotel hotel, String amenity) {
        this.hotel = hotel;
        this.id = new HotelAmenityId(hotel.getId(), amenity);
    }

    public HotelAmenityId getId() { return id; }
    public void setId(HotelAmenityId id) { this.id = id; }

    public Hotel getHotel() { return hotel; }
    public void setHotel(Hotel hotel) { this.hotel = hotel; }

    // Convenience
    @Transient
    public String getAmenity() {
        return id != null ? id.getAmenity() : null;
    }
}
