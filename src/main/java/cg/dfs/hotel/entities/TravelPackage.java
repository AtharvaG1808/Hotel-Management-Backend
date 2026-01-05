package cg.dfs.hotel.entities;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "travel_packages")
public class TravelPackage {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 200)
    String description;

    @Column(nullable = false, length = 100)
    private String destination;

    @Column(nullable = false)
    private Integer durationDays;

    @Column(nullable = false)
    private Double price;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "photo1")
    private byte[] photo1;

    @Column(name = "photo1content_type")
    private String photo1ContentType;

    @Column(name = "photo1size")
    private Long photo1Size;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "photo2")
    private byte[] photo2;

    @Column(name = "photo2content_type")
    private String photo2ContentType;

    @Column(name = "photo2size")
    private Long photo2Size;



    public TravelPackage() {}

    public TravelPackage(Long id, String title, String description, String destination, Integer durationDays, Double price, User createdBy, LocalDateTime createdAt, LocalDateTime updatedAt, byte[] photo1, String photo1ContentType, Long photo1Size, byte[] photo2, String photo2ContentType, Long photo2Size) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.destination = destination;
        this.durationDays = durationDays;
        this.price = price;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.photo1 = photo1;
        this.photo1ContentType = photo1ContentType;
        this.photo1Size = photo1Size;
        this.photo2 = photo2;
        this.photo2ContentType = photo2ContentType;
        this.photo2Size = photo2Size;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public byte[] getPhoto1() {
        return photo1;
    }

    public void setPhoto1(byte[] photo1) {
        this.photo1 = photo1;
    }

    public String getPhoto1ContentType() {
        return photo1ContentType;
    }

    public void setPhoto1ContentType(String photo1ContentType) {
        this.photo1ContentType = photo1ContentType;
    }

    public Long getPhoto1Size() {
        return photo1Size;
    }

    public void setPhoto1Size(Long photo1Size) {
        this.photo1Size = photo1Size;
    }

    public byte[] getPhoto2() {
        return photo2;
    }

    public void setPhoto2(byte[] photo2) {
        this.photo2 = photo2;
    }

    public String getPhoto2ContentType() {
        return photo2ContentType;
    }

    public void setPhoto2ContentType(String photo2ContentType) {
        this.photo2ContentType = photo2ContentType;
    }

    public Long getPhoto2Size() {
        return photo2Size;
    }

    public void setPhoto2Size(Long photo2Size) {
        this.photo2Size = photo2Size;
    }
}
