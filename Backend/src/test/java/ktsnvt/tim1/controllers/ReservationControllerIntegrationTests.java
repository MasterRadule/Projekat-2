package ktsnvt.tim1.controllers;

import ktsnvt.tim1.DTOs.*;
import ktsnvt.tim1.utils.HeaderTokenGenerator;
import ktsnvt.tim1.utils.RestResponsePage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ReservationControllerIntegrationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private HeaderTokenGenerator headerTokenGenerator;

    @AfterEach
    public void rollback() {
        Resource resource = new ClassPathResource("data-h2.sql");
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator(resource);
        resourceDatabasePopulator.execute(dataSource);
    }

    @Test
    public void getReservations_parameterALL_allReturned() {
        HttpHeaders headers = headerTokenGenerator.generateHeaderWithToken("JennifferHooker@example.com");

        ParameterizedTypeReference<RestResponsePage<ReservationDTO>> responseType = new ParameterizedTypeReference<RestResponsePage<ReservationDTO>>() {
        };

        ResponseEntity<RestResponsePage<ReservationDTO>> result = testRestTemplate.exchange(createURLWithPort(
                "/reservations?type=ALL&$page=0&size=5"), HttpMethod.GET, new HttpEntity<>(headers), responseType);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(2, result.getBody().getTotalElements());
        assertEquals(2, result.getBody().getNumberOfElements());
    }

    @Test
    public void getReservations_parameterBOUGHT_boughtReturned() {
        HttpHeaders headers = headerTokenGenerator.generateHeaderWithToken("JennifferHooker@example.com");

        ParameterizedTypeReference<RestResponsePage<ReservationDTO>> responseType = new ParameterizedTypeReference<RestResponsePage<ReservationDTO>>() {
        };

        ResponseEntity<RestResponsePage<ReservationDTO>> result = testRestTemplate.exchange(createURLWithPort(
                "/reservations?type=BOUGHT&$page=0&size=5"), HttpMethod.GET, new HttpEntity<>(headers), responseType);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().getTotalElements());
        assertEquals(1, result.getBody().getNumberOfElements());
    }

    @Test
    public void getReservations_parameterRESERVED_reservedReturned() {
        HttpHeaders headers = headerTokenGenerator.generateHeaderWithToken("JennifferHooker@example.com");

        ParameterizedTypeReference<RestResponsePage<ReservationDTO>> responseType = new ParameterizedTypeReference<RestResponsePage<ReservationDTO>>() {
        };

        ResponseEntity<RestResponsePage<ReservationDTO>> result = testRestTemplate.exchange(createURLWithPort(
                "/reservations?type=RESERVED&$page=0&size=5"), HttpMethod.GET, new HttpEntity<>(headers), responseType);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().getTotalElements());
        assertEquals(1, result.getBody().getNumberOfElements());
    }

    @Test
    public void getReservations_parameterEmpty_typeIsEmpty() {
        HttpHeaders headers = headerTokenGenerator.generateHeaderWithToken("JennifferHooker@example.com");

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort(
                "/reservations?type=&$page=0&size=5"), HttpMethod.GET, new HttpEntity<>(headers), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Type is empty", result.getBody());
    }

    @Test
    public void getReservations_userHasNoUncancelledReservations_emptyReturned() {
        HttpHeaders headers = headerTokenGenerator.generateHeaderWithToken("ogqojyxj5136@example.com");

        ParameterizedTypeReference<RestResponsePage<ReservationDTO>> responseType = new ParameterizedTypeReference<RestResponsePage<ReservationDTO>>() {
        };

        ResponseEntity<RestResponsePage<ReservationDTO>> result = testRestTemplate.exchange(createURLWithPort(
                "/reservations?type=RESERVED&$page=0&size=5"), HttpMethod.GET, new HttpEntity<>(headers), responseType);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(0, result.getBody().getTotalElements());
        assertEquals(0, result.getBody().getNumberOfElements());
    }


    @Test
    public void getReservation_reservationExists_reservationReturned() {
        HttpHeaders headers = headerTokenGenerator.generateHeaderWithToken("JennifferHooker@example.com");

        Long reservationId = 1L;

        ResponseEntity<ReservationDTO> result = testRestTemplate.exchange(createURLWithPort(
                "/reservations/" + reservationId), HttpMethod.GET, new HttpEntity<>(headers), ReservationDTO.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(reservationId, result.getBody().getId());
    }

    @Test
    public void getReservation_reservationDoesNotExist_entityNotFoundExceptionThrown() {
        HttpHeaders headers = headerTokenGenerator.generateHeaderWithToken("JennifferHooker@example.com");

        long reservationId = 100L;

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort(
                "/reservations/" + reservationId), HttpMethod.GET, new HttpEntity<>(headers), String.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Reservation not found", result.getBody());
    }


    @Test
    public void createReservation_validSeatTicketSingleDay_reservationCreated() {
        HttpHeaders headers = headerTokenGenerator.generateHeaderWithToken("JennifferHooker@example.com");

        NewTicketDTO newTicketDTO = new NewTicketDTO(1L, 2L, false);
        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(newTicketDTO);
        NewReservationDTO newReservationDTO = new NewReservationDTO(1L, tickets);


        ResponseEntity<ReservationDTO> result = testRestTemplate.exchange(createURLWithPort(
                "/reservations"), HttpMethod.POST, new HttpEntity<>(newReservationDTO, headers), ReservationDTO.class);
        assertNotNull(result.getBody());
        ReservationDTO reservationDTO = result.getBody();

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNull(reservationDTO.getOrderId());
        assertEquals("Conputor", reservationDTO.getEventName());
        assertEquals(Long.valueOf(1L), reservationDTO.getEventId());
        assertEquals(1, reservationDTO.getTickets().size());

        TicketDTO ticketDTO = reservationDTO.getTickets().get(0);
        assertEquals(Integer.valueOf(1), ticketDTO.getColNum());
        assertEquals(Integer.valueOf(2), ticketDTO.getRowNum());
        assertEquals("Side", ticketDTO.getSeatGroupName());
        assertEquals(Double.valueOf(31), ticketDTO.getPrice());
        assertEquals(1, ticketDTO.getEventDays().size());
        assertEquals(LocalDateTime.of(2020, Month.MARCH, 1, 0, 0), ticketDTO.getEventDays().get(0));
    }


    @Test
    public void createReservation_validSeatTicketAllDays_reservationCreated() {
        HttpHeaders headers = headerTokenGenerator.generateHeaderWithToken("JennifferHooker@example.com");

        NewTicketDTO newTicketDTO = new NewTicketDTO(1L, 2L, true);
        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(newTicketDTO);
        NewReservationDTO newReservationDTO = new NewReservationDTO(1L, tickets);

        ResponseEntity<ReservationDTO> result = testRestTemplate.exchange(createURLWithPort(
                "/reservations"), HttpMethod.POST, new HttpEntity<>(newReservationDTO, headers), ReservationDTO.class);
        assertNotNull(result.getBody());
        ReservationDTO reservationDTO = result.getBody();

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNull(reservationDTO.getOrderId());
        assertEquals("Conputor", reservationDTO.getEventName());
        assertEquals(Long.valueOf(1L), reservationDTO.getEventId());
        assertEquals(1, reservationDTO.getTickets().size());

        TicketDTO ticketDTO = reservationDTO.getTickets().get(0);
        assertEquals(Integer.valueOf(1), ticketDTO.getColNum());
        assertEquals(Integer.valueOf(2), ticketDTO.getRowNum());
        assertEquals("Side", ticketDTO.getSeatGroupName());
        assertEquals(Double.valueOf(31), ticketDTO.getPrice());
        assertEquals(2, ticketDTO.getEventDays().size());
        assertTrue(ticketDTO.getEventDays().contains(LocalDateTime.of(2020, Month.MARCH, 1, 0, 0)));
        assertTrue(ticketDTO.getEventDays().contains(LocalDateTime.of(2020, Month.MARCH, 20, 0, 0)));
    }


    @Test
    public void createReservation_validParterreTicketSingleDay_reservationCreated() {
        HttpHeaders headers = headerTokenGenerator.generateHeaderWithToken("JennifferHooker@example.com");

        NewTicketDTO newTicketDTO = new NewTicketDTO(26L, null, false);
        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(newTicketDTO);
        NewReservationDTO newReservationDTO = new NewReservationDTO(1L, tickets);

        ResponseEntity<ReservationDTO> result = testRestTemplate.exchange(createURLWithPort(
                "/reservations"), HttpMethod.POST, new HttpEntity<>(newReservationDTO, headers), ReservationDTO.class);
        assertNotNull(result.getBody());
        ReservationDTO reservationDTO = result.getBody();

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNull(reservationDTO.getOrderId());
        assertEquals("Conputor", reservationDTO.getEventName());
        assertEquals(Long.valueOf(1L), reservationDTO.getEventId());
        assertEquals(1, reservationDTO.getTickets().size());

        TicketDTO ticketDTO = reservationDTO.getTickets().get(0);
        assertNull(ticketDTO.getColNum());
        assertNull(ticketDTO.getRowNum());
        assertEquals("Front", ticketDTO.getSeatGroupName());
        assertEquals(Double.valueOf(8), ticketDTO.getPrice());
        assertEquals(1, ticketDTO.getEventDays().size());
        assertEquals(LocalDateTime.of(2020, Month.MARCH, 20, 0, 0), ticketDTO.getEventDays().get(0));
    }


    @Test
    public void createReservation_validParterreTicketAllDays_reservationCreated() {
        HttpHeaders headers = headerTokenGenerator.generateHeaderWithToken("JennifferHooker@example.com");

        NewTicketDTO newTicketDTO = new NewTicketDTO(26L, null, true);
        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(newTicketDTO);
        NewReservationDTO newReservationDTO = new NewReservationDTO(1L, tickets);

        ResponseEntity<ReservationDTO> result = testRestTemplate.exchange(createURLWithPort(
                "/reservations"), HttpMethod.POST, new HttpEntity<>(newReservationDTO, headers), ReservationDTO.class);
        assertNotNull(result.getBody());
        ReservationDTO reservationDTO = result.getBody();

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNull(reservationDTO.getOrderId());
        assertEquals("Conputor", reservationDTO.getEventName());
        assertEquals(Long.valueOf(1L), reservationDTO.getEventId());
        assertEquals(1, reservationDTO.getTickets().size());

        TicketDTO ticketDTO = reservationDTO.getTickets().get(0);
        assertNull(ticketDTO.getColNum());
        assertNull(ticketDTO.getRowNum());
        assertEquals("Front", ticketDTO.getSeatGroupName());
        assertEquals(Double.valueOf(8), ticketDTO.getPrice());
        assertEquals(2, ticketDTO.getEventDays().size());
        assertTrue(ticketDTO.getEventDays().contains(LocalDateTime.of(2020, Month.MARCH, 1, 0, 0)));
        assertTrue(ticketDTO.getEventDays().contains(LocalDateTime.of(2020, Month.MARCH, 20, 0, 0)));
    }


    @Test
    public void createReservation_noSuchEvent_entityNotFoundException() {
        HttpHeaders headers = headerTokenGenerator.generateHeaderWithToken("JennifferHooker@example.com");

        NewTicketDTO newTicketDTO = new NewTicketDTO(1L, 2L, false);
        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(newTicketDTO);
        NewReservationDTO newReservationDTO = new NewReservationDTO(10000L, tickets);

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort(
                "/reservations"), HttpMethod.POST, new HttpEntity<>(newReservationDTO, headers), String.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Event not found", result.getBody());
    }


    @Test
    public void createReservation_tooManyTickets_entityNotValidException() {
        HttpHeaders headers = headerTokenGenerator.generateHeaderWithToken("JennifferHooker@example.com");

        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(new NewTicketDTO(2L, 10L, false));
        tickets.add(new NewTicketDTO(2L, 11L, false));
        tickets.add(new NewTicketDTO(2L, 12L, false));
        NewReservationDTO newReservationDTO = new NewReservationDTO(2L, tickets);

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort(
                "/reservations"), HttpMethod.POST, new HttpEntity<>(newReservationDTO, headers), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Too many tickets in the reservation", result.getBody());
    }


    @Test
    public void createReservation_eventStarted_impossibleActionException() {
        HttpHeaders headers = headerTokenGenerator.generateHeaderWithToken("JennifferHooker@example.com");

        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(new NewTicketDTO(2L, 10L, false));

        NewReservationDTO newReservationDTO = new NewReservationDTO(2L, tickets);

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort(
                "/reservations"), HttpMethod.POST, new HttpEntity<>(newReservationDTO, headers), String.class);

        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
        assertEquals("Event already started", result.getBody());
    }


    @Test
    public void createReservation_seatDoesNotExist_entityNotFoundException() {
        HttpHeaders headers = headerTokenGenerator.generateHeaderWithToken("JennifferHooker@example.com");

        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(new NewTicketDTO(1L, 10L, false));

        NewReservationDTO newReservationDTO = new NewReservationDTO(1L, tickets);

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort(
                "/reservations"), HttpMethod.POST, new HttpEntity<>(newReservationDTO, headers), String.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Seat not found", result.getBody());
    }


    @Test
    public void createReservation_seatTaken_impossibleActionException() {
        HttpHeaders headers = headerTokenGenerator.generateHeaderWithToken("JennifferHooker@example.com");

        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(new NewTicketDTO(1L, 1L, false));

        NewReservationDTO newReservationDTO = new NewReservationDTO(1L, tickets);


        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort(
                "/reservations"), HttpMethod.POST, new HttpEntity<>(newReservationDTO, headers), String.class);

        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
        assertEquals("Seat is already taken", result.getBody());
    }


    @Test
    public void createReservation_seatNotFreeAllDays_impossibleActionException() {
        HttpHeaders headers = headerTokenGenerator.generateHeaderWithToken("JennifferHooker@example.com");

        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(new NewTicketDTO(1L, 1L, true));

        NewReservationDTO newReservationDTO = new NewReservationDTO(1L, tickets);

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort(
                "/reservations"), HttpMethod.POST, new HttpEntity<>(newReservationDTO, headers), String.class);

        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
        assertEquals("Seat is not free for all days", result.getBody());
    }


    @Test
    public void createReservation_parterreDoesNotExist_entityNotFoundException() {
        HttpHeaders headers = headerTokenGenerator.generateHeaderWithToken("JennifferHooker@example.com");

        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(new NewTicketDTO(25L, null, false));

        NewReservationDTO newReservationDTO = new NewReservationDTO(1L, tickets);

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort(
                "/reservations"), HttpMethod.POST, new HttpEntity<>(newReservationDTO, headers), String.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Parterre not found", result.getBody());
    }


    @Test
    public void createReservation_parterreFull_impossibleActionException() {
        HttpHeaders headers = headerTokenGenerator.generateHeaderWithToken("JennifferHooker@example.com");

        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(new NewTicketDTO(50L, null, false));

        NewReservationDTO newReservationDTO = new NewReservationDTO(25L, tickets);

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort(
                "/reservations"), HttpMethod.POST, new HttpEntity<>(newReservationDTO, headers), String.class);

        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
        assertEquals("Parterre is already fully taken", result.getBody());
    }


    @Test
    public void createReservation_parterreNotFreeAllDays_impossibleActionException() {
        HttpHeaders headers = headerTokenGenerator.generateHeaderWithToken("JennifferHooker@example.com");

        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(new NewTicketDTO(50L, null, true));

        NewReservationDTO newReservationDTO = new NewReservationDTO(25L, tickets);

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort(
                "/reservations"), HttpMethod.POST, new HttpEntity<>(newReservationDTO, headers), String.class);

        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
        assertEquals("Parterre not free for all days", result.getBody());
    }


    @Test
    public void cancelReservation_reservationDoesNotExist_entityNotFoundExceptionThrown() {
        HttpHeaders headers = headerTokenGenerator.generateHeaderWithToken("JennifferHooker@example.com");

        long reservationId = 100L;

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort(
                "/reservations/" + reservationId), HttpMethod.DELETE, new HttpEntity<>(headers), String.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Reservation not found", result.getBody());
    }


    @Test
    public void cancelReservation_reservationAlreadyPaid_impossibleActionException() {
        HttpHeaders headers = headerTokenGenerator.generateHeaderWithToken("JennifferHooker@example.com");

        long reservationId = 1L;
        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort(
                "/reservations/" + reservationId), HttpMethod.DELETE, new HttpEntity<>(headers), String.class);

        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
        assertEquals("Reservation is already paid, therefore cannot be cancelled", result.getBody());
    }


    @Test
    public void cancelReservation_everythingValid_reservationCanceled() {
        HttpHeaders headers = headerTokenGenerator.generateHeaderWithToken("ktsnvt.tim1@gmail.com");

        long reservationId = 20L;

        ResponseEntity<ReservationDTO> result = testRestTemplate.exchange(createURLWithPort(
                "/reservations/" + reservationId), HttpMethod.DELETE, new HttpEntity<>(headers), ReservationDTO.class);
        assertNotNull(result.getBody());
        ReservationDTO reservationDTO = result.getBody();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertFalse(reservationDTO.getTickets().isEmpty());
        assertNull(reservationDTO.getOrderId());
    }


    @Test
    public void payReservationCreatePayment_reservationDoesNotExist_entityNotFoundExceptionThrown() {
        HttpHeaders headers = headerTokenGenerator.generateHeaderWithToken("JennifferHooker@example.com");

        long reservationId = 100L;

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort(
                "/reservations/" + reservationId), HttpMethod.POST, new HttpEntity<>(headers), String.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Reservation not found", result.getBody());
    }


    @Test
    public void payReservationCreatePayment_reservationAlreadyPaid_impossibleActionException() {
        HttpHeaders headers = headerTokenGenerator.generateHeaderWithToken("JennifferHooker@example.com");

        long reservationId = 1L;
        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort(
                "/reservations/" + reservationId), HttpMethod.POST, new HttpEntity<>(headers), String.class);

        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
        assertEquals("Reservation is already paid, therefore cannot be payed again", result.getBody());
    }


    @Test
    public void payReservationCreatePayment_everythingValid_paymentCreated() {
        HttpHeaders headers = headerTokenGenerator.generateHeaderWithToken("ktsnvt.tim1@gmail.com");

        long reservationId = 20L;
        ResponseEntity<PaymentDTO> result = testRestTemplate.exchange(createURLWithPort(
                "/reservations/" + reservationId), HttpMethod.POST, new HttpEntity<>(headers), PaymentDTO.class);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        PaymentDTO paymentDTO = result.getBody();
        assertNotNull(paymentDTO.getPaymentID());
    }


    @Test
    public void payReservationExecutePayment_reservationDoesNotExist_entityNotFoundExceptionThrown() {
        HttpHeaders headers = headerTokenGenerator.generateHeaderWithToken("JennifferHooker@example.com");
        String payerId = "payerId";
        PaymentDTO paymentDTO = new PaymentDTO("paymentId");
        paymentDTO.setPayerID(payerId);

        long reservationId = 100L;
        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort(
                "/reservations/" + reservationId + "/execute-payment"), HttpMethod.POST, new HttpEntity<>(paymentDTO, headers), String.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Reservation not found", result.getBody());
    }


    @Test
    public void payReservationExecutePayment_reservationAlreadyPaid_impossibleActionException() {
        HttpHeaders headers = headerTokenGenerator.generateHeaderWithToken("JennifferHooker@example.com");
        String payerId = "payerId";
        PaymentDTO paymentDTO = new PaymentDTO("paymentId");
        paymentDTO.setPayerID(payerId);

        long reservationId = 1L;
        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort(
                "/reservations/" + reservationId + "/execute-payment"), HttpMethod.POST, new HttpEntity<>(paymentDTO, headers), String.class);

        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
        assertEquals("Reservation is already paid, therefore cannot be payed again", result.getBody());
    }


    @Test
    public void createAndPayReservationCreatePayment_everythingValid_paymentCreated() {
        HttpHeaders headers = headerTokenGenerator.generateHeaderWithToken("ktsnvt.tim1@gmail.com");

        NewTicketDTO newTicketDTO = new NewTicketDTO(1L, 2L, false);
        ArrayList<NewTicketDTO> tickets = new ArrayList<>();
        tickets.add(newTicketDTO);
        NewReservationDTO newReservationDTO = new NewReservationDTO(1L, tickets);

        ResponseEntity<PaymentDTO> result = testRestTemplate.exchange(createURLWithPort(
                "/reservations/create-and-pay"), HttpMethod.POST, new HttpEntity<>(newReservationDTO, headers), PaymentDTO.class);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        PaymentDTO paymentDTO = result.getBody();
        assertNotNull(paymentDTO.getPaymentID());
    }


    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + "/api" + uri;
    }
}
