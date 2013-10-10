Voyager Java Quickstart
========================

This project aims to show simple samples using various Voyager APIs from Java.


Custom Extractor
----------------

A custom extractor is used to open a file (or InputStream) and generate the initial properties
that will be passed through the indexing pipeline.

Java based Extractors are registed by implementing a 



Mime to Extractor Mapping
-------------------------

Extractors are mapped to mimetypes.  This mapping is managed using the (Apache Tika)[http://tika.apache.org/1.4/detection.html]
content detection system.  This uses the standard (Freedesktop MIME-info XML spec)[http://standards.freedesktop.org/shared-mime-info-spec/].
Voyager has extended this to support:
# mime > extractor mapping
# grouping component files (a shapefile is .shp + .dbf + .shx + ...)
# format details
# format tagging.  (keywords, product, company, application, etc)

To register your custom 



Discovery Jobs
--------------

Show how to submit jobs via REST api



Custom Location
---------------

Coming soon.  Implement a custom locaiton



Direct Solr Access
------------------

The voyager index is backed by [Apache Solr](http://lucene.apache.org/solr/).  For java access to the 
index, we reccomend using [SolrJ](https://cwiki.apache.org/confluence/display/solr/Using+SolrJ) the java
client to solr.



Integration Tests
=================

These samples include simple unit tests in addition to more complex integration tests.  The integration 
tests require an instance of voyager running.  By default the integration tests will look to http://localhost:8888, 
to change this, set the <code>voyager.url</code> system property:

    ant -Dvoyager.url=http://yourhost:2345/path integration

If you are running these samples from a location other than <code>${app.dir}/dev/java</code> you can 
tell ant where to find the bundled .jar files using the <code>voyager.app</code> system property

    ant -Dvoyager.app=d:\voyager\server_1.9\app 
















