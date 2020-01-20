package ktsnvt.tim1.services;

import ktsnvt.tim1.DTOs.UserDTO;
import ktsnvt.tim1.exceptions.EntityNotValidException;
import ktsnvt.tim1.mappers.UserMapper;
import ktsnvt.tim1.model.User;
import ktsnvt.tim1.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
public class UserServiceUnitTests {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepositoryMocked;

    @MockBean
    private PasswordEncoder passwordEncoderMocked;

    @MockBean
    private UserMapper userMapperMocked;

    private void setUpPrincipal(User user) {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void editUser_userIdIsNull_entityNotValidExceptionThrown(){
        User user = new User();
        setUpPrincipal(user);
        UserDTO editedUser = new UserDTO(null, "Petar", "Petrovic", "KtsNvt1+", "ppetrovic@gmail.com", true);
        assertThrows(EntityNotValidException.class, () -> userService.editUser(editedUser));
    }

    @Test
    void editUser_notAllowedUser_entityNotValidExceptionThrown(){
        User user = new User();
        user.setId(2L);
        setUpPrincipal(user);
        Long id = 1L;
        UserDTO editedUser = new UserDTO(id,"Petar", "Petrovic", "KtsNvt1+", "ppetrovic@gmail.com", true );

        assertThrows(EntityNotValidException.class, () -> userService.editUser(editedUser));
    }

    @Test
    void editUser_userEmailChanged_entityNotValidExceptionThrown(){
        Long id = 1L;
        User user = new User(id,"Petar", "Petrovic", "KtsNvt1+", "ppetrovic@gmail.com", true );
        setUpPrincipal(user);
        UserDTO editedUser = new UserDTO(id,"Petar", "Petrovic", "KtsNvt1+", "ppetrovic1@gmail.com", true );

        Mockito.when(userRepositoryMocked.findById(id)).thenReturn(Optional.of(user));

        assertThrows(EntityNotValidException.class, () -> userService.editUser(editedUser));
    }

    @Test
    void editUser_userExists_userReturned() throws EntityNotValidException{
        Long id = 1L;
        User user = new User(id,"Petar", "Petrovic", "$2y$12$FDOJQfuSrC7UAvBaUaX7UuP9NwZcZGI2joxQcHlzjEMXJBr57XAX6", "ppetrovic@gmail.com", true );;
        setUpPrincipal(user);
        UserDTO editedDTO = new UserDTO(id,"Petar", "Petrovic", "KtsNvt1++", "ppetrovic@gmail.com", true );
        User newUser = new User(id,"Petar", "Petrovic", "$2y$12$y87u8NVCv7wFEcN9kmhvQeJuqfUP3RXbZz2xKhc94aMs6OrXKZYtW", "ppetrovic@gmail.com", true );
        UserDTO returnedDTO = new UserDTO(id,"Petar", "Petrovic", null, "ppetrovic@gmail.com", true );

        Mockito.when(passwordEncoderMocked.encode(editedDTO.getPassword())).thenReturn("$2y$12$y87u8NVCv7wFEcN9kmhvQeJuqfUP3RXbZz2xKhc94aMs6OrXKZYtW\n");
        Mockito.when(userRepositoryMocked.save(user)).thenReturn(newUser);
        Mockito.when(userMapperMocked.toDTO(newUser)).thenReturn(returnedDTO);

        UserDTO editedUser = userService.editUser(editedDTO);

        assertEquals(editedDTO.getId(), editedUser.getId());
        assertEquals(editedDTO.getEmail(), editedUser.getEmail());
        assertEquals(editedDTO.getFirstName(), editedUser.getFirstName());
        assertEquals(editedDTO.getLastName(), editedUser.getLastName());
        assertNull(editedUser.getPassword());
        assertEquals(editedDTO.getVerified(), editedUser.getVerified());

        verify(passwordEncoderMocked, times(1)).encode(editedDTO.getPassword());
        verify(userRepositoryMocked, times(1)).save(user);
        verify(userMapperMocked, times(1)).toDTO(newUser);
    }
}
