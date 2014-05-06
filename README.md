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


Installing in Voyager
---------------------

After running ant succesfully, a jar file will be created in:

    build/voyager-custom-extensions.jar

Copy this file to your ${app.dir}/lib/ext folder.  Alternativly you can run

    ant install
    
This will build the extension and copy it to the configured voyager instance:

    install:
         [copy] Copying 1 file to /Users/ryan/workspace/voyager/test/artifact/Voyager/app/lib/ext
         [copy] Copying /Users/ryan/workspace/quickstart-java/build/voyager-custom-extensions.jar to /Users/ryan/workspace/voyager/test/artifact/Voyager/app/lib/ext/voyager-custom-extensions.jar
    
    BUILD SUCCESSFUL
    Total time: 3 seconds

Check that your .jar file is in the ${app.dir}/lib/ext folder and restart voyager:

   ![ext folder](docs/imgs/install_jar_in_lib_ext.png)


After restarting voyager, your custom code will be loaded in Voyager.

See [Custom Locations](docs/locations.md#adding-custom-locations-from-the-ui) for how some of these extensions will appear.



Integration Tests
-----------------

#### ![warning](docs/imgs/warning_48.png) The integration tests will modify the index!

These samples include simple unit tests in addition to more complex integration tests.  The integration 
tests require an instance of Voyager to be running.  By default the integration tests will look to http://localhost:8888, 
to change this, set the `voyager.url` system property:

    ant -Dvoyager.url=http://yourhost:2345/path integration



Running Samples from Eclipse
----------------------------

This includes an eclipse project where the classpath is registered based on the variable ```VOYAGER_DIR```.  
Set an [Eclipse Classpath Variable](http://help.eclipse.org/juno/index.jsp?topic=%2Forg.eclipse.jdt.doc.user%2Freference%2Fpreferences%2Fjava%2Fbuildpath%2Fref-preferences-classpath-variables.htm) to ```VOYAGER_DIR=c:\voyager\server_1.9```


   Right click on the project to configure the build path:
   
   
   ![build path](docs/imgs/eclipse_1_configure_build_path.png)



   Configure your variable:
   
   
   ![variable](docs/imgs/eclipse_2_set_variable.png)



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










