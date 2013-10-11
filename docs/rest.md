Solr REST API
==============

Solr exposes a REST API that is semi-self-documenting via swagger.  See:

  [http://localhost:8888/api](http://localhost:8888/api)

The REST API is language agnostic, however this project has samples
using the API via java.


Discovery/Job/Index
-------------------
To manipulate the index, submit a DiscoveryJob to <code>/api/rest/discovery/job/index</code>

For samples see: [RestIntegrationTest](../src/test/java/voyager/quickstart/discovery/RestIntegrationTest.java)






