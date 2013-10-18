Voyager Java Quickstart
========================

This purpose of this project is to provide simple examples of extending Voyager with Java.

 * [Query Voyager](docs/query.md)
 * [Custom Extractors and Mimetypes](docs/extractors.md)
 * [Working with DiscoveryJobs](docs/discoveryjob.md)
 * [REST API Access](docs/rest.md)
 * [Custom Locations](docs/locations.md)

JVM Requirements:

  * Voyager runs on Java 1.7+
  * The Oracle JVMs have been tested successfully. Other JVMs have are untested and their viability is unknown. 
  * Check [Lucene JavaBugs](http://wiki.apache.org/lucene-java/JavaBugs) before deciding a deployment JVM.

System Requirements:
  * This project uses [Apache Ant](http://ant.apache.org/)


Getting Started
---------------

To get started, download Voyager here: http://voyagersearch.com/download

This Quickstart Guide is also included in the Voyager distribution in the <code>${install.dir}/dev/java/quickstart</code> folder.  
By default this is <code>c:\voyager\server_1.9\dev</code>

When running these samples from the <code>${install.dir}/dev/java/quickstart</code> folder, simply run:

    ant

To point to any Voyager install, set the `voyager.dir` system property:

    ant -Dvoyager.dir=d:\voyager\server_1.9


Integration Tests
-----------------

#### ![warning](docs/imgs/warning_48.png) The integration tests will modify the index!

These samples include simple unit tests in addition to more complex integration tests.  The integration 
tests require an instance of Voyager to be running.  By default the integration tests will look to http://localhost:8888, 
to change this, set the `voyager.url` system property:

    ant -Dvoyager.url=http://yourhost:2345/path integration



Quickstart Distribution
-----------------------
This file is included in the standard Voyager [download](http://voyagersearch.com/download).  The version info is listed below:
<pre>
Original Source: 
https://github.com/voyagersearch/quickstart-java.git
 
Date: 
@touch.time@

Version: 
https://github.com/voyagersearch/quickstart-java/commit/@githash@
</pre>










