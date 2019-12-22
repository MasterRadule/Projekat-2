package ktsnvt.tim1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.*;

@Entity
public class RegisteredUser extends User {
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "registeredUser")
    private Set<Reservation> reservations;

    public RegisteredUser(Long id, String firstName, String lastName, String password, String email, Boolean isVerified) {
        super(id, firstName, lastName, password, email, isVerified);
        Authority a = new Authority();
        a.setType(UserType.ROLE_USER);
        super.getAuthorities().add(a);
        this.reservations = new HashSet<>();
    }

    public RegisteredUser() {
        this.reservations = new HashSet<>();
    }

    public Set<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(Set<Reservation> reservations) {
        this.reservations = reservations;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }

        if (!(o instanceof RegisteredUser)) {
            return false;
        }
        RegisteredUser user = (RegisteredUser) o;
        if (this.getId()!=null ? !this.getId().equals(user.getId()): user.getId()!=null) return false;
        if (this.getFirstName()!=null ? !this.getFirstName().equals(user.getFirstName()): user.getFirstName()!=null) return false;
        if (this.getLastName()!=null ? !this.getLastName().equals(user.getLastName()): user.getLastName()!=null) return false;
        if (this.getEmail()!=null ? !this.getEmail().equals(user.getEmail()): user.getEmail()!=null) return false;
        if (this.getVerified()!=null ? !this.getVerified().equals(user.getVerified()): user.getVerified()!=null) return false;
        if (this.getPassword()!=null ? !this.getPassword().equals(user.getPassword()): user.getPassword()!=null) return false;
        if (this.getUsername()!=null ? !this.getUsername().equals(user.getUsername()): user.getUsername()!=null) return false;
        if (this.getAuthorities()!=null ? !this.getAuthorities().equals(user.getAuthorities()): user.getAuthorities()!=null) return false;
        return (this.getReservations()!=null ? this.getReservations().equals(user.getReservations()): user.getReservations()==null);

    }

}
