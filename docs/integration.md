# Integration Tests

#### ![warning](imgs/warning_48.png) The integration tests will modify the index!

These samples include simple unit tests in addition to more complex integration tests.  The integration 
tests require an instance of Voyager to be running.  By default the integration tests will look to http://localhost:8888, 
to change this, set the `voyager.url` system property:

    ant -Dvoyager.url=http://yourhost:2345/path integration