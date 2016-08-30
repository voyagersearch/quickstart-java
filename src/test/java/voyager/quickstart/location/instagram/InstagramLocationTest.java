package voyager.quickstart.location.instagram;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import voyager.quickstart.location.LocationTestHelper;

public class InstagramLocationTest {

    private InstagramRunner runner;

    @Before
    public void setup() throws Exception {
        LocationTestHelper helper = new LocationTestHelper();
        InstagramLocation loc = new InstagramLocation();
        runner = new InstagramRunner(loc, helper.solr, helper.jobs);
    }

    @Test
    public void testParset() throws Exception {
        /* check that parser runs without exceptions */
        InputStream stream = InstagramLocationTest.class.getResourceAsStream("sample.json");
        runner.processInstagramJson(stream);

    }
    // TODO add test for that triggers runner.run()
}
