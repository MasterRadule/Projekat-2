package ktsnvt.tim1.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "token_id")
    private Long id;

    private String token;

    private LocalDateTime dateCreated;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private boolean expired;

    public VerificationToken() {
    }

    public VerificationToken(User user) {
        this.user = user;
        this.dateCreated = LocalDateTime.now();
        this.token = UUID.randomUUID().toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isExpired() {
        LocalDateTime d1 = LocalDateTime.now();

        this.expired = d1.minusDays(1).compareTo(this.dateCreated) > 0;
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }
}
