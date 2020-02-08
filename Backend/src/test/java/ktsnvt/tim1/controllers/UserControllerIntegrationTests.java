package ktsnvt.tim1.controllers;

import ktsnvt.tim1.DTOs.ChangePasswordDTO;
import ktsnvt.tim1.DTOs.UserDTO;
import ktsnvt.tim1.model.User;
import ktsnvt.tim1.security.TokenUtils;
import ktsnvt.tim1.utils.HeaderTokenGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerIntegrationTests {

    @LocalServerPort
    private int port;

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    TokenUtils tokenUtils;

    @Autowired
    private HeaderTokenGenerator headerTokenGenerator;

    @Transactional
    @Rollback
    @Test
    void editUser_usedIdIsNull_errorMessageReturned() {
        HttpHeaders headers = headerTokenGenerator.generateHeaderWithToken("JennifferHooker@example.com");
        UserDTO editedDTO = new UserDTO(null, "Petar", "Petrovic", "KtsNvt1+", "ppetrovic@gmail.com", true);

        HttpEntity<UserDTO> entity = new HttpEntity<>(editedDTO, headers);

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/user"),
                HttpMethod.PUT, entity, String.class);

        assertNotNull(result.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("User must have an ID", result.getBody());
    }

    @Transactional
    @Rollback
    @Test
    void editUser_notAllowedUser_errorMessageReturned() {
        HttpHeaders headers = headerTokenGenerator.generateHeaderWithToken("JennifferHooker@example.com");

        UserDTO editedDTO = new UserDTO(60L, "Petar", "Petrovic", "KtsNvt1+", "ppetrovic@gmail.com", true);

        HttpEntity<UserDTO> entity = new HttpEntity<>(editedDTO, headers);

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/user"),
                HttpMethod.PUT, entity, String.class);

        assertNotNull(result.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Invalid action", result.getBody());
    }

    @Transactional
    @Rollback
    @Test
    void editUser_userEmailChanged_errorMessageReturned() {
        HttpHeaders headers = headerTokenGenerator.generateHeaderWithToken("JennifferHooker@example.com");
        UserDTO editedDTO = new UserDTO(6L, "Jack", "Bowlin", "KtsNvt1+", "JennifferHooker1@example.com", true);

        HttpEntity<UserDTO> entity = new HttpEntity<>(editedDTO, headers);

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/user"),
                HttpMethod.PUT, entity, String.class);

        assertNotNull(result.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Email cannot be changed", result.getBody());
    }

    @Transactional
    @Rollback
    @Test
    void editUser_userExists_editedUserReturned() {
        HttpHeaders headers = headerTokenGenerator.generateHeaderWithToken("JennifferHooker@example.com");

        UserDTO editedDTO = new UserDTO(6L, "Jackson", "Bowlin", "KtsNvt11+", "JennifferHooker@example.com", true);

        HttpEntity<UserDTO> entity = new HttpEntity<>(editedDTO, headers);

        ResponseEntity<UserDTO> result = testRestTemplate.exchange(createURLWithPort("/user"),
                HttpMethod.PUT, entity, UserDTO.class);

        UserDTO returnedUser = result.getBody();
        assertNotNull(returnedUser);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(editedDTO.getId(), returnedUser.getId());
        assertEquals(editedDTO.getFirstName(), returnedUser.getFirstName());
        assertEquals(editedDTO.getLastName(), returnedUser.getLastName());
        assertEquals(editedDTO.getEmail(), returnedUser.getEmail());
        assertTrue(returnedUser.getVerified());
        assertNull(returnedUser.getPassword());
    }

    @Test
    @Transactional
    void getUser_userLoggedIn_userReturned() {
        UserDetails userDetails = new User(6L, "Jack", "Bowlin", "$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra", "JennifferHooker@example.com", true);
        String token = tokenUtils.generateToken(userDetails);
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", token);
        ResponseEntity<User> result = testRestTemplate.exchange(createURLWithPort("/user"),
                HttpMethod.GET, new HttpEntity<>(headers), User.class);

        User user = result.getBody();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(user);
        assertEquals(6L, user.getId().longValue());
    }

    @Test
    void getUser_noUserLoggedIn_errorMessageReturned() {
        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/user"),
                HttpMethod.GET, null, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("No User Logged In!", result.getBody());
    }

    @Test
    void changePassword_incorrectOldPassword_errorMessageReturned() {
        HttpHeaders headers = headerTokenGenerator.generateHeaderWithToken("JennifferHooker@example.com");

        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO("1234", "KtsNvtTim1+", "KtsNvtTim1+");

        HttpEntity<ChangePasswordDTO> entity = new HttpEntity<>(changePasswordDTO, headers);

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/user/password"),
                HttpMethod.PUT, entity, String.class);

        assertNotNull(result.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Old password is incorrect", result.getBody());
    }

    @Test
    void changePassword_passwordsDontMatch_errorMessageReturned() {
        HttpHeaders headers = headerTokenGenerator.generateHeaderWithToken("JennifferHooker@example.com");

        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO("123", "KtsNvtTim1+", "KtsNvtTim1++");

        HttpEntity<ChangePasswordDTO> entity = new HttpEntity<>(changePasswordDTO, headers);

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/user/password"),
                HttpMethod.PUT, entity, String.class);

        assertNotNull(result.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Passwords don't match", result.getBody());
    }

    @Transactional
    @Rollback
    @Test
    void changePassword_passwordsOK_true() {
        HttpHeaders headers = headerTokenGenerator.generateHeaderWithToken("Porsha.Ferraro219@nowhere.com");

        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO("123", "KtsNvtTim1+", "KtsNvtTim1+");

        HttpEntity<ChangePasswordDTO> entity = new HttpEntity<>(changePasswordDTO, headers);

        ResponseEntity<Boolean> result = testRestTemplate.exchange(createURLWithPort("/user/password"),
                HttpMethod.PUT, entity, Boolean.class);

        boolean success = result.getBody();
        assertTrue(success);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + "/api" + uri;
    }

}
