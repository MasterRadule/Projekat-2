package ktsnvt.tim1;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SelectPackages({"ktsnvt.tim1.controllers", "ktsnvt.tim1.repositories", "ktsnvt.tim1.services"})
public class TestSuiteJUnit5 {
}
