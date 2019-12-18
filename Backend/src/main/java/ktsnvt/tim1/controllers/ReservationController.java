package ktsnvt.tim1.controllers;

import com.paypal.base.rest.PayPalRESTException;
import ktsnvt.tim1.DTOs.*;
import ktsnvt.tim1.exceptions.EntityNotFoundException;
import ktsnvt.tim1.exceptions.EntityNotValidException;
import ktsnvt.tim1.exceptions.ImpossibleActionException;
import ktsnvt.tim1.exceptions.PayPalException;
import ktsnvt.tim1.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping()
    public ResponseEntity<Page<ReservationDTO>> getReservations(@RequestParam("type") ReservationTypeDTO type, Pageable pageable) {
        return new ResponseEntity<>(reservationService.getReservations(type, pageable), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> getReservation(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<>(reservationService.getReservation(id), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping()
    public ResponseEntity<Object> createReservation(@Valid @RequestBody NewReservationDTO newReservationDTO) {
        try {
            return new ResponseEntity<>(reservationService.createReservation(newReservationDTO), HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (EntityNotValidException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (ImpossibleActionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Object> cancelReservation(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<>(reservationService.cancelReservation(id), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (ImpossibleActionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PostMapping(value = "/{id}")
    public ResponseEntity<Object> payReservationCreatePayment(@PathVariable("id") Long reservationId) {
        try {
            return new ResponseEntity<>(reservationService.payReservationCreatePayment(reservationId), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (ImpossibleActionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (PayPalRESTException | PayPalException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    @PostMapping(value = "/{id}/executePayment", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<Object> payReservationExecutePayment(@Valid PaymentDTO paymentDTO, @PathVariable("id") Long reservationId) {
        try {
            return new ResponseEntity<>(reservationService.payReservationExecutePayment(reservationId, paymentDTO), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (ImpossibleActionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (PayPalRESTException | PayPalException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

}
