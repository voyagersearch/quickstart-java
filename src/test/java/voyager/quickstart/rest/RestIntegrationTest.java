package voyager.quickstart.rest;

import org.junit.Test;

/**
 * This test hits a live Voyager HTTP Server
 */
public class RestIntegrationTest {
  @Test
  public void testRest() {
    String base = System.getProperty("voyager.url");
    System.out.println( "TODO, hit: "+base );
  }
}
