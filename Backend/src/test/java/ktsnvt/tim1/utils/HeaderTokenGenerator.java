package ktsnvt.tim1.utils;

import ktsnvt.tim1.model.User;
import ktsnvt.tim1.security.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class HeaderTokenGenerator {
    @Autowired
    private TokenUtils tokenUtils;

    public HttpHeaders generateHeaderWithToken(String userEmail) {
        UserDetails userDetails = new User(null, null, null, null, userEmail, null);
        String token = tokenUtils.generateToken(userDetails);
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", token);
        return headers;
    }
}
