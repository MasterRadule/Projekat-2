package ktsnvt.tim1;

import ktsnvt.tim1.controllers.LocationControllerIntegrationTests;
import ktsnvt.tim1.controllers.ReportController;
import ktsnvt.tim1.controllers.ReportControllerIntegrationTests;
import ktsnvt.tim1.repositories.EventRepositoryIntegrationTests;
import ktsnvt.tim1.repositories.ReservationRepositoryIntegrationTests;
import ktsnvt.tim1.services.EventServiceIntegrationTests;
import ktsnvt.tim1.services.EventServiceUnitTests;
import ktsnvt.tim1.services.LocationServiceIntegrationTests;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runners.Suite;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class ProjekatApplicationTests {

	@Test
	void contextLoads() {
	}

}
