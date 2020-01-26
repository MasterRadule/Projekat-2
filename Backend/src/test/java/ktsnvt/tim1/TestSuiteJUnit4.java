package ktsnvt.tim1;

import ktsnvt.tim1.services.EventServiceUnitTests;
import ktsnvt.tim1.services.PeriodicCheckServiceUnitTests;
import ktsnvt.tim1.services.ReservationServiceIntegrationTests;
import ktsnvt.tim1.services.ReservationServiceUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ReservationServiceUnitTests.class, ReservationServiceIntegrationTests.class,
        EventServiceUnitTests.class, PeriodicCheckServiceUnitTests.class
})
public class TestSuiteJUnit4 {
}
