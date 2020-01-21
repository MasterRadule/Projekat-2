package ktsnvt.tim1.services;

import ktsnvt.tim1.DTOs.UserDTO;
import ktsnvt.tim1.exceptions.EntityNotValidException;
import ktsnvt.tim1.model.User;
import ktsnvt.tim1.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserServiceIntegrationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private void setUpPrincipal(User user) {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);
    }

    @Transactional
    @Rollback
    @Test
    void editUser_userIdIsNull_entityNotValidExceptionThrown(){
        User user = new User();
        setUpPrincipal(user);
        UserDTO editedDTO = new UserDTO(null, "Petar", "Petrovic", "KtsNvt1+", "ppetrovic@gmail.com", true);

        assertThrows(EntityNotValidException.class, () -> userService.editUser(editedDTO));
    }

    @Transactional
    @Rollback
    @Test
    void editUser_notAllowedUser_entityNotValidExceptionThrown(){
        User user = new User();
        user.setId(1L);
        setUpPrincipal(user);
        UserDTO editedDTO = new UserDTO(60L, "Petar", "Petrovic", "KtsNvt1+", "ppetrovic@gmail.com", true);

        assertThrows(EntityNotValidException.class, () -> userService.editUser(editedDTO));
    }

    @Transactional
    @Rollback
    @Test
    void editUser_userEmailChanged_entityNotValidExceptionThrown(){
        User user = new User();
        user.setId(6L);
        user.setEmail("JennifferHooker@example.com");
        setUpPrincipal(user);

        UserDTO editedDTO = new UserDTO(6L, "Jack", "Bowlin", "123", "JennifferHooker1@example.com", true);

        assertThrows(EntityNotValidException.class, () -> userService.editUser(editedDTO));
    }

    @Transactional
    @Rollback
    @Test
    void editUser_userExists_editedUserReturned() throws EntityNotValidException{
        setUpPrincipal(userRepository.findByEmail("JennifferHooker@example.com"));

        UserDTO editedDTO = new UserDTO(6L, "Jackie", "Bowlin", "1234", "JennifferHooker@example.com", true);

        UserDTO returnedDTO = userService.editUser(editedDTO);

        assertEquals(editedDTO.getId(),returnedDTO.getId());
        assertEquals(editedDTO.getFirstName(), returnedDTO.getFirstName());
        assertEquals(editedDTO.getLastName(), returnedDTO.getLastName());
        assertEquals(editedDTO.getEmail(), returnedDTO.getEmail());
        assertEquals(editedDTO.getVerified(), returnedDTO.getVerified());
        assertNull(returnedDTO.getPassword());
    }

}
