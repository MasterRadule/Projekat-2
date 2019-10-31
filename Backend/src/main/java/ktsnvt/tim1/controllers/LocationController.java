package ktsnvt.tim1.controllers;

import ktsnvt.tim1.DTOs.LocationDTO;
import ktsnvt.tim1.exceptions.EntityNotFoundException;
import ktsnvt.tim1.services.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/locations")
public class LocationController {
    @Autowired
    private LocationService locationService;

    @GetMapping()
    public ResponseEntity<Page<LocationDTO>> getLocations(Pageable pageable) {
        return new ResponseEntity<>(locationService.getLocations(pageable), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> getLocation(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<>(locationService.getLocation(id), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Location not found", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping()
    public ResponseEntity<LocationDTO> createLocation(@Valid @RequestBody LocationDTO location) {
        return new ResponseEntity<>(locationService.createLocation(location), HttpStatus.CREATED);
    }
}
