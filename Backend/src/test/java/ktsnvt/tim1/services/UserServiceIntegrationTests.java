package ktsnvt.tim1.services;

import ktsnvt.tim1.DTOs.UserDTO;
import ktsnvt.tim1.exceptions.EntityNotFoundException;
import ktsnvt.tim1.exceptions.EntityNotValidException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private PasswordEncoder passwordEncoder;

    @Transactional
    @Rollback
    @Test
    void editUser_userIdIsNull_entityNotValidExceptionThrown(){
        UserDTO editedDTO = new UserDTO(null, "Petar", "Petrovic", "KtsNvt1+", "ppetrovic@gmail.com", true);

        assertThrows(EntityNotValidException.class, () -> userService.editUser(editedDTO));
    }

    @Transactional
    @Rollback
    @Test
    void editUser_userDoesNotExist_entityNotFoundExceptionThrown(){
        UserDTO editedDTO = new UserDTO(60L, "Petar", "Petrovic", "KtsNvt1+", "ppetrovic@gmail.com", true);

        assertThrows(EntityNotFoundException.class, () -> userService.editUser(editedDTO));
    }

    @Transactional
    @Rollback
    @Test
    void editUser_userEmailChanged_entityNotValidExceptionThrown(){
        UserDTO editedDTO = new UserDTO(6L, "Jack", "Bowlin", "123", "JennifferHooker1@example.com", true);

        assertThrows(EntityNotValidException.class, () -> userService.editUser(editedDTO));
    }

    @Transactional
    @Rollback
    @Test
    void editUser_userExists_editedUserReturned() throws EntityNotValidException, EntityNotFoundException{
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
