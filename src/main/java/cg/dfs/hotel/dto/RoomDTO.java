package cg.dfs.hotel.dto;

import java.time.LocalDateTime;

public class RoomDTO {
    private Long id;
    private Long hotelId;
    private String type;
    private String description;
    private Integer capacity;
    private Double price;
    private Integer inventory;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RoomDTO() {}

    public RoomDTO(Long id, Long hotelId, String type, String description, Integer capacity,
                   Double price, Integer inventory, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.hotelId = hotelId;
        this.type = type;
        this.description = description;
        this.capacity = capacity;
        this.price = price;
        this.inventory = inventory;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters & setters ...
    // (Generate via IDE to keep concise)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
