package ktsnvt.tim1.services;

import ktsnvt.tim1.DTOs.LocationDTO;
import ktsnvt.tim1.DTOs.SeatGroupDTO;
import ktsnvt.tim1.exceptions.EntityNotFoundException;
import ktsnvt.tim1.exceptions.EntityNotValidException;
import ktsnvt.tim1.model.Location;
import ktsnvt.tim1.repositories.LocationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LocationServiceIntegrationTests {
    @Autowired
    private LocationService locationService;

    @Autowired
    private LocationRepository locationRepository;

    @Test
    void getLocations_pageRequestSent_locationsReturned() {
        int pageSize = 5;
        Pageable pageable = PageRequest.of(0, pageSize);
        Page<LocationDTO> page = locationService.getLocations(pageable);

        long totalLocationCount = locationRepository.count();

        if (totalLocationCount < pageSize)
            assertTrue(page.getTotalElements() < pageSize);
        else
            assertEquals(pageSize, page.getSize());
    }

    @Test
    void getLocation_locationExists_locationReturned() throws EntityNotFoundException {
        Long id = 1L;

        LocationDTO location = locationService.getLocation(id);

        assertEquals(id, location.getId());
    }

    @Test
    void getLocation_locationDoesNotExist_entityNotFoundExceptionThrown() {
        Long id = 31L;
        assertThrows(EntityNotFoundException.class, () -> locationService.getLocation(id));
    }

    @Test
    void searchLocations_searchParameterProvided_locationsReturned() {
        Pageable pageable = PageRequest.of(0, 5);
        String name = "black";

        Page<LocationDTO> searchResults = locationService.searchLocations(name, pageable); // should return Blackpool
        // and Blackburn

        for (LocationDTO location : searchResults.getContent())
            assertTrue(location.getName().toLowerCase().contains(name));
    }

    @Test
    void searchLocations_searchParameterEmpty_locationsReturned() {
        Pageable pageable = PageRequest.of(0, 5);
        String name = "";

        Page<LocationDTO> searchResults = locationService.searchLocations(name, pageable);

        long totalLocationsCount = locationRepository.count();
        assertEquals(totalLocationsCount, searchResults.getTotalElements());
    }

    @Test
    void searchLocations_searchParameterDoesNotMatchAnyLocation_emptyPageReturned() {
        Pageable pageable = PageRequest.of(0, 5);
        String name = "dsadahfghbfghvcs";

        Page<LocationDTO> searchResults = locationService.searchLocations(name, pageable);

        assertTrue(searchResults.isEmpty());
    }

    @Transactional
    @Rollback
    @Test
    void createLocation_locationCreated() {
        LocationDTO newDTO = new LocationDTO(null, "Spens", 50.0, 30.0, false);

        LocationDTO newLocationSaved = locationService.createLocation(newDTO);

        assertEquals(newDTO.getName(), newLocationSaved.getName());
        assertEquals(newDTO.getLongitude(), newLocationSaved.getLongitude());
        assertEquals(newDTO.getLatitude(), newLocationSaved.getLatitude());
        assertEquals(newDTO.isDisabled(), newLocationSaved.isDisabled());
        assertNotEquals(null, newLocationSaved.getId());
    }

    @Transactional
    @Rollback
    @Test
    void editLocation_locationIdIsNull_entityNotValidExceptionThrown() {
        LocationDTO editedDTO = new LocationDTO(null, "Spens", 50.0, 60.0, false);
        assertThrows(EntityNotValidException.class, () -> locationService.editLocation(editedDTO));
    }

    @Transactional
    @Rollback
    @Test
    void editLocation_locationDoesNotExist_entityNotFoundExceptionThrown() {
        Long id = 31L;
        LocationDTO editedDTO = new LocationDTO(id, "Spens", 50.0, 60.0, false);

        assertThrows(EntityNotFoundException.class, () -> locationService.editLocation(editedDTO));
    }

    @Transactional
    @Rollback
    @Test
    void editLocation_locationExists_locationEditedAndReturned() throws EntityNotValidException,
            EntityNotFoundException {
        LocationDTO editedDTO = new LocationDTO(30L, "Spens", 50.0, 60.0, false);

        LocationDTO editedLocation = locationService.editLocation(editedDTO);

        assertEquals(editedDTO.getId(), editedLocation.getId());
        assertEquals(editedDTO.getLatitude(), editedLocation.getLatitude());
        assertEquals(editedDTO.getLongitude(), editedLocation.getLongitude());
        assertEquals(editedDTO.getName(), editedLocation.getName());
        assertEquals(editedDTO.isDisabled(), editedLocation.isDisabled());
    }

    @Test
    void getSeatGroups_locationExists_pageReturned() throws EntityNotFoundException {
        Long id = 1L;
        Optional<Location> locationOptional = locationRepository.findById(id);

        Location location = null;
        if (locationOptional.isPresent())
            location = locationOptional.get();

        assertNotNull(location);

        int numberOfSeatGroups = location.getSeatGroups().size();
        Page<SeatGroupDTO> page = locationService.getSeatGroups(id, PageRequest.of(0, 5));

        assertEquals(numberOfSeatGroups, page.getTotalElements());
    }

    @Test
    void getSeatGroups_locationDoesNotExist_entityNotFoundExceptionThrown() {
        Long id = 31L;

        assertThrows(EntityNotFoundException.class, () -> locationService.getSeatGroups(id, PageRequest.of(0, 5)));
    }

    @Test
    void getSeatGroup_locationExistsAndSeatGroupExists_seatGroupReturned() throws EntityNotFoundException {
        Long locationId = 1L;
        Long seatGroupId = 1L;

        SeatGroupDTO returnedValue = locationService.getSeatGroup(locationId, seatGroupId);

        assertEquals(seatGroupId, returnedValue.getId());
    }

    @Test
    void getSeatGroup_locationExistsAndSeatGroupDoesNotExist_entityNotFoundExceptionThrown() {
        Long locationId = 1L;
        Long seatGroupId = 51L;

        assertThrows(EntityNotFoundException.class, () -> locationService.getSeatGroup(locationId, seatGroupId));
    }

    @Test
    void getSeatGroup_locationDoesNotExist_entityNotFoundExceptionThrown() {
        Long locationId = 31L;
        Long seatGroupId = 1L;

        assertThrows(EntityNotFoundException.class, () -> locationService.getSeatGroup(locationId, seatGroupId));
    }

    @Transactional
    @Rollback
    @Test
    void createSeatGroup_locationExistsAndSeatGroupIsValid_seatGroupCreated() throws EntityNotValidException, EntityNotFoundException {
        Long locationId = 1L;
        Optional<Location> locationOptional = locationRepository.findById(locationId);
        Location l = null;

        if (locationOptional.isPresent())
            l = locationOptional.get();

        assertNotNull(l);

        int startingNumberOfSeatGroups = l.getSeatGroups().size();

        SeatGroupDTO newDTO = new SeatGroupDTO();
        newDTO.setParterre(false);
        newDTO.setColsNum(1);
        newDTO.setRowsNum(3);
        newDTO.setxCoordinate(3.0);
        newDTO.setyCoordinate(4.0);

        SeatGroupDTO returnValue = locationService.createSeatGroup(locationId, newDTO);

        locationOptional = locationRepository.findById(locationId);
        if (locationOptional.isPresent())
            l = locationOptional.get();

        assertNotNull(l);

        assertNotEquals(null, returnValue.getId());
        assertEquals(newDTO.getColsNum(), returnValue.getColsNum());
        assertEquals(newDTO.getRowsNum(), returnValue.getRowsNum());
        assertEquals(newDTO.getRowsNum() * newDTO.getColsNum(), returnValue.getTotalSeats().intValue());
        assertEquals(newDTO.isParterre(), returnValue.isParterre());

        assertEquals(startingNumberOfSeatGroups + 1, l.getSeatGroups().size());
    }

    @Transactional
    @Rollback
    @Test
    void createSeatGroup_locationExistsAndSeatGroupIsNotValid_entityNotValidExceptionThrown() {
        Long locationId = 1L;

        SeatGroupDTO newDTO = new SeatGroupDTO();
        newDTO.setParterre(true);
        newDTO.setColsNum(1);
        newDTO.setRowsNum(3);
        newDTO.setxCoordinate(3.0);
        newDTO.setyCoordinate(4.0);

        assertThrows(EntityNotValidException.class, () -> locationService.createSeatGroup(locationId, newDTO));
    }

    @Transactional
    @Rollback
    @Test
    void createSeatGroup_locationDoesNotExist_entityNotFoundExceptionThrown() {
        Long locationId = 31L;
        SeatGroupDTO newDTO = new SeatGroupDTO();
        newDTO.setParterre(true);
        newDTO.setTotalSeats(30);
        newDTO.setxCoordinate(3.0);
        newDTO.setyCoordinate(4.0);

        assertThrows(EntityNotFoundException.class, () -> locationService.createSeatGroup(locationId, newDTO));
    }


}
