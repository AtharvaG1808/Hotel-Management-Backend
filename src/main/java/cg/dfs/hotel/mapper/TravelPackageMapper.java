package cg.dfs.hotel.mapper;


import cg.dfs.hotel.dto.TravelPackageDTO;
import cg.dfs.hotel.entities.TravelPackage;
import cg.dfs.hotel.entities.User;
import org.springframework.stereotype.Component;

import java.util.Base64;


@Component
public class TravelPackageMapper {

    public TravelPackage toEntityForCreate(TravelPackageDTO dto, User creator) {
        TravelPackage tp = new TravelPackage();
        tp.setTitle(dto.getTitle());
        tp.setDescription(dto.getDescription());
        tp.setDestination(dto.getDestination());
        tp.setDurationDays(dto.getDurationDays());
        tp.setPrice(dto.getPrice());
        tp.setCreatedBy(creator);

        if (dto.getPhoto1() != null && !dto.getPhoto1().isBlank()) {
            byte[] b = decode(dto.getPhoto1());
            tp.setPhoto1(b);
            tp.setPhoto1Size((long) b.length);
        }
        if (dto.getPhoto2() != null && !dto.getPhoto2().isBlank()) {
            byte[] b = decode(dto.getPhoto2());
            tp.setPhoto2(b);
            tp.setPhoto2Size((long) b.length);
        }
        tp.setPhoto1ContentType(dto.getPhoto1contentType());
        tp.setPhoto2ContentType(dto.getPhoto2contentType());

        return tp;
    }

    public void mergeForUpdate(TravelPackage target, TravelPackageDTO dto) {
        target.setTitle(dto.getTitle());             // <-- set title
        target.setDescription(dto.getDescription());
        target.setDestination(dto.getDestination());
        target.setDurationDays(dto.getDurationDays());
        target.setPrice(dto.getPrice());

        if (dto.getPhoto1() != null && !dto.getPhoto1().isBlank()) {
            byte[] b = Base64.getDecoder().decode(dto.getPhoto1().split(",").length > 1 ? dto.getPhoto1().split(",")[1] : dto.getPhoto1());
            target.setPhoto1(b);
            target.setPhoto1Size((long) b.length);
        }
        if (dto.getPhoto2() != null && !dto.getPhoto2().isBlank()) {
            byte[] b = Base64.getDecoder().decode(dto.getPhoto2().split(",").length > 1 ? dto.getPhoto2().split(",")[1] : dto.getPhoto2());
            target.setPhoto2(b);
            target.setPhoto2Size((long) b.length);
        }

    }


    private String toDataUri(byte[] bytes, String contentType) {
        if (bytes == null || contentType == null) return null;
        String base64 = Base64.getEncoder().encodeToString(bytes);
        return "data:" + contentType + ";base64," + base64;
    }

    public TravelPackageDTO toDto(TravelPackage tp) {
        TravelPackageDTO dto = new TravelPackageDTO();
        dto.setId(tp.getId());
        dto.setTitle(tp.getTitle());                 // <-- set title
        dto.setDescription(tp.getDescription());
        dto.setDestination(tp.getDestination());
        dto.setDurationDays(tp.getDurationDays());
        dto.setPrice(tp.getPrice());
        if (tp.getCreatedBy() != null) {
            dto.setCreatedById(tp.getCreatedBy().getId());
            dto.setCreatedByUsername(tp.getCreatedBy().getUsername());
        }
        dto.setCreatedAt(tp.getCreatedAt());
        dto.setUpdatedAt(tp.getUpdatedAt());

        dto.setPhoto1(toDataUri(tp.getPhoto1(), tp.getPhoto1ContentType()));
        dto.setPhoto2(toDataUri(tp.getPhoto2(), tp.getPhoto2ContentType()));

        // Optional: map metadata if you keep them
        dto.setPhoto1contentType(tp.getPhoto1ContentType());
        dto.setPhoto1Size(tp.getPhoto1Size());
        dto.setPhoto2contentType(tp.getPhoto2ContentType());
        dto.setPhoto2Size(tp.getPhoto2Size());

        return dto;
    }

    private byte[] decode(String base64) {
        int comma = base64.indexOf(',');
        String pure = (comma >= 0) ? base64.substring(comma + 1) : base64;
        return Base64.getDecoder().decode(pure);
    }

}

