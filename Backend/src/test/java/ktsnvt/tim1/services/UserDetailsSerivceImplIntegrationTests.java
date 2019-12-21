package ktsnvt.tim1.services;

import ktsnvt.tim1.exceptions.EntityNotFoundException;
import ktsnvt.tim1.exceptions.EntityNotValidException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserDetailsSerivceImplIntegrationTests {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Test
    @Transactional
    void loadUserByUsername_emailNotFound_usernameNotFoundExceptionThrown() {
        String email = "ppetrovic@gmail.com";

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(email));
    }

    @Test
    @Transactional
    void loadUserByUsername_emailOk_userReturned(){
        String email = "Dickens@example.com";

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        assertEquals(email, userDetails.getUsername());
    }

    @Test
    void checkIsVerified_accountNotFound_entityNotFoundExceptionThrown(){
        String email = "ppetrovic@gmail.com";

        assertThrows(EntityNotFoundException.class, () -> userDetailsService.checkIsVerified(email));

    }

    @Test
    void checkIsVerified_accountIsNotVerified_entityNotValidExceptionThrown(){
        String email = "Bumgarner26@example.com";

        assertThrows(EntityNotValidException.class, () -> userDetailsService.checkIsVerified(email));
    }

    @Test
    void checkIsVerified_accountIsVerified_userDetailsReturned() throws EntityNotFoundException, EntityNotValidException{
        String email = "JennifferHooker@example.com";

        UserDetails userDetails = userDetailsService.checkIsVerified(email);

        assertEquals(email, userDetails.getUsername());

    }



}
