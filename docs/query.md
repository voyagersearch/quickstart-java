Query Voyager
==============


Using the OpenSearch Feed
--------------------------

The OpenSearch Atom feed can be found:
  * [http://localhost:8888/feed/atom.xml](http://localhost:8888/feed/atom.xml)
  * Use the [OpenSearch Description](http://localhost:7777/feed/description.xml) to identify the query syntax


The tests include an example reading the atom feed using [Apache Abdera](http://abdera.apache.org/).
See: [SampleQueryIntegrationTest.java](../src/test/java/voyager/quickstart/query/SampleQueryIntegrationTest.java#L51)



Direct Solr Access
-------------------

The voyager index is backed by [Apache Solr](http://lucene.apache.org/solr/).  For java access to the 
index, we reccomend using [SolrJ](https://cwiki.apache.org/confluence/display/solr/Using+SolrJ) the java
client to solr.

The Solr REST API is exposed under:
  * [http://localhost:8888/solr/v0](http://localhost:8888/solr/v0)

The tests include a simple example in [SampleQueryIntegrationTest.java](../src/test/java/voyager/quickstart/query/SampleQueryIntegrationTest.java#L33)



