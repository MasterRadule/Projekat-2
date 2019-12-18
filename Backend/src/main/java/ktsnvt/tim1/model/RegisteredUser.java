package ktsnvt.tim1.model;

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


}
