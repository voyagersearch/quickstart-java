Working With DiscoveryJobs
==========================

A DiscoveryJob is the structure/message passed around that will support
 * finding data
 * reading file properties
 * transforming metadata
 * index manipulation

See: [Sample Discovery Jobs](discoveryjob-examples.md) for more examples

A Discovery Job looks like:
```json
{
  "id": "itemid",
  "path": "path/to/item",
  "action": "ADD",
  "entry": {
    >> the entry information goes here <<
  }
}
```

ID or Path
----------
When adding data to Voyager you need to give each item a unique ID or a path to the item and Voyager will generate an ID based on the path.

When adding nested data, only the root item requires a path or ID.  Nested data will generate ids based on the root.


Discovery Action
----------------

The possible Actions are:

  * `ADD` this will add (or replace) the item in the index
  * `UPDATE` this will pull out the existing content and add new fields to the entry
  * `DELETE` remove an item from the index.  If this is the parent of tree, the children will also be removed
  * `COMMIT` this will issue a solr [<commit/>](http://wiki.apache.org/solr/UpdateXmlMessages#A.22commit.22_and_.22optimize.22) command, making any index changes visible in the search engine.  Care should be taken to not call commit too often.


Entry
------
The Entry class holds all the key information about the item that will be indeded.  The entry is essentially a
[SolrInputDocument](http://svn.apache.org/repos/asf/lucene/dev/trunk/solr/solrj/src/java/org/apache/solr/common/SolrInputDocument.java) that has been extended to support:
  * geospatial content
  * nested documents
  * links to other entries
  * metadata references (direct or via resource)

























