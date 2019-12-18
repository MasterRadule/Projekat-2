package ktsnvt.tim1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Authority implements GrantedAuthority {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private UserType type;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UserType getType() {
		return type;
	}

	public void setType(UserType type) {
		this.type = type;
	}

	@Override
	public String getAuthority() {
		return type.toString();
	}

	@Override
	public boolean equals(Object o) {

		if (o == this) {
			return true;
		}

		if (!(o instanceof Authority)) {
			return false;
		}
		Authority authority = (Authority) o;
		if (this.getId()!=null ? !this.getId().equals(authority.getId()): authority.getId()!=null) return false;
		return (this.getType()!=null ? this.getType().equals(authority.getType()): authority.getType()==null);

	}

}


