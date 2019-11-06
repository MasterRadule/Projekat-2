package ktsnvt.tim1.services;


import ktsnvt.tim1.DTOs.UserDTO;
import ktsnvt.tim1.exceptions.EntityAlreadyExistsException;
import ktsnvt.tim1.model.Authority;
import ktsnvt.tim1.model.RegisteredUser;
import ktsnvt.tim1.model.UserType;
import ktsnvt.tim1.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDTO register(UserDTO user) throws EntityAlreadyExistsException {
        if(userRepository.findByEmail(user.getEmail())!= null){
            throw new EntityAlreadyExistsException("Email already taken!");
        }else{
            RegisteredUser regUser = new RegisteredUser();
            regUser.setId(null);
            regUser.setEmail(user.getEmail());
            regUser.setPassword(passwordEncoder.encode(user.getPassword()));
            regUser.setFirstName(user.getFirstName());
            regUser.setLastName(user.getLastName());
            regUser.setVerified(false);
            List<Authority> authorities = new ArrayList<Authority>();
            Authority a = new Authority();
            a.setType(UserType.ROLE_USER);
            authorities.add(a);
            regUser.setAuthorities(authorities);

            return new UserDTO(userRepository.save(regUser));
        }
    }
}
