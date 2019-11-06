package ktsnvt.tim1.DTOs;

import javax.validation.constraints.NotBlank;

public class LoginDTO {

	@NotBlank(message = "You must enter email")
	private String email;
	@NotBlank(message = "You must enter password")
	private String password;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
