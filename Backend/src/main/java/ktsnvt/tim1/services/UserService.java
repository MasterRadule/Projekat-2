package ktsnvt.tim1.services;

import ktsnvt.tim1.DTOs.UserDTO;
import ktsnvt.tim1.exceptions.EntityNotValidException;
import ktsnvt.tim1.mappers.UserMapper;
import ktsnvt.tim1.model.User;
import ktsnvt.tim1.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    public UserDTO editUser(UserDTO user) throws EntityNotValidException {
        User registeredUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user.getId() == null)
            throw new EntityNotValidException("User must have an ID");

        if (user.getId() != registeredUser.getId()){
            throw new EntityNotValidException("Invalid action");
        }

        if (!user.getEmail().equals(registeredUser.getEmail())){
            throw new EntityNotValidException("Email cannot be changed");
        }
        registeredUser.setFirstName(user.getFirstName());
        registeredUser.setLastName(user.getLastName());
        registeredUser.setPassword(passwordEncoder.encode(user.getPassword()));

        return userMapper.toDTO(userRepository.save(registeredUser));

    }


}


