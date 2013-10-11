Voyager Java Quickstart
========================

This project aims to show simple examples extending Voyager with Java

 * [Custom Extractors and mimetypes](docs/extractors.md)
 * [Working with DiscoveryJobs](docs/discoveryjob.md)
 * [REST API Access](docs/rest.md)
 * [Direct Solr Access](docs/solr.md)
 * [Custom Locations](docs/locations.md)

Voyager runs on Java 1.7+

The Oracle JVMs have been tested, other JVMs may or may not work. 

Check [Lucene JavaBugs](http://wiki.apache.org/lucene-java/JavaBugs) before deciding a deployment JVM.


Getting Started
---------------

This quickstart is included in the Voyager distribution in the <code>${app.dir}/dev</code> folder.

Download voyager here: http://voyagersearch.com/download

When running these samples from the <code>${app.dir}/dev</code> folder, simply run:

    ant

To point to any voyager install, run

    ant -Dvoyager.app=d:\voyager\server_1.9\app 


Integration Tests
-----------------

These samples include simple unit tests in addition to more complex integration tests.  The integration 
tests require an instance of voyager running.  By default the integration tests will look to http://localhost:8888, 
to change this, set the <code>voyager.url</code> system property:

    ant -Dvoyager.url=http://yourhost:2345/path integration

















