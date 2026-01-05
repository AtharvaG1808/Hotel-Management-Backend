package cg.dfs.hotel.dto;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

public class TravelPackageDTO {

    private Long id;
    private Long createdById;
    private String createdByUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String title;
    // Shared request/response fields
    private String description;

    private String destination;

    private Integer durationDays;


    private Double price;


    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String photo1;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String photo2;

    private String photo1contentType;
    private String photo2contentType;
    private Long photo1Size;
    private Long photo2Size;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCreatedById() { return createdById; }
    public void setCreatedById(Long createdById) { this.createdById = createdById; }
    public String getCreatedByUsername() { return createdByUsername; }
    public void setCreatedByUsername(String createdByUsername) { this.createdByUsername = createdByUsername; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public Integer getDurationDays() { return durationDays; }
    public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getPhoto1() { return photo1; }
    public void setPhoto1(String photo1) { this.photo1 = photo1; }
    public String getPhoto2() { return photo2; }
    public void setPhoto2(String photo2) { this.photo2 = photo2; }
    public String getPhoto1contentType() { return photo1contentType; }
    public void setPhoto1contentType(String photo1contentType) { this.photo1contentType = photo1contentType; }
    public String getPhoto2contentType() { return photo2contentType; }
    public void setPhoto2contentType(String photo2contentType) { this.photo2contentType = photo2contentType; }
    public Long getPhoto1Size() { return photo1Size; }
    public void setPhoto1Size(Long photo1Size) { this.photo1Size = photo1Size; }
    public Long getPhoto2Size() { return photo2Size; }
    public void setPhoto2Size(Long photo2Size) { this.photo2Size = photo2Size; }
}
