Voyager Java Quickstart
========================

This purpose of this article is to provide simple examples of extending Voyager with Java.

 * [Custom Extractors and mimetypes](docs/extractors.md)
 * [Working with DiscoveryJobs](docs/discoveryjob.md)
 * [REST API Access](docs/rest.md)
 * [Direct Solr Access](docs/solr.md)
 * [Custom Locations](docs/locations.md)

Some general information about Voyager:

Voyager runs on Java 1.7+

The Oracle JVMs have been tested successfully. Other JVMs have are untested and their viability is unknown. 

Check [Lucene JavaBugs](http://wiki.apache.org/lucene-java/JavaBugs) before deciding a deployment JVM.


Getting Started
---------------

To get started, download Voyager here: http://voyagersearch.com/download

This Quickstart Guide is also included in the Voyager distribution in the <code>${install.dir}/dev</code> folder.  
By default this is <code>c:\voyager\server_1.9\dev</code>

When running these samples from the <code>${install.dir}/dev/java/quickstart</code> folder, simply run:

    ant

To point to any Voyager install, set the `voyager.app` system property:

    ant -Dvoyager.app=d:\voyager\server_1.9\app 


Integration Tests
-----------------

These samples include simple unit tests in addition to more complex integration tests.  The integration 
tests require an instance of Voyager to be running.  By default the integration tests will look to http://localhost:8888, 
to change this, set the `voyager.url` system property:

    ant -Dvoyager.url=http://yourhost:2345/path integration




Quickstart Distribution
-----------------------
This file is included in the standard Voyager download.  The version info is listed below:
<pre>
 @versioninfo@
</pre>














