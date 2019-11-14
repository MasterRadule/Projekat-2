package ktsnvt.tim1.services;

import ktsnvt.tim1.DTOs.UserDTO;
import ktsnvt.tim1.exceptions.EntityNotFoundException;
import ktsnvt.tim1.exceptions.EntityNotValidException;
import ktsnvt.tim1.mappers.UserMapper;
import ktsnvt.tim1.model.User;
import ktsnvt.tim1.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public UserDTO editUser(UserDTO user) throws EntityNotValidException, EntityNotFoundException{

        if (user.getId() == null)
            throw new EntityNotValidException("User must have an ID");

        User toEdit = userRepository.findById(user.getId()).orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!user.getEmail().equals(toEdit.getEmail())){
            throw new EntityNotValidException("Email cannot be changed");
        }
        toEdit.setFirstName(user.getFirstName());
        toEdit.setLastName(user.getLastName());
        toEdit.setPassword(passwordEncoder.encode(user.getPassword()));

        return userMapper.toDTO(userRepository.save(toEdit));

    }


}


