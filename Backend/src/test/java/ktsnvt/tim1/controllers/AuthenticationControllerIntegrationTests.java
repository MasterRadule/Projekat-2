package ktsnvt.tim1.controllers;

import ktsnvt.tim1.DTOs.LoginDTO;
import ktsnvt.tim1.DTOs.UserDTO;
import ktsnvt.tim1.repositories.UserRepository;
import ktsnvt.tim1.repositories.VerificationTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthenticationControllerIntegrationTests {

    @LocalServerPort
    private int port;

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    VerificationTokenRepository verificationTokenRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    void login_wrongEmail_errorMessageReturned(){
        LoginDTO loginDTO = new LoginDTO("ppetrovic@gmail.com", "KtsNvt1+");

        HttpEntity<LoginDTO> entity = new HttpEntity<>(loginDTO);

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/login"),
                HttpMethod.POST, entity, String.class);

        assertNotNull(result.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Invalid email or password", result.getBody());
    }

    @Test
    void login_wrongPassword_errorMessageReturned(){
        LoginDTO loginDTO = new LoginDTO("Dickens@example.com", "1234");

        HttpEntity<LoginDTO> entity = new HttpEntity<>(loginDTO);

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/login"),
                HttpMethod.POST, entity, String.class);

        assertNotNull(result.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Invalid email or password", result.getBody());
    }

    @Test
    void login_accountIsNotVerified_errorMessageReturned(){
        LoginDTO loginDTO = new LoginDTO("Colby126@example.com", "123");

        HttpEntity<LoginDTO> entity = new HttpEntity<>(loginDTO);

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/login"),
                HttpMethod.POST, entity, String.class);

        assertNotNull(result.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Your account is not verified!", result.getBody());
    }

    @Test
    void login_accountOk_tokenReturned(){
        LoginDTO loginDTO = new LoginDTO("Dickens@example.com", "123");

        HttpEntity<LoginDTO> entity = new HttpEntity<>(loginDTO);

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/login"),
                HttpMethod.POST, entity, String.class);

        String token = result.getBody();
        assertNotNull(token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    @Transactional
    @Rollback
    void register_emailExists_errorMessageReturned(){
        String email = "Porsha.Ferraro219@nowhere.com";
        UserDTO userToRegister = new UserDTO(null, "Petar", "Petrovic", "KtsNvt1+", email, false);

        HttpEntity<UserDTO> entity = new HttpEntity<>(userToRegister);
        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/register"),
                HttpMethod.POST, entity, String.class);

        assertNotNull(result.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Email already taken!", result.getBody());
    }

    @Test
    @Transactional
    @Rollback
    void register_userOk_registredUserReturned(){
        UserDTO userToRegister = new UserDTO(null, "Petar", "Petrovic", "KtsNvt1+", "ppetrovic@gmail.com", false);
        long initialSize = userRepository.count();

        HttpEntity<UserDTO> entity = new HttpEntity<>(userToRegister);
        ResponseEntity<UserDTO> result = testRestTemplate.exchange(createURLWithPort("/register"),
                HttpMethod.POST, entity, UserDTO.class);

        UserDTO registeredUser = result.getBody();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(registeredUser);
        assertEquals(userToRegister.getFirstName(), registeredUser.getFirstName());
        assertEquals(userToRegister.getLastName(), registeredUser.getLastName());
        assertFalse(registeredUser.getVerified());
        assertNotNull(registeredUser.getId());
        assertNull(registeredUser.getPassword());
        assertEquals(initialSize + 1, userRepository.count());
    }

    @Transactional
    @Rollback
    @Test
    void verifyUser_verificationTokenIsNull_errorMessageReturned(){
        String token = "afksdjlfjs111";
        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/verify-account?token="+token),
                HttpMethod.GET, null, String.class);

        assertNotNull(result.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("The link is invalid or broken!", result.getBody());
    }

    @Transactional
    @Rollback
    @Test
    void verifyUser_verificationTokenHasExpired_errorMessageReturned(){
        String token = "$2a$04$Vbug2lwwJGrvUXTj6z7ff";
        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/verify-account?token="+token),
                HttpMethod.GET, null, String.class);

        assertNotNull(result.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("The link is invalid or broken!", result.getBody());
    }

    @Transactional
    @Rollback
    @Test
    void verifyUser_verificationTokenIsValid_true(){
        String token = "$2a$04$Vbug2lwwJGrvUXTj6z7fq";
        ResponseEntity<Boolean> result = testRestTemplate.exchange(createURLWithPort("/verify-account?token="+token),
                HttpMethod.GET, null, Boolean.class);

        assertNotNull(result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody());
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + "/api" + uri;
    }
}
