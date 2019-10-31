package ktsnvt.tim1.services;

import ktsnvt.tim1.DTOs.LocationDTO;
import ktsnvt.tim1.exceptions.EntityNotFoundException;
import ktsnvt.tim1.repositories.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class LocationService {
    @Autowired
    private LocationRepository locationRepository;

    public Page<LocationDTO> getLocations(Pageable pageable) {
        return locationRepository.findAll(pageable).map(LocationDTO::new);
    }

    public LocationDTO getLocation(Long id) throws EntityNotFoundException {
        return new LocationDTO(locationRepository.findById(id).orElseThrow(EntityNotFoundException::new));
    }

    public LocationDTO createLocation(LocationDTO location) {
        return new LocationDTO(locationRepository.save(location.convertToEntity()));
    }
}
