package ktsnvt.tim1.controllers;

import ktsnvt.tim1.DTOs.LocationDTO;
import ktsnvt.tim1.DTOs.SeatGroupDTO;
import ktsnvt.tim1.exceptions.EntityNotFoundException;
import ktsnvt.tim1.exceptions.EntityNotValidException;
import ktsnvt.tim1.model.Location;
import ktsnvt.tim1.model.SeatGroup;
import ktsnvt.tim1.repositories.LocationRepository;
import ktsnvt.tim1.services.LocationService;
import ktsnvt.tim1.utils.RestResponsePage;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LocationControllerIntegrationTests {
    @LocalServerPort
    private int port;

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    LocationService locationService;

    @Autowired
    EntityManager entityManager;

    @SuppressWarnings("ConstantConditions")
    @Test
    public void getLocations_locationsReturned() {
        ParameterizedTypeReference<RestResponsePage<LocationDTO>> responseType = new ParameterizedTypeReference<RestResponsePage<LocationDTO>>() {
        };

        ResponseEntity<RestResponsePage<LocationDTO>> result = testRestTemplate.exchange(createURLWithPort(
                "/locations?page=0&size=5"), HttpMethod.GET, null, responseType);

        List<LocationDTO> locations = result.getBody().getContent();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(5, locations.size());
    }

    @Test
    public void getLocation_locationExists_locationReturned() {
        ResponseEntity<LocationDTO> result = testRestTemplate
                .exchange(createURLWithPort("/locations/1"), HttpMethod.GET,
                        null, LocationDTO.class);

        LocationDTO location = result.getBody();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(location);
        assertEquals(1L, location.getId().longValue());
    }

    @Test
    public void getLocation_locationDoesNotExist_errorMessageReturned() {
        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/locations/31"), HttpMethod.GET,
                null, String.class);

        String errorMessage = result.getBody();

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Location not found", errorMessage);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void searchLocations_searchParameterProvided_locationsReturned() {
        ParameterizedTypeReference<RestResponsePage<LocationDTO>> responseType = new ParameterizedTypeReference<RestResponsePage<LocationDTO>>() {
        };

        String name = "black";

        ResponseEntity<RestResponsePage<LocationDTO>> result = testRestTemplate.exchange(createURLWithPort(
                "/locations/search?page=0&size=5&name=black"), HttpMethod.GET, null, responseType);

        List<LocationDTO> locations = result.getBody().getContent();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(2, locations.size());

        for (LocationDTO location : locations) {
            assertTrue(location.getName().toLowerCase().contains(name));
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void searchLocations_searchParameterEmpty_locationsReturned() {
        ParameterizedTypeReference<RestResponsePage<LocationDTO>> responseType = new ParameterizedTypeReference<RestResponsePage<LocationDTO>>() {
        };

        ResponseEntity<RestResponsePage<LocationDTO>> result = testRestTemplate.exchange(createURLWithPort(
                "/locations/search?page=0&size=5&name="), HttpMethod.GET, null, responseType);

        List<LocationDTO> locations = result.getBody().getContent();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(5, locations.size());
        assertEquals(30, result.getBody().getTotalElements());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void searchLocations_searchParameterDoesNotMatchAnyLocation_emptyPageReturned() {
        ParameterizedTypeReference<RestResponsePage<LocationDTO>> responseType = new ParameterizedTypeReference<RestResponsePage<LocationDTO>>() {
        };

        ResponseEntity<RestResponsePage<LocationDTO>> result = testRestTemplate.exchange(createURLWithPort(
                "/locations/search?page=0&size=5&name=dsadahfghbfghvcs"), HttpMethod.GET, null, responseType);

        List<LocationDTO> locations = result.getBody().getContent();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(0, locations.size());
    }

    @Transactional
    @Rollback
    @Test
    public void createLocation_locationCreated() {
        LocationDTO newDTO = new LocationDTO(null, "Spens", 50.0, 30.0, false);

        long initialSize = locationRepository.count();

        HttpEntity<LocationDTO> entity = new HttpEntity<>(newDTO);

        ResponseEntity<LocationDTO> result = testRestTemplate.exchange(createURLWithPort("/locations"),
                HttpMethod.POST, entity, LocationDTO.class);

        LocationDTO location = result.getBody();

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(location);
        assertEquals(newDTO.getName(), location.getName());
        assertEquals(newDTO.getLongitude(), location.getLongitude());
        assertEquals(newDTO.getLatitude(), location.getLatitude());
        assertEquals(newDTO.isDisabled(), location.isDisabled());
        assertNotEquals(null, location.getId());

        Page<Location> locationPage = locationRepository.findAll(PageRequest.of(0, 5));
        assertEquals(initialSize + 1, locationPage.getTotalElements());
    }

    @Transactional
    @Rollback
    @Test
    public void editLocation_locationIdIsNull_errorMessageReturned() {
        LocationDTO editedDTO = new LocationDTO(null, "Spens", 50.0, 60.0, false);

        HttpEntity<LocationDTO> entity = new HttpEntity<>(editedDTO);

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/locations"),
                HttpMethod.PUT, entity, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Location must have an ID", result.getBody());
    }

    @Transactional
    @Rollback
    @Test
    public void editLocation_locationDoesNotExist_errorMessageReturned() {
        LocationDTO editedDTO = new LocationDTO(31L, "Spens", 50.0, 60.0, false);

        HttpEntity<LocationDTO> entity = new HttpEntity<>(editedDTO);

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/locations"),
                HttpMethod.PUT, entity, String.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Location not found", result.getBody());
    }

    @Transactional
    @Rollback
    @Test
    public void editLocation_locationExists_locationEditedAndReturned() {
        LocationDTO editedDTO = new LocationDTO(30L, "Spens", 50.0, 60.0, false);

        HttpEntity<LocationDTO> entity = new HttpEntity<>(editedDTO);

        ResponseEntity<LocationDTO> result = testRestTemplate.exchange(createURLWithPort("/locations"),
                HttpMethod.PUT, entity, LocationDTO.class);

        LocationDTO returnedDTO = result.getBody();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(returnedDTO);
        assertEquals(editedDTO.getId(), returnedDTO.getId());
        assertEquals(editedDTO.getName(), returnedDTO.getName());
        assertEquals(editedDTO.getLatitude(), returnedDTO.getLatitude());
        assertEquals(editedDTO.getLongitude(), returnedDTO.getLongitude());
    }

    @Test
    public void getSeatGroups_locationExists_pageReturned() {
        Long locationId = 1L;
        Optional<Location> location = locationRepository.findById(locationId);
        Integer seatGroupsCount = null;

        if (location.isPresent())
            seatGroupsCount = location.get().getSeatGroups().size();

        assertNotNull(seatGroupsCount);

        ParameterizedTypeReference<RestResponsePage<SeatGroupDTO>> responseType =
                new ParameterizedTypeReference<RestResponsePage<SeatGroupDTO>>() {
                };

        ResponseEntity<RestResponsePage<SeatGroupDTO>> result = testRestTemplate.exchange(createURLWithPort(
                "/locations/1/seat-groups"), HttpMethod.GET, null, responseType);

        Page<SeatGroupDTO> page = result.getBody();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(page);
        assertEquals(seatGroupsCount.intValue(), page.getTotalElements());
    }

    @Test
    void getSeatGroups_locationDoesNotExist_errorMessageReturned() {
        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/locations/31/seat-groups"),
                HttpMethod.GET, null, String.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Location not found", result.getBody());
    }

    @Test
    void getSeatGroup_locationExistsAndSeatGroupExists_seatGroupReturned() {
        Long locationId = 1L;
        Long seatGroupId = 1L;

        Optional<Location> l = locationRepository.findById(locationId);
        Location location = null;

        if (l.isPresent())
            location = l.get();

        ResponseEntity<SeatGroupDTO> result = testRestTemplate
                .exchange(createURLWithPort("/locations/1/seat-groups/1"), HttpMethod.GET, null, SeatGroupDTO.class);

        SeatGroupDTO seatGroup = result.getBody();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(seatGroup);
        assertEquals(seatGroupId, seatGroup.getId());
        assertNotNull(location);
        assertTrue(location.getSeatGroups().stream().map(SeatGroup::getId).collect(Collectors.toList())
                .contains(seatGroup.getId()));
    }

    @Test
    void getSeatGroup_locationExistsAndSeatGroupDoesNotExist_errorMessageReturned() {
        Long locationId = 1L;
        Long seatGroupId = 51L;

        Optional<Location> l = locationRepository.findById(locationId);
        Location location = null;

        if (l.isPresent())
            location = l.get();

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/locations/1/seat-groups/51"),
                HttpMethod.GET, null, String.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Seat group not found", result.getBody());
        assertNotNull(location);
        assertFalse(location.getSeatGroups().stream().map(SeatGroup::getId).collect(Collectors.toList())
                .contains(seatGroupId));
    }

    @Test
    void getSeatGroup_locationDoesNotExist_errorMessageReturned() {
        Long locationId = 31L;

        Optional<Location> l = locationRepository.findById(locationId);

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/locations/31/seat-groups/1"),
                HttpMethod.GET, null, String.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Location not found", result.getBody());
        assertFalse(l.isPresent());
    }

    @Transactional
    @Rollback
    @Test
    void createSeatGroup_locationExistsAndSeatGroupIsValid_seatGroupCreated() {
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
        newDTO.setName("Group1");

        HttpEntity<SeatGroupDTO> entity = new HttpEntity<>(newDTO);

        ResponseEntity<SeatGroupDTO> result = testRestTemplate
                .exchange(createURLWithPort("/locations/1/seat-groups"), HttpMethod.POST, entity, SeatGroupDTO.class);

        SeatGroupDTO returnedValue = result.getBody();

        Session session = (Session) entityManager.getDelegate();
        session.evict(l);

        locationOptional = locationRepository.findById(locationId);

        if (locationOptional.isPresent())
            l = locationOptional.get();

        assertNotNull(l);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(returnedValue);
        assertNotEquals(null, returnedValue.getId());
        assertEquals(newDTO.getColsNum(), returnedValue.getColsNum());
        assertEquals(newDTO.getRowsNum(), returnedValue.getRowsNum());
        assertEquals(newDTO.getRowsNum() * newDTO.getColsNum(), returnedValue.getTotalSeats().intValue());
        assertEquals(newDTO.isParterre(), returnedValue.isParterre());
        assertEquals(newDTO.getName(), returnedValue.getName());
        assertEquals(startingNumberOfSeatGroups + 1, l.getSeatGroups().size());
    }

    @Transactional
    @Rollback
    @Test
    void createSeatGroup_locationExistsAndSeatGroupIsNotValid_errorMessageReturned() {
        SeatGroupDTO newDTO = new SeatGroupDTO();
        newDTO.setParterre(true);
        newDTO.setColsNum(1);
        newDTO.setRowsNum(3);
        newDTO.setxCoordinate(3.0);
        newDTO.setyCoordinate(4.0);
        newDTO.setName("Group1");

        HttpEntity<SeatGroupDTO> entity = new HttpEntity<>(newDTO);

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/locations/1/seat-groups"),
                HttpMethod.POST, entity, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Invalid value for parterre's total seats.", result.getBody());
    }

    @Transactional
    @Rollback
    @Test
    void createSeatGroup_locationDoesNotExist_errorMessageReturned() {
        Long locationId = 31L;
        Optional<Location> l = locationRepository.findById(locationId);

        SeatGroupDTO newDTO = new SeatGroupDTO();
        newDTO.setParterre(true);
        newDTO.setTotalSeats(30);
        newDTO.setxCoordinate(3.0);
        newDTO.setyCoordinate(4.0);
        newDTO.setName("Group1");

        HttpEntity<SeatGroupDTO> entity = new HttpEntity<>(newDTO);

        ResponseEntity<String> result = testRestTemplate
                .exchange(createURLWithPort("/locations/31/seat-groups"), HttpMethod.POST, entity, String.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Location not found", result.getBody());
        assertFalse(l.isPresent());
    }


    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + "/api" + uri;
    }


}
