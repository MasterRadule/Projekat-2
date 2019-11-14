package ktsnvt.tim1.model;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "token_id")
    private Long id;

    private String token;

    private Date dateCreated;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private boolean expired;

    public VerificationToken() {
    }

    public VerificationToken(User user) {
        this.user = user;
        this.dateCreated = new Date();
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

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isExpired() {
        Date d1 = new Date();
        if (d1.getTime() - this.dateCreated.getTime() > 86400000) {
            this.expired = true;
        } else {
            this.expired=false;
        }
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }
}
