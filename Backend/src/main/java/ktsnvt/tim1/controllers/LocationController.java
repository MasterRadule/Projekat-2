package ktsnvt.tim1.controllers;

import ktsnvt.tim1.DTOs.LocationDTO;
import ktsnvt.tim1.DTOs.LocationOptionDTO;
import ktsnvt.tim1.DTOs.SeatGroupDTO;
import ktsnvt.tim1.exceptions.EntityNotFoundException;
import ktsnvt.tim1.exceptions.EntityNotValidException;
import ktsnvt.tim1.services.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/locations")
@CrossOrigin
public class LocationController {
    @Autowired
    private LocationService locationService;

    @GetMapping
    public ResponseEntity<Page<LocationDTO>> getLocations(Pageable pageable) {
        return new ResponseEntity<>(locationService.getLocations(pageable), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> getLocation(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<>(locationService.getLocation(id), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "options")
    public ResponseEntity<List<LocationOptionDTO>> getLocationsOptions() {
        return new ResponseEntity<>(locationService.getLocationsOptions(), HttpStatus.OK);
    }

    @GetMapping(value = "search")
    public ResponseEntity<Page<LocationDTO>> searchLocations(@RequestParam("name") String name, Pageable pageable) {
        return new ResponseEntity<>(locationService.searchLocations(name, pageable), HttpStatus.OK);
    }

    @PostMapping
    @Secured("ROLE_ADMIN")
    public ResponseEntity<LocationDTO> createLocation(@Valid @RequestBody LocationDTO location) {
        return new ResponseEntity<>(locationService.createLocation(location), HttpStatus.CREATED);
    }

    @PutMapping
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Object> editLocation(@Valid @RequestBody LocationDTO location) {
        try {
            return new ResponseEntity<>(locationService.editLocation(location), HttpStatus.OK);
        } catch (EntityNotValidException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/{id}/seat-groups")
    public ResponseEntity<Object> getSeatGroups(@PathVariable("id") Long id, Pageable pageable) {
        try {
            return new ResponseEntity<>(locationService.getSeatGroups(id, pageable), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/{locationId}/seat-groups/{seatGroupId}")
    public ResponseEntity<Object> getSeatGroup(@PathVariable("locationId") Long locationId, @PathVariable("seatGroupId") Long seatGroupId) {
        try {
            return new ResponseEntity<>(locationService.getSeatGroup(locationId, seatGroupId), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/{id}/seat-groups")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Object> createSeatGroup(@PathVariable("id") Long id, @Valid @RequestBody SeatGroupDTO seatGroup) {
        try {
            return new ResponseEntity<>(locationService.createSeatGroup(id, seatGroup), HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (EntityNotValidException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/{id}/seat-groups")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Object> editSeatGroupPosition(@PathVariable("id") Long id, @Valid @RequestBody SeatGroupDTO seatGroupDTO) {
        try {
            return new ResponseEntity<>(locationService.editSeatGroupPosition(id, seatGroupDTO), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
