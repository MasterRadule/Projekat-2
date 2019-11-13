package ktsnvt.tim1.repositories;

import ktsnvt.tim1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByEmail(String email);

	@Modifying
	Long deleteByEmail(String email);

}
