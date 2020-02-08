package ktsnvt.tim1.services;

import ktsnvt.tim1.DTOs.ChangePasswordDTO;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    @Test
    void changePassword_incorrectOldPassword_entityNotValidExceptionThrown(){
        User user = new User();
        user.setPassword("$2y$12$YATuOt2bcLaly5c0aMLx/e5l.wg6fLHmicF.Uj02Pl0tmRtCqk6NC");
        setUpPrincipal(user);

        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO("123", "KtsNvtTim1+", "KtsNvtTim1+");

        Mockito.when(passwordEncoderMocked.matches(changePasswordDTO.getOldPassword(), user.getPassword())).thenReturn(false);

        assertThrows(EntityNotValidException.class, () -> userService.changePassword(changePasswordDTO));

        verify(passwordEncoderMocked, times(1)).matches(changePasswordDTO.getOldPassword(), user.getPassword());
    }

    @Test
    void changePassword_passwordsDontMatch_entityNotValidExceptionThrown(){
        User user = new User();
        user.setPassword("$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra");
        setUpPrincipal(user);

        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO("123", "KtsNvtTim1+", "KtsNvtTim1++");

        Mockito.when(passwordEncoderMocked.matches(changePasswordDTO.getOldPassword(), user.getPassword())).thenReturn(true);
        assertThrows(EntityNotValidException.class, () -> userService.changePassword(changePasswordDTO));
        verify(passwordEncoderMocked, times(1)).matches(changePasswordDTO.getOldPassword(), user.getPassword());
    }

    @Test
    void changePassword_passwordsOk_true() throws EntityNotValidException {
        Long id = 1L;
        String password = "$2y$12$YVTUQqw5VcyZTrH7rP7k/.iZsZxA8RK93szRmf.xUk9Cm0XzFl0dG";
        String newPassword = "$2y$12$Gx9OessyeXO4.dZrP9uSsuaTUG2XJqdLWPzC0x1vF7DcoyTSgNBbi";
        User user = new User(id,"Petar", "Petrovic", password, "ppetrovic@gmail.com", true );;
        setUpPrincipal(user);

        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO("123", "KtsNvtTim1+", "KtsNvtTim1+");
        User newUser = new User(id,"Petar", "Petrovic", newPassword, "ppetrovic@gmail.com", true );

        Mockito.when(passwordEncoderMocked.matches(changePasswordDTO.getOldPassword(), password)).thenReturn(true);
        Mockito.when(passwordEncoderMocked.encode(changePasswordDTO.getPassword())).thenReturn(newPassword);
        Mockito.when(userRepositoryMocked.save(user)).thenReturn(newUser);

        boolean success = userService.changePassword(changePasswordDTO);
        assertTrue(success);

        verify(passwordEncoderMocked, times(1)).matches(changePasswordDTO.getOldPassword(), password);
        verify(passwordEncoderMocked, times(1)).encode(changePasswordDTO.getPassword());
        verify(userRepositoryMocked, times(1)).save(user);
    }


}
