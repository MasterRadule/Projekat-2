package ktsnvt.tim1.services;

import ktsnvt.tim1.DTOs.ChangePasswordDTO;
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

    public UserDTO editUser(UserDTO userDTO) throws EntityNotValidException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDTO.getId() == null)
            throw new EntityNotValidException("User must have an ID");

        if (userDTO.getId() != user.getId()){
            throw new EntityNotValidException("Invalid action");
        }

        if (!userDTO.getEmail().equals(user.getEmail())){
            throw new EntityNotValidException("Email cannot be changed");
        }
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        if(!userDTO.getPassword().equals(user.getPassword())){
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        return userMapper.toDTO(userRepository.save(user));

    }

    public boolean changePassword(ChangePasswordDTO changePasswordDTO) throws EntityNotValidException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())){
            throw new EntityNotValidException("Old password is incorrect");
        }
        if (!changePasswordDTO.getPassword().equals(changePasswordDTO.getRepeatedPassword())){
            throw new EntityNotValidException("Passwords don't match");
        }
        user.setPassword(passwordEncoder.encode(changePasswordDTO.getPassword()));
        userRepository.save(user);
        return true;
    }
}


