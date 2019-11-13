package ktsnvt.tim1.mappers;

import ktsnvt.tim1.DTOs.UserDTO;
import ktsnvt.tim1.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements  IMapper<User, UserDTO> {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User toEntity(UserDTO userDTO){
        User u = new User();
        u.setId(null);
        u.setVerified(false);
        u.setEmail(userDTO.getEmail());
        u.setFirstName(userDTO.getFirstName());
        u.setLastName(userDTO.getLastName());
        u.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        return u;
    }

    @Override
    public UserDTO toDTO(User user){
        UserDTO dto = new UserDTO();
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setId(user.getId());
        dto.setVerified(user.getVerified());
        return dto;
    }
}
