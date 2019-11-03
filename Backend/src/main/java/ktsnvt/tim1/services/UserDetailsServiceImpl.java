package ktsnvt.tim1.services;

import ktsnvt.tim1.model.User;
import ktsnvt.tim1.model.UserAuthority;
import ktsnvt.tim1.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

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
    	List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
    	for (UserAuthority ua: user.getUserAuthorities()) {
    		grantedAuthorities.add(new SimpleGrantedAuthority(ua.getAuthority().getName()));
    	}

    	return new org.springframework.security.core.userdetails.User(
    		  user.getEmail(),
    		  user.getPassword(),
    		  grantedAuthorities);
    }
  }

}
