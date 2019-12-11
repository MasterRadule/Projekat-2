package ktsnvt.tim1;

import ktsnvt.tim1.controllers.EventControllerIntegrationTests;
import ktsnvt.tim1.controllers.LocationControllerIntegrationTests;
import ktsnvt.tim1.controllers.ReportControllerIntegrationTests;
import ktsnvt.tim1.repositories.EventRepositoryIntegrationTests;
import ktsnvt.tim1.repositories.ReservationRepositoryIntegrationTests;
import ktsnvt.tim1.services.*;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SelectClasses({EventControllerIntegrationTests.class, LocationControllerIntegrationTests.class,
        ReportControllerIntegrationTests.class, EventRepositoryIntegrationTests.class,
        ReservationRepositoryIntegrationTests.class, EventServiceIntegrationTests.class,
        LocationServiceIntegrationTests.class, LocationServiceUnitTests.class,
        ReportServiceIntegrationTests.class, ReportServiceUnitTests.class})
public class TestSuite {
}
