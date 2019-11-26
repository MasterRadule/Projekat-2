package ktsnvt.tim1.services;

import ktsnvt.tim1.DTOs.LocationDTO;
import ktsnvt.tim1.exceptions.EntityNotFoundException;
import ktsnvt.tim1.exceptions.EntityNotValidException;
import ktsnvt.tim1.mappers.LocationMapper;
import ktsnvt.tim1.model.Location;
import ktsnvt.tim1.repositories.LocationRepository;
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

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class LocationServiceUnitTests {
    @Autowired
    private LocationService locationService;

    @MockBean
    private LocationRepository locationRepositoryMocked;

    @MockBean
    private LocationMapper locationMapperMocked;

    @Test
    void getLocations_repositoryMethodCalledOnce() {
        Pageable pageable = PageRequest.of(0, 5);
        Mockito.when(locationRepositoryMocked.findAll(pageable)).thenReturn(Page.empty());
        locationService.getLocations(pageable);
        verify(locationRepositoryMocked, times(1)).findAll(pageable);
    }

    @Test
    void getLocation_locationExists_LocationReturned() throws EntityNotFoundException {
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
    void getLocation_locationDoesNotExist_EntityNotFoundExceptionThrown() {
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
    void editLocation_locationIdIsNull_EntityNotValidExceptionThrown() {
        LocationDTO editedDTO = new LocationDTO(null, "Spens", 50.0, 60.0, false);
        assertThrows(EntityNotValidException.class, () -> locationService.editLocation(editedDTO));
    }

    @Test
    void editLocation_locationDoesNotExist_EntityNotFoundExceptionThrown() {
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
    }


}
