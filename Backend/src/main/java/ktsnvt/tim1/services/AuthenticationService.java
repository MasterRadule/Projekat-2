package ktsnvt.tim1.services;


import ktsnvt.tim1.DTOs.UserDTO;
import ktsnvt.tim1.exceptions.EntityAlreadyExistsException;
import ktsnvt.tim1.exceptions.EntityNotFoundException;
import ktsnvt.tim1.model.*;
import ktsnvt.tim1.repositories.UserRepository;
import ktsnvt.tim1.repositories.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private Environment environment;

    @Autowired
    private EmailService emailService;

    public UserDTO register(UserDTO user, HttpServletRequest request) throws EntityAlreadyExistsException, MalformedURLException {
        String url = new URL(request.getRequestURL().toString()).getAuthority();

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

            UserDTO u = new UserDTO(userRepository.save(regUser));

            VerificationToken verificationToken = new VerificationToken(regUser);
            verificationTokenRepository.save(verificationToken);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(regUser.getEmail());
            mailMessage.setSubject("Complete Registration");
            mailMessage.setFrom(environment.getProperty("spring.mail.username"));
            mailMessage.setText("To confirm your account please click here: "
            +"http://"+url+"/verify-account?token="+ verificationToken.getToken());

            emailService.sendEmail(mailMessage);

            return u;
        }
    }

    public boolean verifyUser(String token) throws EntityNotFoundException {
        VerificationToken vt = verificationTokenRepository.findByToken(token);
        if(vt==null){
            throw new EntityNotFoundException("The link is invalid or broken!");
        }
        User u = userRepository.findByEmail(vt.getUser().getEmail());
        u.setVerified(true);
        userRepository.save(u);
        return true;
    }
}
