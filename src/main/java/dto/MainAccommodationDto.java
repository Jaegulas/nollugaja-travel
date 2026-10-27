package dto;

import java.time.LocalDateTime;
import vo.AccommodationVo;

public class MainAccommodationDto extends AccommodationVo {

    private final boolean liked;

    public MainAccommodationDto(
            Integer id,
            String placeid,
            String name,
            String addr,
            String type,
            String tel,
            String link,
            Double lat,
            Double lon,
            Double rating,
            Integer reviewCount,
            Boolean liked,
            String picpath,
            LocalDateTime updatedAt) {

        super(id, placeid, name, addr, type, tel, link,
              lat, lon, rating, reviewCount, liked, picpath, updatedAt);

        this.liked = Boolean.TRUE.equals(liked);
    }

    @Override
    public boolean isIsliked() {
        return liked;
    }
}