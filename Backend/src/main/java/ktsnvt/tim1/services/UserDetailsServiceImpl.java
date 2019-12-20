package ktsnvt.tim1.services;

import ktsnvt.tim1.exceptions.EntityNotFoundException;
import ktsnvt.tim1.exceptions.EntityNotValidException;
import ktsnvt.tim1.model.User;
import ktsnvt.tim1.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email);

    if (user == null) {
      throw new UsernameNotFoundException(String.format("No user found with email '%s'.", email));
    } else {
    	return user;
    }
  }

  public UserDetails checkIsVerified(String email) throws EntityNotValidException, EntityNotFoundException{
    User user = userRepository.findByEmail(email);
    if(user == null){
      throw new EntityNotFoundException("Account not found");
    }
    else if(user.getVerified()){
      return user;
    }else{
      throw new EntityNotValidException("Your account is not verified!");
    }
  }

}
