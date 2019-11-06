package ktsnvt.tim1.controllers;

import ktsnvt.tim1.DTOs.LoginDTO;
import ktsnvt.tim1.model.User;
import ktsnvt.tim1.security.TokenUtils;
import ktsnvt.tim1.services.RegistrationService;
import ktsnvt.tim1.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.Console;

@RestController
public class UserController {

	@Autowired
    AuthenticationManager authenticationManager;

	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	@Autowired
	private TokenUtils tokenUtils;

	@Autowired
    private RegistrationService registrationService;
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<String> login(@RequestBody LoginDTO loginDTO,
										HttpServletResponse response) throws AuthenticationException {
		try {
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
					loginDTO.getEmail(), loginDTO.getPassword());
            Authentication authentication = authenticationManager.authenticate(token);
            UserDetails details = userDetailsService.loadUserByUsername(loginDTO.getEmail());
            return new ResponseEntity<String>(tokenUtils.generateToken(details), HttpStatus.OK);
        }
        catch (BadCredentialsException ex) {
            return new ResponseEntity<String>("Invalid email or password", HttpStatus.BAD_REQUEST);
        }catch(Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }


	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<String> register(@RequestBody User user) {
		String mess = registrationService.register(user);
		if(mess.equals("User registered!")){
			return new ResponseEntity<String>(mess, HttpStatus.OK);
		}else{
			return new ResponseEntity<String>(mess, HttpStatus.BAD_REQUEST);
		}

	}


}
