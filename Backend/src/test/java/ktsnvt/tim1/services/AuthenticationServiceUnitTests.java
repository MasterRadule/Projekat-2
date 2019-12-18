package ktsnvt.tim1.services;


import ktsnvt.tim1.DTOs.UserDTO;
import ktsnvt.tim1.exceptions.EntityAlreadyExistsException;
import ktsnvt.tim1.exceptions.EntityNotFoundException;
import ktsnvt.tim1.mappers.UserMapper;
import ktsnvt.tim1.model.*;
import ktsnvt.tim1.repositories.UserRepository;
import ktsnvt.tim1.repositories.VerificationTokenRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
public class AuthenticationServiceUnitTests {

    @Autowired
    private AuthenticationService authenticationService;

    @MockBean
    private UserRepository userRepositoryMocked;

    @MockBean
    private UserMapper userMapperMocked;

    @MockBean
    private PasswordEncoder passwordEncoderMocked;

    @MockBean
    private VerificationTokenRepository verificationTokenRepositoryMocked;


    @Test
    @Transactional
    void register_emailExists_entityAlreadyExistsExceptionThrown(){
        String email = "mmarkovic@gmail.com";
        User user = new User(1L, "Marko", "Markovic", "$2y$12$FDOJQfuSrC7UAvBaUaX7UuP9NwZcZGI2joxQcHlzjEMXJBr57XAX6", email, true);
        UserDTO userToRegister = new UserDTO(null, "Petar", "Petrovic", "KtsNvt1+", email, false);
        MockHttpServletRequest request = new MockHttpServletRequest();

        Mockito.when(userRepositoryMocked.findByEmail(email)).thenReturn(user);

        assertThrows(EntityAlreadyExistsException.class,() -> authenticationService.register(userToRegister, request));

        verify(userRepositoryMocked, times(1)).findByEmail(email);
    }

    @Test
    @Transactional
    void register_userOk_user() throws EntityAlreadyExistsException, MalformedURLException {
        String email = "mmarkovic@gmail.com";
        Long id = 1L;
        UserDTO userToRegister = new UserDTO(null, "Petar", "Petrovic", "KtsNvt1+", email, false);
        RegisteredUser registeredUser = new RegisteredUser(null, userToRegister.getFirstName(), userToRegister.getLastName(), "$2y$12$FDOJQfuSrC7UAvBaUaX7UuP9NwZcZGI2joxQcHlzjEMXJBr57XAX6", userToRegister.getEmail(), false);
        RegisteredUser registeredUserSaved = new RegisteredUser(id, userToRegister.getFirstName(), userToRegister.getLastName(), "$2y$12$FDOJQfuSrC7UAvBaUaX7UuP9NwZcZGI2joxQcHlzjEMXJBr57XAX6", userToRegister.getEmail(), userToRegister.getVerified());
        UserDTO returnedUserDTO = new UserDTO(id, userToRegister.getFirstName(), userToRegister.getLastName(), null, userToRegister.getEmail(), userToRegister.getVerified());
        MockHttpServletRequest request = new MockHttpServletRequest();

        Mockito.when(userRepositoryMocked.findByEmail(email)).thenReturn(null);
        Mockito.when(passwordEncoderMocked.encode(userToRegister.getPassword())).thenReturn("$2y$12$FDOJQfuSrC7UAvBaUaX7UuP9NwZcZGI2joxQcHlzjEMXJBr57XAX6");
        Mockito.when(userRepositoryMocked.save(registeredUser)).thenReturn(registeredUserSaved);
        Mockito.when(userMapperMocked.toDTO(registeredUserSaved)).thenReturn(returnedUserDTO);

        UserDTO createdUser = authenticationService.register(userToRegister, request);

        assertEquals(id, createdUser.getId());
        Assert.assertNull(createdUser.getPassword());

        verify(userMapperMocked, times(1)).toDTO(registeredUserSaved);
        //verify(userRepositoryMocked, times(1)).save(registeredUser);
        verify(passwordEncoderMocked, times(1)).encode(userToRegister.getPassword());
        verify(userRepositoryMocked, times(1)).findByEmail(email);

    }

    @Test
    void verifyUser_verificationTokenIsNull_entityNotFoundExceptionThrown(){
        String token = "123546";

        Mockito.when(verificationTokenRepositoryMocked.findByToken(token)).thenReturn(null);

        assertThrows(EntityNotFoundException.class,() -> authenticationService.verifyUser(token));

        verify(verificationTokenRepositoryMocked, times(1)).findByToken(token);
    }

    @Test
    void verifyUser_verificationTokenHasExpired_entityNotFoundExceptionThrown(){
        String token = "123546";
        User user = new User(1L, "Petar", "Petrovic","$2y$12$FDOJQfuSrC7UAvBaUaX7UuP9NwZcZGI2joxQcHlzjEMXJBr57XAX6","ppetrovic@gmail.com",false);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm");
        VerificationToken verificationToken = new VerificationToken(1L, token, LocalDateTime.parse("15.12.2019. 21:00", formatter), user);

        Mockito.when(verificationTokenRepositoryMocked.findByToken(token)).thenReturn(verificationToken);

        assertThrows(EntityNotFoundException.class,() -> authenticationService.verifyUser(token));

        verify(verificationTokenRepositoryMocked, times(1)).findByToken(token);

    }

    @Test
    void verifyUser_verificationTokenIsValid_true() throws EntityNotFoundException{
        String token = "123546";
        String email = "ppetrovic@gmail.com";
        User user = new User(1L, "Petar", "Petrovic","$2y$12$FDOJQfuSrC7UAvBaUaX7UuP9NwZcZGI2joxQcHlzjEMXJBr57XAX6",email,false);
        VerificationToken verificationToken = new VerificationToken(1L, token, LocalDateTime.now().minusHours(1), user);

        Mockito.when(verificationTokenRepositoryMocked.findByToken(token)).thenReturn(verificationToken);
        Mockito.when(userRepositoryMocked.findByEmail(email)).thenReturn(user);

        assertTrue(authenticationService.verifyUser(token));
        assertTrue(user.getVerified());

        verify(verificationTokenRepositoryMocked, times(1)).findByToken(token);
        verify(userRepositoryMocked, times(1)).findByEmail(email);

    }


}
