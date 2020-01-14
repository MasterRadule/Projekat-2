package ktsnvt.tim1.services;

import edu.emory.mathcs.backport.java.util.Arrays;
import ktsnvt.tim1.DTOs.*;
import ktsnvt.tim1.exceptions.EntityNotFoundException;
import ktsnvt.tim1.exceptions.EntityNotValidException;
import ktsnvt.tim1.exceptions.ImpossibleActionException;
import ktsnvt.tim1.mappers.ReservationMapper;
import ktsnvt.tim1.model.Event;
import ktsnvt.tim1.model.EventCategory;
import ktsnvt.tim1.model.RegisteredUser;
import ktsnvt.tim1.model.Reservation;
import ktsnvt.tim1.repositories.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;


import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ReservationServiceUnitTests {

    @Autowired
    private ReservationService reservationService;

    @MockBean
    private ReservationRepository reservationRepositoryMocked;

    @MockBean
    private ReservationMapper reservationMapperMocked;


    private void setUpPrincipal(RegisteredUser registeredUser) {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(registeredUser);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getReservations_parameterALL_repositoryCalledOnce() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        int pageSize = 5;
        Pageable pageable = PageRequest.of(0, pageSize);
        reservationService.getReservations(ReservationTypeDTO.ALL, pageable);

        verify(reservationRepositoryMocked, times(1)).findByRegisteredUserIdAndIsCancelledFalse(registeredUserId, pageable);
    }

    @Test
    void getReservations_parameterBOUGHT_repositoryCalledOnce() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        int pageSize = 5;
        Pageable pageable = PageRequest.of(0, pageSize);
        reservationService.getReservations(ReservationTypeDTO.BOUGHT, pageable);

        verify(reservationRepositoryMocked, times(1)).findByRegisteredUserIdAndOrderIdIsNotNullAndIsCancelledFalse(registeredUserId, pageable);
    }

    @Test
    void getReservations_parameterRESERVED_repositoryCalledOnce() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        int pageSize = 5;
        Pageable pageable = PageRequest.of(0, pageSize);
        reservationService.getReservations(ReservationTypeDTO.RESERVED, pageable);

        verify(reservationRepositoryMocked, times(1)).findByRegisteredUserIdAndOrderIdIsNullAndIsCancelledFalse(registeredUserId, pageable);
    }


    @Test
    void getReservation_reservationExists_reservationReturned() throws EntityNotFoundException {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        Long reservationId = 1L;
        Reservation entity = new Reservation(reservationId, null, false, registeredUser, null);
        ReservationDTO returnDTO = new ReservationDTO(reservationId, null, null, null, new ArrayList<>());
        Optional<Reservation> o = Optional.of(entity);

        Mockito.when(reservationRepositoryMocked.findByIdAndRegisteredUserIdAndIsCancelledFalse(reservationId, registeredUserId)).thenReturn(o);
        Mockito.when(reservationMapperMocked.toDTO(entity)).thenReturn(returnDTO);
        ReservationDTO reservationDTO = reservationService.getReservation(reservationId);

        assertEquals(reservationId, reservationDTO.getId());
        verify(reservationRepositoryMocked, times(1)).findByIdAndRegisteredUserIdAndIsCancelledFalse(reservationId, registeredUserId);
    }

    @Test
    void getReservation_reservationDoesNotExist_entityNotFoundExceptionThrown() {
        Long registeredUserId = 6L;
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setId(registeredUserId);
        setUpPrincipal(registeredUser);

        Long reservationId = 1L;
        Optional<Reservation> o = Optional.empty();
        Mockito.when(reservationRepositoryMocked.findByIdAndRegisteredUserIdAndIsCancelledFalse(reservationId, registeredUserId)).thenReturn(o);

        assertThrows(EntityNotFoundException.class, () -> reservationService.getReservation(reservationId));
    }

    @Test
    void createReservation() {

    }

    @Test
    void cancelReservation() {
    }

    @Test
    void payReservationCreatePayment() {
    }

    @Test
    void payReservationExecutePayment() {
    }

    @Test
    void createAndPayReservationCreatePayment() {
    }

    @Test
    void createAndPayReservationExecutePayment() {
    }
}
