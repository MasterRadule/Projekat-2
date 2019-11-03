package ktsnvt.tim1.repositories;

import ktsnvt.tim1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
	  public User findByEmail(String email);
}
