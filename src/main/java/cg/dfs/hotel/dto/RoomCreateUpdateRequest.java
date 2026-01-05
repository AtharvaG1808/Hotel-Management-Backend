package cg.dfs.hotel.dto;

import jakarta.validation.constraints.*;

public class RoomCreateUpdateRequest {

    @NotNull(message = "hotelId is required")
    private Long hotelId;

    @NotBlank
    @Size(max = 100)
    private String type;

    @Size(max = 1000)
    private String description;

    @NotNull
    @Min(value = 1, message = "capacity must be at least 1")
    private Integer capacity;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "price must be greater than 0")
    private Double price;

    @NotNull
    @Min(value = 0, message = "inventory cannot be negative")
    private Integer inventory;

    public RoomCreateUpdateRequest() {}

    public RoomCreateUpdateRequest(Long hotelId, String type, String description,
                                   Integer capacity, Double price, Integer inventory) {
        this.hotelId = hotelId;
        this.type = type;
        this.description = description;
        this.capacity = capacity;
        this.price = price;
        this.inventory = inventory;
    }

    // Getters & setters ...
    public Long getHotelId() { return hotelId; }
    public void setHotelId(Long hotelId) { this.hotelId = hotelId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Integer getInventory() { return inventory; }
    public void setInventory(Integer inventory) { this.inventory = inventory; }
}
