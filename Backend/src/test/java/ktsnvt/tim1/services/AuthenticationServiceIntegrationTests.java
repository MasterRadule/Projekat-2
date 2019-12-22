package ktsnvt.tim1.services;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import ktsnvt.tim1.DTOs.UserDTO;
import ktsnvt.tim1.exceptions.EntityAlreadyExistsException;
import ktsnvt.tim1.exceptions.EntityNotFoundException;
import ktsnvt.tim1.model.User;
import ktsnvt.tim1.model.VerificationToken;
import ktsnvt.tim1.repositories.UserRepository;
import ktsnvt.tim1.repositories.VerificationTokenRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthenticationServiceIntegrationTests {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    private static GreenMail greenMail;

    @BeforeAll
    public static void setupSMTP() {
        greenMail = new GreenMail(new ServerSetup(2525, "localhost", "smtp"));
        greenMail.setUser("ktsnvt","ktsnvttim1");
        greenMail.start();
    }

    @AfterAll
    public static void stopSMTP() {
        greenMail.stop();
    }

    @Transactional
    @Rollback
    @Test
    void register_userRegistered() throws EntityAlreadyExistsException, MessagingException, IOException {
        UserDTO userToRegister = new UserDTO(null, "Petar", "Petrovic", "KtsNvt1+", "ppetrovic@gmail.com", false);
        MockHttpServletRequest request = new MockHttpServletRequest();

        UserDTO registeredUser = authenticationService.register(userToRegister, request);

        assertNotEquals(userToRegister.getPassword(), registeredUser.getPassword());
        assertEquals(userToRegister.getEmail(), registeredUser.getEmail());
        assertEquals(userToRegister.getFirstName(), registeredUser.getFirstName());
        assertEquals(userToRegister.getLastName(), registeredUser.getLastName());
        assertNotNull(registeredUser.getId());
        assertFalse(registeredUser.getVerified());
        assertNotNull(verificationTokenRepository.findByUser(userRepository.findByEmail(userToRegister.getEmail())));
        assertTrue(greenMail.waitForIncomingEmail(5000, 1));
        assertEquals(userToRegister.getEmail(), greenMail.getReceivedMessages()[0].getRecipients(Message.RecipientType.TO)[0].toString());
    }

    @Test
    void register_emailExists_entityAlreadyExistsExceptionThrown(){
        UserDTO userToRegister = new UserDTO(null, "Petar", "Petrovic", "KtsNvt1+", "Dickens@example.com", false);
        MockHttpServletRequest request = new MockHttpServletRequest();

        assertThrows(EntityAlreadyExistsException.class, () -> authenticationService.register(userToRegister, request));
    }

    @Test
    void verifyUser_verificationTokenIsNull_entityNotFoundExceptionThrown(){
        String token = "ajkdsadkslo7d6";

        assertThrows(EntityNotFoundException.class, () -> authenticationService.verifyUser(token));
    }

    @Test
    void verifyUser_verificationTokenHasExpired_entityNotFoundExceptionThrown(){
        String token = "$2a$04$Vbug2lwwJGrvUXTj6z7fw";

        assertThrows(EntityNotFoundException.class, () -> authenticationService.verifyUser(token));
    }

    @Test
    @Transactional
    @Rollback
    void verifyUser_verificationTokenOk_userVerified() throws EntityNotFoundException{
        String token = "$2a$04$Vbug2lwwJGrvUXTj6z7fq";
        authenticationService.verifyUser(token);
        VerificationToken vt = verificationTokenRepository.findByToken(token);
        User user = userRepository.findByEmail(vt.getUser().getEmail());

        assertTrue(user.getVerified());
    }
}
