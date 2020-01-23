package ktsnvt.tim1.services;

import ktsnvt.tim1.DTOs.LocationDTO;
import ktsnvt.tim1.DTOs.LocationOptionDTO;
import ktsnvt.tim1.DTOs.SeatGroupDTO;
import ktsnvt.tim1.exceptions.EntityNotFoundException;
import ktsnvt.tim1.exceptions.EntityNotValidException;
import ktsnvt.tim1.mappers.LocationMapper;
import ktsnvt.tim1.mappers.SeatGroupMapper;
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
import java.util.List;
import java.util.stream.Collectors;


@Service
public class LocationService {
    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private SeatGroupRepository seatGroupRepository;

    @Autowired
    private LocationMapper locationMapper;

    @Autowired
    private SeatGroupMapper seatGroupMapper;

    public Page<LocationDTO> getLocations(Pageable pageable) {
        return locationRepository.findAll(pageable)
                .map(l -> locationMapper.toDTO(l));
    }

    public LocationDTO getLocation(Long id) throws EntityNotFoundException {
        return locationMapper.toDTO(locationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Location not found")));
    }

    public Page<LocationDTO> searchLocations(String name, Pageable pageable) {
        return locationRepository.findByNameIgnoreCaseContaining(name, pageable).map(l -> locationMapper.toDTO(l));
    }

    public LocationDTO createLocation(LocationDTO location) {
        return locationMapper.toDTO(locationRepository.save(locationMapper.toEntity(location)));
    }

    public LocationDTO editLocation(LocationDTO location) throws EntityNotValidException, EntityNotFoundException {
        if (location.getId() == null)
            throw new EntityNotValidException("Location must have an ID");

        Location editedLocation = locationRepository.findById(location.getId())
                .orElseThrow(() -> new EntityNotFoundException("Location not found"));

        editedLocation.setName(location.getName());
        editedLocation.setLongitude(location.getLongitude());
        editedLocation.setLatitude(location.getLatitude());
        editedLocation.setDisabled(location.isDisabled());

        return locationMapper.toDTO(locationRepository.save(editedLocation));
    }

    public Page<SeatGroupDTO> getSeatGroups(Long id, Pageable pageable) throws EntityNotFoundException {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Location not found"));

        return (new PageImpl<>(new ArrayList<>(location.getSeatGroups()), pageable, location.getSeatGroups()
                .size())).map(sg -> seatGroupMapper.toDTO(sg));
    }

    public SeatGroupDTO getSeatGroup(Long locationId, Long seatGroupId) throws EntityNotFoundException {
        return seatGroupMapper.toDTO(locationRepository.findById(locationId)
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

        SeatGroup seatGroupBeforeSave = seatGroupMapper.toEntity(seatGroup);
        seatGroupBeforeSave.setLocation(location);
        SeatGroup newSeatGroup = seatGroupRepository.save(seatGroupBeforeSave);
        location.getSeatGroups()
                .add(newSeatGroup);
        locationRepository.save(location);

        return seatGroupMapper.toDTO(newSeatGroup);
    }

    public List<LocationOptionDTO> getLocationsOptions() {
        return locationRepository.findAll().stream().map(LocationOptionDTO::new).collect(Collectors.toList());
    }

    public SeatGroupDTO editSeatGroupAngle(Long id, SeatGroupDTO seatGroupDTO) throws EntityNotFoundException {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Location not found"));

        SeatGroup seatGroup = location.getSeatGroups().stream()
                .filter(sg -> sg.getId().equals(seatGroupDTO.getId()))
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException("Seat group not found"));

        seatGroup.setAngle(seatGroupDTO.getAngle());

        locationRepository.save(location);

        return seatGroupMapper.toDTO(seatGroup);
    }
}
