package ktsnvt.tim1.DTOs;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class ChangePasswordDTO {

    @NotBlank(message = "Old password cannot be empty")
    private String oldPassword;
    @NotBlank(message = "Password cannot be empty")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@!_*#$%^&+=])(?=\\S+$).{8,}")
    private String password;
    @NotBlank(message = "Repeated password cannot be empty")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@!_*#$%^&+=])(?=\\S+$).{8,}")
    private String repeatedPassword;

    public ChangePasswordDTO(String oldPassword, String password, String repeatedPassword) {
        this.oldPassword = oldPassword;
        this.password = password;
        this.repeatedPassword = repeatedPassword;
    }

    public ChangePasswordDTO() {
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public String getPassword() {
        return password;
    }

    public String getRepeatedPassword() {
        return repeatedPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRepeatedPassword(String repeatedPassword) {
        this.repeatedPassword = repeatedPassword;
    }
}
