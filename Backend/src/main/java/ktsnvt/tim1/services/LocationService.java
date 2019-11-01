package ktsnvt.tim1.services;

import ktsnvt.tim1.DTOs.LocationDTO;
import ktsnvt.tim1.DTOs.SeatGroupDTO;
import ktsnvt.tim1.exceptions.EntityNotFoundException;
import ktsnvt.tim1.exceptions.EntityNotValidException;
import ktsnvt.tim1.model.Location;
import ktsnvt.tim1.model.SeatGroup;
import ktsnvt.tim1.repositories.LocationRepository;
import ktsnvt.tim1.repositories.SeatGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


@Service
public class LocationService {
    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private SeatGroupRepository seatGroupRepository;

    public Page<LocationDTO> getLocations(Pageable pageable) {
        return locationRepository.findAll(pageable)
                .map(LocationDTO::new);
    }

    public LocationDTO getLocation(Long id) throws EntityNotFoundException {
        return new LocationDTO(locationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Location not found")));
    }

    public Page<LocationDTO> searchLocations(String name, Pageable pageable) {
        return locationRepository.findByNameContaining(name, pageable).map(LocationDTO::new);
    }

    public LocationDTO createLocation(LocationDTO location) {
        return new LocationDTO(locationRepository.save(location.convertToEntity()));
    }

    public Page<SeatGroupDTO> getSeatGroups(Long id, Pageable pageable) throws EntityNotFoundException {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Location not found"));

        return (new PageImpl<>(new ArrayList<>(location.getSeatGroups()), pageable, location.getSeatGroups()
                .size())).map(SeatGroupDTO::new);
    }

    public SeatGroupDTO getSeatGroup(Long locationId, Long seatGroupId) throws EntityNotFoundException {
        return new SeatGroupDTO(locationRepository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("Location not found"))
                .getSeatGroups()
                .stream()
                .filter(sg -> sg.getId()
                        .equals(seatGroupId))
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException("Seat group not found")));
    }

    public SeatGroupDTO createSeatGroup(Long id, SeatGroupDTO seatGroup) throws EntityNotFoundException, EntityNotValidException {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Location not found"));

        SeatGroup newSeatGroup = seatGroupRepository.save(seatGroup.convertToEntity());
        location.getSeatGroups()
                .add(newSeatGroup);
        locationRepository.save(location);

        return new SeatGroupDTO(newSeatGroup);
    }
}
