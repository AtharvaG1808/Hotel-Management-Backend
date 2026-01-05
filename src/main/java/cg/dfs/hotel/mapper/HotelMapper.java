package cg.dfs.hotel.mapper;

import cg.dfs.hotel.dto.HotelDTO;
import cg.dfs.hotel.entities.Hotel;
import cg.dfs.hotel.entities.User;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class HotelMapper {

    public Hotel toEntityForCreate(HotelDTO dto, User creator) {
        Hotel h = new Hotel();
        h.setName(dto.getName());
        h.setDescription(dto.getDescription());
        h.setDestination(dto.getDestination());
        h.setAddressLine1(dto.getAddressLine1());
        h.setCity(dto.getCity());
        h.setState(dto.getState());
        h.setCountry(dto.getCountry());
        h.setZip(dto.getZip());
        h.setCreatedBy(creator);
        if (dto.getBannerImage() != null && !dto.getBannerImage().isBlank()) {
            byte[] b = decode(dto.getBannerImage());
            h.setBannerImage(b);
        }
        if (dto.getPhoto1() != null && !dto.getPhoto1().isBlank()) {
            byte[] b = decode(dto.getPhoto1());
            h.setPhoto1(b);
        }
        if (dto.getPhoto2() != null && !dto.getPhoto2().isBlank()) {
            byte[] b = decode(dto.getPhoto2());
            h.setPhoto2(b);
        }
        return h;
    }

    public void mergeForUpdate(Hotel target, HotelDTO dto) {
        target.setName(dto.getName());
        target.setDescription(dto.getDescription());
        target.setDestination(dto.getDestination());
        target.setAddressLine1(dto.getAddressLine1());
        target.setCity(dto.getCity());
        target.setState(dto.getState());
        target.setCountry(dto.getCountry());
        target.setZip(dto.getZip());
    }

    public HotelDTO toDto(Hotel h) {
        HotelDTO dto = new HotelDTO();
        dto.setId(h.getId());
        dto.setName(h.getName());
        dto.setDescription(h.getDescription());
        dto.setDestination(h.getDestination());
        dto.setAddressLine1(h.getAddressLine1());
        dto.setCity(h.getCity());
        dto.setState(h.getState());
        dto.setCountry(h.getCountry());
        dto.setZip(h.getZip());
        dto.setRating(h.getRating());
        if (h.getCreatedBy() != null) {
            dto.setCreatedById(h.getCreatedBy().getId());
            dto.setCreatedByUsername(h.getCreatedBy().getUsername());
        }
        dto.setBannerImage(toDataUri(h.getBannerImage()));
        dto.setPhoto1(toDataUri(h.getPhoto1()));
        dto.setPhoto2(toDataUri(h.getPhoto2()));
        dto.setCreatedAt(h.getCreatedAt());
        dto.setUpdatedAt(h.getUpdatedAt());
        return dto;
    }

    private byte[] decode(String base64) {
        int comma = base64.indexOf(',');
        String pure = (comma >= 0) ? base64.substring(comma + 1) : base64;
        return Base64.getDecoder().decode(pure);
    }
    private String toDataUri(byte[] bytes) {
        if (bytes == null) return null;
        String base64 = Base64.getEncoder().encodeToString(bytes);
        return "data:image/jpeg;base64," + base64;
    }
}
