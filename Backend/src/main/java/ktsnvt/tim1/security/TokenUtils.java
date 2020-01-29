package ktsnvt.tim1.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class TokenUtils {

	@Value("myXAuthSecret")
	private String secret;
	
	@Value("18000") //in seconds (5 hours)
	private Long expiration;

	public String getEmailFromToken(String token) {
		String email;
		try {
			Claims claims = this.getClaimsFromToken(token);
			email = claims.getSubject();
		} catch (Exception e) {
			email = null;
		}
		return email;
	}

	private Claims getClaimsFromToken(String token) {
		Claims claims;
		try {
			claims = Jwts.parser().setSigningKey(this.secret)
					.parseClaimsJws(token).getBody();
		} catch (Exception e) {
			claims = null;
		}
		return claims;
	}
	
	public Date getExpirationDateFromToken(String token) {
		Date expiration;
		try {
			final Claims claims = this.getClaimsFromToken(token);
			expiration = claims.getExpiration();
		} catch (Exception e) {
			expiration = null;
		}
		return expiration;
	}
	
	private boolean isTokenExpired(String token) {
	    final Date expiration = this.getExpirationDateFromToken(token);
	    return expiration.before(new Date(System.currentTimeMillis()));
	  }
	
	public boolean validateToken(String token, UserDetails userDetails) {
		final String email = getEmailFromToken(token);
		return email.equals(userDetails.getUsername())
				&& !isTokenExpired(token);
	}
	
	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<String, Object>();
		claims.put("sub", userDetails.getUsername());
		claims.put("created", new Date(System.currentTimeMillis()));
		claims.put("role", userDetails.getAuthorities());
		return Jwts.builder().setClaims(claims)
				.setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	}


}
