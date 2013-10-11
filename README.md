Voyager Java Quickstart
========================

This project aims to show simple samples using various Voyager APIs from Java.

 * [Custom Extractors and mimetypes](docs/extractors.md)
 * [Working with DiscoveryJob](docs/discoveryjob.md)
 * [Direct Solr Access](docs/solr.md)
 * [Custom Locations](docs/locations.md)



Integration Tests
=================

These samples include simple unit tests in addition to more complex integration tests.  The integration 
tests require an instance of voyager running.  By default the integration tests will look to http://localhost:8888, 
to change this, set the <code>voyager.url</code> system property:

    ant -Dvoyager.url=http://yourhost:2345/path integration

If you are running these samples from a location other than <code>${app.dir}/dev/java</code> you can 
tell ant where to find the bundled .jar files using the <code>voyager.app</code> system property

    ant -Dvoyager.app=d:\voyager\server_1.9\app 
















