package ktsnvt.tim1.services;

import ktsnvt.tim1.DTOs.LocationDTO;
import ktsnvt.tim1.DTOs.SeatGroupDTO;
import ktsnvt.tim1.exceptions.EntityNotFoundException;
import ktsnvt.tim1.exceptions.EntityNotValidException;
import ktsnvt.tim1.mappers.LocationMapper;
import ktsnvt.tim1.mappers.SeatGroupMapper;
import ktsnvt.tim1.model.Location;
import ktsnvt.tim1.model.SeatGroup;
import ktsnvt.tim1.repositories.LocationRepository;
import ktsnvt.tim1.repositories.SeatGroupRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
public class LocationServiceUnitTests {
    @Autowired
    private LocationService locationService;

    @MockBean
    private LocationRepository locationRepositoryMocked;

    @MockBean
    private LocationMapper locationMapperMocked;

    @MockBean
    private SeatGroupRepository seatGroupRepositoryMocked;

    @MockBean
    private SeatGroupMapper seatGroupMapperMocked;

    @Test
    void getLocations_repositoryMethodCalledOnce() {
        Pageable pageable = PageRequest.of(0, 5);
        Mockito.when(locationRepositoryMocked.findAll(pageable)).thenReturn(Page.empty());
        locationService.getLocations(pageable);
        verify(locationRepositoryMocked, times(1)).findAll(pageable);
    }

    @Test
    void getLocation_locationExists_locationReturned() throws EntityNotFoundException {
        Long id = 1L;
        Location entity = new Location(id, "Spens", 50.0, 50.0, false);

        LocationDTO returnDTO = new LocationDTO(id, "Spens", 50.0, 50.0, false);
        Optional<Location> o = Optional.of(entity);

        Mockito.when(locationRepositoryMocked.findById(entity.getId())).thenReturn(o);
        Mockito.when(locationMapperMocked.toDTO(entity)).thenReturn(returnDTO);
        LocationDTO location = locationService.getLocation(id);

        assertEquals(id, location.getId());
        verify(locationRepositoryMocked, times(1)).findById(id);
    }

    @Test
    void getLocation_locationDoesNotExist_entityNotFoundExceptionThrown() {
        Long id = 1L;
        Mockito.when(locationRepositoryMocked.findById(id)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> locationService.getLocation(id));
    }

    @Test
    void searchLocations_repositoryMethodCalledOnce() {
        Pageable pageable = PageRequest.of(0, 5);
        String name = "a";
        Mockito.when(locationRepositoryMocked.findByNameIgnoreCaseContaining(name, pageable)).thenReturn(Page.empty());
        locationService.searchLocations(name, pageable);
        verify(locationRepositoryMocked, times(1)).findByNameIgnoreCaseContaining(name, pageable);
    }

    @Test
    void createLocation_locationCreated() {
        LocationDTO newDTO = new LocationDTO(null, "Spens", 50.0, 30.0, false);

        Location entityBeforeSaving = new Location(null, newDTO.getName(), newDTO.getLongitude(),
                newDTO.getLatitude(), newDTO.isDisabled());

        Long id = 1L;

        Location entityAfterSaving = new Location(id, newDTO.getName(), newDTO.getLongitude(),
                newDTO.getLatitude(), newDTO.isDisabled());

        LocationDTO returnDTO = new LocationDTO(id, newDTO.getName(), newDTO.getLongitude(),
                newDTO.getLatitude(), newDTO.isDisabled());

        Mockito.when(locationMapperMocked.toEntity(newDTO)).thenReturn(entityBeforeSaving);
        Mockito.when(locationRepositoryMocked.save(entityBeforeSaving)).thenReturn(entityAfterSaving);
        Mockito.when(locationMapperMocked.toDTO(entityAfterSaving)).thenReturn(returnDTO);

        LocationDTO newLocationSaved = locationService.createLocation(newDTO);

        assertEquals(id, newLocationSaved.getId());
        verify(locationMapperMocked, times(1)).toEntity(newDTO);
        verify(locationRepositoryMocked, times(1)).save(entityBeforeSaving);
        verify(locationMapperMocked, times(1)).toDTO(entityAfterSaving);
    }

    @Test
    void editLocation_locationIdIsNull_entityNotValidExceptionThrown() {
        LocationDTO editedDTO = new LocationDTO(null, "Spens", 50.0, 60.0, false);
        assertThrows(EntityNotValidException.class, () -> locationService.editLocation(editedDTO));
    }

    @Test
    void editLocation_locationDoesNotExist_entityNotFoundExceptionThrown() {
        Long id = 1L;
        LocationDTO editedDTO = new LocationDTO(id, "Spens", 50.0, 60.0, false);

        Mockito.when(locationRepositoryMocked.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> locationService.editLocation(editedDTO));
    }

    @Test
    void editLocation_locationExists_locationReturned() throws EntityNotValidException, EntityNotFoundException {
        Location oldEntity = new Location(1L, "Spens", 50.0, 60.0, false);
        LocationDTO editedDTO = new LocationDTO(1L, "Spensd", 50.0, 61.0, true);
        Location newEntity = new Location(1L, "Spensd", 50.0, 61.0, true);

        Mockito.when(locationRepositoryMocked.findById(editedDTO.getId())).thenReturn(Optional.of(oldEntity));
        Mockito.when(locationRepositoryMocked.save(oldEntity)).thenReturn(newEntity);
        Mockito.when(locationMapperMocked.toDTO(newEntity)).thenReturn(editedDTO);

        LocationDTO editedLocation = locationService.editLocation(editedDTO);

        assertEquals(editedDTO.getId(), editedLocation.getId());
        assertEquals(editedDTO.getLatitude(), editedLocation.getLatitude());
        assertEquals(editedDTO.getLongitude(), editedLocation.getLongitude());
        assertEquals(editedDTO.getName(), editedLocation.getName());

        verify(locationRepositoryMocked, times(1)).findById(editedDTO.getId());
        verify(locationRepositoryMocked, times(1)).save(oldEntity);
        verify(locationMapperMocked, times(1)).toDTO(newEntity);
    }

    @Test
    void getSeatGroups_locationExists_pageReturned() throws EntityNotFoundException {
        Long id = 1L;
        Location l = new Location(1L, "Spens", 50.0, 60.0, false);
        int numberOfSeatGroups = 5;
        HashSet<SeatGroup> seatGroups = new HashSet<>();

        for (int i = 0; i < numberOfSeatGroups; i++) {
            SeatGroup sg = new SeatGroup();
            seatGroups.add(sg);
            Mockito.when(seatGroupMapperMocked.toDTO(sg)).thenReturn(new SeatGroupDTO());
        }
        l.setSeatGroups(seatGroups);

        Mockito.when(locationRepositoryMocked.findById(id)).thenReturn(Optional.of(l));

        Page page = locationService.getSeatGroups(id, PageRequest.of(0, 5));

        assertEquals(numberOfSeatGroups, page.getSize());

        for (SeatGroup sg : seatGroups) {
            verify(seatGroupMapperMocked, times(1)).toDTO(sg);
        }
    }

    @Test
    void getSeatGroups_locationDoesNotExist_entityNotFoundExceptionThrown() {
        Long id = 1L;
        Mockito.when(locationRepositoryMocked.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> locationService.getSeatGroups(id, PageRequest.of(0, 5)));
    }

    @Test
    void getSeatGroup_locationExistsAndSeatGroupExists_seatGroupReturned() throws EntityNotFoundException {
        Long locationId = 1L;
        Long seatGroupId = 2L;

        Location l = new Location(locationId, "Spens", 50.0, 50.0, false);
        SeatGroup sg = new SeatGroup();
        sg.setId(seatGroupId);
        l.getSeatGroups().add(sg);

        SeatGroupDTO seatGroupDTO = new SeatGroupDTO();
        seatGroupDTO.setId(seatGroupId);

        Mockito.when(locationRepositoryMocked.findById(locationId)).thenReturn(Optional.of(l));
        Mockito.when(seatGroupMapperMocked.toDTO(sg)).thenReturn(seatGroupDTO);

        SeatGroupDTO returnedValue = locationService.getSeatGroup(locationId, seatGroupId);

        assertEquals(seatGroupId, returnedValue.getId());
        verify(locationRepositoryMocked, times(1)).findById(locationId);
        verify(seatGroupMapperMocked, times(1)).toDTO(sg);
    }

    @Test
    void getSeatGroup_locationExistsAndSeatGroupDoesNotExist_entityNotFoundExceptionThrown() {
        Long locationId = 1L;
        Long seatGroupId = 2L;

        Location l = new Location(locationId, "Spens", 50.0, 50.0, false);

        Mockito.when(locationRepositoryMocked.findById(locationId)).thenReturn(Optional.of(l));

        assertThrows(EntityNotFoundException.class, () -> locationService.getSeatGroup(locationId, seatGroupId));
    }

    @Test
    void getSeatGroup_locationDoesNotExist_entityNotFoundExceptionThrown() {
        Long locationId = 1L;
        Long seatGroupId = 2L;

        Mockito.when(locationRepositoryMocked.findById(locationId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> locationService.getSeatGroup(locationId, seatGroupId));
    }

    @Test
    void createSeatGroup_locationExistsAndSeatGroupIsValid_seatGroupCreated() throws EntityNotValidException, EntityNotFoundException {
        Long locationId = 1L;
        Long seatGroupId = 2L;

        Location l = new Location(locationId, "Spens", 50.0, 50.0, false);
        SeatGroup sg = new SeatGroup();
        sg.setId(seatGroupId);

        SeatGroup sgBeforeSave = new SeatGroup();

        SeatGroupDTO newDTO = new SeatGroupDTO();
        SeatGroupDTO returnedDTO = new SeatGroupDTO();
        returnedDTO.setId(seatGroupId);

        Mockito.when(locationRepositoryMocked.findById(locationId)).thenReturn(Optional.of(l));
        Mockito.when(seatGroupMapperMocked.toEntity(newDTO)).thenReturn(sgBeforeSave);
        Mockito.when(seatGroupRepositoryMocked.save(sgBeforeSave)).thenReturn(sg);
        Mockito.when(seatGroupMapperMocked.toDTO(sg)).thenReturn(returnedDTO);

        SeatGroupDTO returnValue = locationService.createSeatGroup(locationId, newDTO);

        assertEquals(seatGroupId, returnValue.getId());
        verify(locationRepositoryMocked, times(1)).findById(locationId);
        verify(seatGroupMapperMocked, times(1)).toEntity(newDTO);
        verify(seatGroupRepositoryMocked, times(1)).save(sgBeforeSave);
        verify(seatGroupMapperMocked, times(1)).toDTO(sg);
        assertEquals(1, l.getSeatGroups().size());
    }

    @Test
    void createSeatGroup_locationExistsAndSeatGroupIsNotValid_entityNotValidExceptionThrown() throws EntityNotValidException {
        Long locationId = 1L;

        Location l = new Location(locationId, "Spens", 50.0, 50.0, false);

        SeatGroupDTO newDTO = new SeatGroupDTO();

        Mockito.when(locationRepositoryMocked.findById(locationId)).thenReturn(Optional.of(l));
        Mockito.when(seatGroupMapperMocked.toEntity(newDTO)).thenThrow(EntityNotValidException.class);

        assertThrows(EntityNotValidException.class, () -> locationService.createSeatGroup(locationId, newDTO));
    }

    @Test
    void createSeatGroup_locationDoesNotExist_entityNotFoundExceptionThrown() {
        Long locationId = 1L;
        SeatGroupDTO newDTO = new SeatGroupDTO();

        Mockito.when(locationRepositoryMocked.findById(locationId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> locationService.createSeatGroup(locationId, newDTO));
    }

    @Test
    void getLocationsOptions_repositoryMethodCalledOnce() {
        Mockito.when(locationRepositoryMocked.findAll()).thenReturn(new ArrayList<>());
        locationService.getLocationsOptions();
        verify(locationRepositoryMocked, times(1)).findAll();
    }

    @Test
    void editSeatGroupPosition_locationAndSeatGroupExist_seatGroupPositionChanged() throws EntityNotFoundException {
        Long locationId = 1L;
        Long seatGroupId = 1L;

        Double newAngle = 0.0;
        Double newXCoordinate = 0.0;
        Double newYCoordinate = 0.0;

        Location l = new Location(locationId, "Spens", 50.0, 50.0, false);
        SeatGroup seatGroupBeforeChange = new SeatGroup(3, 3, false, 13.0, 10.0, 30.0, "SG", 9, l);
        seatGroupBeforeChange.setId(seatGroupId);
        l.getSeatGroups().add(seatGroupBeforeChange);
        SeatGroupDTO changedSeatGroup = new SeatGroupDTO(seatGroupId, 3, 3, false, newXCoordinate, newYCoordinate, 9,
                "SG", newAngle);

        Mockito.when(locationRepositoryMocked.findById(locationId)).thenReturn(Optional.of(l));
        Mockito.when(seatGroupMapperMocked.toDTO(seatGroupBeforeChange)).thenReturn(changedSeatGroup);

        SeatGroupDTO result = locationService.editSeatGroupPosition(seatGroupId, changedSeatGroup);

        verify(locationRepositoryMocked, times(1)).findById(locationId);
        verify(locationRepositoryMocked, times(1)).save(l);
        verify(seatGroupMapperMocked, times(1)).toDTO(seatGroupBeforeChange);

        assertNotNull(result);
        assertEquals(newAngle, result.getAngle());
        assertEquals(newXCoordinate, result.getxCoordinate());
        assertEquals(newYCoordinate, result.getyCoordinate());
    }

    @Test
    void editSeatGroupPosition_locationDoesNotExist_entityNotFoundExceptionThrown() {
        Long locationId = 1L;
        SeatGroupDTO changedSeatGroup = new SeatGroupDTO();

        Mockito.when(locationRepositoryMocked.findById(locationId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> locationService.editSeatGroupPosition(locationId,
                changedSeatGroup));
    }

    @Test
    void editSeatGroupPosition_locationExistsAndSeatGroupDoesNotExist_entityNotFoundExceptionThrown() {
        Long locationId = 1L;
        Long seatGroupId = 1L;

        Double newAngle = 0.0;
        Double newXCoordinate = 0.0;
        Double newYCoordinate = 0.0;

        Location l = new Location(locationId, "Spens", 50.0, 50.0, false);
        SeatGroupDTO changedSeatGroup = new SeatGroupDTO(seatGroupId, 3, 3, false, newXCoordinate, newYCoordinate, 9,
                "SG", newAngle);

        Mockito.when(locationRepositoryMocked.findById(locationId)).thenReturn(Optional.of(l));

        assertThrows(EntityNotFoundException.class, () -> locationService.editSeatGroupPosition(locationId,
                changedSeatGroup));
    }
}
