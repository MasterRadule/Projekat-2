package ktsnvt.tim1.services;

import ktsnvt.tim1.exceptions.EntityNotValidException;
import ktsnvt.tim1.model.User;
import ktsnvt.tim1.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserDetailsServiceImplUnitTests {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private UserRepository userRepositoryMocked;

    @Test
    void loadUserByUsername_emailNotFound_usernameNotFoundExceptionThrown(){
        String email = "ppetrovic@gmail.com";

        Mockito.when(userRepositoryMocked.findByEmail(email)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(email));

        verify(userRepositoryMocked, times(1)).findByEmail(email);
    }

    @Test
    void loadUserByUsername_emailOk_user(){
        String email = "ppetrovic@gmail.com";
        User user = new User(1L,"Petar","Petrovic","$2y$12$UPsAA8iwVtUJmUX0VX.a4eTKqFexiz2t2nXQBZWTozKsvPnMAj3Om",email,false);

        Mockito.when(userRepositoryMocked.findByEmail(email)).thenReturn(user);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        assertEquals(userDetails.getUsername(), email);
        assertEquals(userDetails.getPassword(), user.getPassword());

        verify(userRepositoryMocked, times(1)).findByEmail(email);
    }

    @Test
    void checkIsVerified_accountIsNotVerified_entityNotValidException(){
        String email = "ppetrovic@gmail.com";
        User user = new User(1L,"Petar","Petrovic","$2y$12$UPsAA8iwVtUJmUX0VX.a4eTKqFexiz2t2nXQBZWTozKsvPnMAj3Om",email,false);

        Mockito.when(userRepositoryMocked.findByEmail(email)).thenReturn(user);

        assertThrows(EntityNotValidException.class, () -> userDetailsService.checkIsVerified(email));

        verify(userRepositoryMocked, times(1)).findByEmail(email);
    }

    @Test
    void checkIsVerified_accountIsVerified_user() throws EntityNotValidException{
        String email = "ppetrovic@gmail.com";
        User user = new User(1L,"Petar","Petrovic","$2y$12$UPsAA8iwVtUJmUX0VX.a4eTKqFexiz2t2nXQBZWTozKsvPnMAj3Om",email,true);

        Mockito.when(userRepositoryMocked.findByEmail(email)).thenReturn(user);

        UserDetails userDetails = userDetailsService.checkIsVerified(email);

        assertEquals(userDetails.getUsername(), user.getEmail());
        assertEquals(userDetails.getPassword(), user.getPassword());

        verify(userRepositoryMocked, times(1)).findByEmail(email);

    }




}
