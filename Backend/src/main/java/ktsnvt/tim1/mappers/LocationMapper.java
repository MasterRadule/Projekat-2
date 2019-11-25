package ktsnvt.tim1.mappers;

import ktsnvt.tim1.DTOs.LocationDTO;
import ktsnvt.tim1.model.Location;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper implements IMapper<Location, LocationDTO> {
    @Override
    public Location toEntity(LocationDTO dto) {
        Location l = new Location();
        l.setDisabled(dto.isDisabled());
        l.setId(null);
        l.setLatitude(dto.getLatitude());
        l.setLongitude(dto.getLongitude());
        l.setName(dto.getName());

        return l;
    }

    @Override
    public LocationDTO toDTO(Location location) {
        LocationDTO dto = new LocationDTO();
        dto.setDisabled(location.isDisabled());
        dto.setId(location.getId());
        dto.setLatitude(location.getLatitude());
        dto.setLongitude(location.getLongitude());
        dto.setName(location.getName());

        return dto;
    }
}
