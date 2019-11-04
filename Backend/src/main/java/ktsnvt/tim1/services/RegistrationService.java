package ktsnvt.tim1.services;

import ktsnvt.tim1.model.Authority;
import ktsnvt.tim1.model.RegisteredUser;
import ktsnvt.tim1.model.User;
import ktsnvt.tim1.model.UserType;
import ktsnvt.tim1.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class RegistrationService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String register(User user){
          if(userRepository.findByEmail(user.getEmail())!= null){
              return "Email is already taken!";
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

              try{
                  userRepository.save(regUser);
                  return "User registered!";
              }catch(Exception e){
                  return "Failed to save user.";
              }

          }

    }


}


