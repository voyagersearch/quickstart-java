Custom Extractor
================

A custom extractor is used to open a file (or InputStream) and generate the initial properties
that will be passed through the indexing pipeline.



Create Extractor
----------------
For a simple first example, we extend <code>HttpEnabledExtractor</code> to create [SimpleTextExtractor.java].  
This class simply reads the input stream and puts the contents of in a `text` property.

The behavior is exercised in [SimpleTextExtractorTest.java]


Register ExtractorFactory
-------------------------
Each extractor needs a factory registed with the [Java ServiceLoader](http://docs.oracle.com/javase/6/docs/api/java/util/ServiceLoader.html)

In this example, we extend <code>AbstractJavaExtractorFactory</code> and create [SimpleTextExtractorFactory.java].  
This class is noted in [META-INF/services/voyager.api.discovery.extractor.ExtractionWorkerInfo](../src/main/resources/META-INF/services/voyager.api.discovery.extractor.ExtractionWorkerInfo).
When Voyager starts up these classes registered and should be listed on:
  >  [http://localhost:8888/manage/discovery/extractor](http://localhost:8888/manage/discovery/extractor)


Mime to Extractor Mapping
-------------------------

Extractors are mapped to mimetypes.  This mapping is managed using the [Apache Tika](http://tika.apache.org/1.4/detection.html)
content detection system.  This uses the standard [Freedesktop MIME-info XML spec](http://standards.freedesktop.org/shared-mime-info-spec/).

Voyager has extended this to support:
  * mime to extractor mapping
  * grouping component files (a shapefile is .shp + .dbf + .shx + ...)
  * format details
  * format tagging.  (keywords, product, company, application, etc)

To register your custom add a file <code>${data.dir}/config/mimetypes.xml</code> The following sample includes 
the definition for shapefiles as an example of 

```xml
<?xml version="1.0" encoding="UTF-8"?>
<mime-info>
  <mime-type type="text/plain">
    <voyager:extractor name="quickstart"  priority="70" />
  </mime-type>
</mime-info>
```

The above example simply registeres the <code>quickstart</code> extractor with the <code>text/plain</code> mimetype.

To define more properties or new mimetypes, see the following example:

```xml
<mime-type type="application/vnd.esri.shapefile">
  <acronym>SHP</acronym>
  <_comment>Shapefile</_comment>
  <glob pattern="*.shp" />
  <sub-class-of type="application/vnd.esri.dataset" />
  <tika:link>http://en.wikipedia.org/wiki/Shapefile</tika:link>
  <voyager:description>
    A vector data storage format for storing the location, shape, and attributes 
    of geographic features. A shapefile is stored in a set of related files and 
    contains one feature class.
  </voyager:description>
  <voyager:tag name="format_category">GIS</voyager:tag>
  <voyager:tag name="format_keyword">Vector</voyager:tag>
  <voyager:tag name="format_app">ArcMap</voyager:tag>
  
  <voyager:extractor name="esri/shp"  priority="100" />
  <voyager:extractor name="ogr"       priority="90" />
  <voyager:extractor name="geotools"  priority="80" />
  <voyager:components extensions="shx,dbf,prj,sbn,sbx,fbn,fbx,ain,aih,ixs,mxs,cpg,atx,xml" />
</mime-type>
```
Note that <code>voyager:components</code> specifies a list of extensions that are considered to be 
part of the `same` entry.  This means that a .dbf file on without a .shp is recognized as its own
entry



[SimpleTextExtractor.java]:        ../src/main/java/voyager/quickstart/extractor/simple/SimpleTextExtractor.java
[SimpleTextExtractorFactory.java]: ../src/main/java/voyager/quickstart/extractor/simple/SimpleTextExtractorFactory.java
[SimpleTextExtractorTest.java]:    ../src/test/java/voyager/quickstart/extractor/simple/SimpleTextExtractorTest.java





