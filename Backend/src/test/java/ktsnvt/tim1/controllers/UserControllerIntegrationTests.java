package ktsnvt.tim1.controllers;

import ktsnvt.tim1.DTOs.UserDTO;
import ktsnvt.tim1.model.User;
import ktsnvt.tim1.security.TokenUtils;
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
import static org.junit.jupiter.api.Assertions.*;

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

    @Transactional
    @Rollback
    @Test
    void editUser_usedIdIsNull_errorMessageReturned(){
        UserDTO editedDTO  = new UserDTO(null, "Petar", "Petrovic", "KtsNvt1+", "ppetrovic@gmail.com", true);

        HttpEntity<UserDTO> entity = new HttpEntity<>(editedDTO);

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/user"),
                HttpMethod.PUT, entity, String.class);

        assertNotNull(result.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("User must have an ID", result.getBody());
    }

    @Transactional
    @Rollback
    @Test
    void editUser_userDoesNotExist_errorMessageReturned(){
        UserDTO editedDTO  = new UserDTO(60L, "Petar", "Petrovic", "KtsNvt1+", "ppetrovic@gmail.com", true);

        HttpEntity<UserDTO> entity = new HttpEntity<>(editedDTO);

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/user"),
                HttpMethod.PUT, entity, String.class);

        assertNotNull(result.getBody());
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("User not found", result.getBody());
    }

    @Transactional
    @Rollback
    @Test
    void editUser_userEmailChanged_errorMessageReturned(){
        UserDTO editedDTO  = new UserDTO(6L, "Jack", "Bowlin", "KtsNvt1+", "JennifferHooker1@example.com", true);

        HttpEntity<UserDTO> entity = new HttpEntity<>(editedDTO);

        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/user"),
                HttpMethod.PUT, entity, String.class);

        assertNotNull(result.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Email cannot be changed", result.getBody());
    }

    @Transactional
    @Rollback
    @Test
    void editUser_userExists_editedUserReturned(){
        UserDTO editedDTO  = new UserDTO(6L, "Jackson", "Bowlin", "KtsNvt11+", "JennifferHooker@example.com", true);

        HttpEntity<UserDTO> entity = new HttpEntity<>(editedDTO);

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
    void getUser_userLoggedIn_userReturned(){
        UserDetails userDetails = new User(6L, "Jack", "Bowlin", "$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra", "JennifferHooker@example.com", true);
        String token = tokenUtils.generateToken(userDetails);
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", token);
        ResponseEntity<User> result = testRestTemplate.exchange(createURLWithPort("/user"),
                HttpMethod.GET,new HttpEntity<>(headers), User.class);

        User user = result.getBody();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(user);
        assertEquals(6L, user.getId().longValue());
    }

    @Test
    void getUser_noUserLoggedIn_errorMessageReturned(){
        ResponseEntity<String> result = testRestTemplate.exchange(createURLWithPort("/user"),
                HttpMethod.GET, null, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("No User Logged In!", result.getBody());
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + "/api" + uri;
    }

}
