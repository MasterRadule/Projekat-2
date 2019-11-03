package ktsnvt.tim1.repositories;

import ktsnvt.tim1.model.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Page<Location> findByNameIgnoreCaseContaining(String name, Pageable pageable);
}
