package ktsnvt.tim1.controllers;

import ktsnvt.tim1.DTOs.EventDTO;
import ktsnvt.tim1.DTOs.LocationSeatGroupDTO;
import ktsnvt.tim1.DTOs.SearchEventsDTO;
import ktsnvt.tim1.exceptions.EntityAlreadyExistsException;
import ktsnvt.tim1.exceptions.EntityNotFoundException;
import ktsnvt.tim1.exceptions.EntityNotValidException;
import ktsnvt.tim1.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("/events")
@CrossOrigin
public class EventController {
    @Autowired
    private EventService eventService;

    @GetMapping()
    public ResponseEntity<Page<EventDTO>> getEvents(Pageable pageable) {
        return new ResponseEntity<>(eventService.getEvents(pageable), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> getEvent(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<>(eventService.getEvent(id), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping()
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Object> createEvent(@Valid @RequestBody EventDTO event) {
        try {
            return new ResponseEntity<>(eventService.createEvent(event), HttpStatus.CREATED);
        }
        catch(EntityNotValidException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/{id}/pictures-and-videos")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Object> uploadEventsPicturesAndVideos(@PathVariable("id") Long id, @RequestParam("files") MultipartFile[] files) {
        try {
            eventService.uploadPicturesAndVideos(id, files);
            return new ResponseEntity<>("Files uploaded successfully", HttpStatus.OK);
        } catch (EntityNotValidException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/{id}/pictures-and-videos")
    public ResponseEntity<Object> getEventsPicturesAndVideos(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<>(eventService.getPicturesAndVideos(id), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "{eventID}/pictures-and-videos/{fileID}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Object> deleteMediaFile(@PathVariable("eventID") Long eventID, @PathVariable("fileID") Long fileID) {
        try {
            eventService.deleteMediaFile(eventID, fileID);
            return new ResponseEntity<>("File deleted successfully", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping()
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Object> editEvent(@Valid @RequestBody EventDTO event) {
        try {
            return new ResponseEntity<>(eventService.editEvent(event), HttpStatus.OK);
        }
        catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        catch (EntityNotValidException | EntityAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchEvents(SearchEventsDTO searchDTO, Pageable pageable) {
        try {
            return new ResponseEntity<>(eventService.searchEvents(searchDTO, pageable), HttpStatus.OK);
        }
        catch (EntityNotValidException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/location")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Object> setEventLocationAndSeatGroups(@Valid @RequestBody LocationSeatGroupDTO locSeatGroupDTO) {
        try {
            return new ResponseEntity<>(eventService.setEventLocationAndSeatGroups(locSeatGroupDTO), HttpStatus.OK);
        }
        catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        catch (EntityNotValidException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
