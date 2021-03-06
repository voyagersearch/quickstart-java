package voyager.quickstart.discovery;

import java.io.File;
import java.io.PrintStream;
import java.util.Date;
import voyager.api.discovery.jobs.DiscoveryAction;
import voyager.api.discovery.jobs.DiscoveryJob;
import voyager.api.domain.model.entry.DexField;
import voyager.api.domain.model.entry.Entry;
import voyager.api.domain.model.entry.EntryGeo;
import voyager.api.domain.model.entry.EntryLink;
import voyager.api.domain.model.entry.EntryMeta;
import voyager.api.mime.VoyagerMimeTypes;

public class JobSamples {
  
  /**
   * We will use the Docs folder for reference
   */
  private static File docs = null;
  public synchronized static File getDocsFolder()
  {
    if(docs==null) {
      String[] check = new String[] {
          "docs",
          "../docs"
      };
      
      for(String name : check) {
        docs = new File(name);
        if(docs.exists()) {
          try {
            docs = docs.getCanonicalFile();
          }
          catch(Exception ex) {
            ex.printStackTrace();
          }
          break;
        }
      }
    }
    return docs;
  }

  public static DiscoveryJob makeCommitJob()
  {
    DiscoveryJob job = new DiscoveryJob();
    job.setAction(DiscoveryAction.ADD);
    return job;
  }
  

  public static DiscoveryJob makeDelteJob(String id)
  {
    DiscoveryJob job = new DiscoveryJob();
    job.setAction(DiscoveryAction.DELETE);
    job.setId(id);
    return job;
  }
  

  public static DiscoveryJob makeAddFile()
  {
    File f = new File(getDocsFolder(), "imgs/links.png");
    
    DiscoveryJob job = new DiscoveryJob();
    job.setAction(DiscoveryAction.ADD);
    job.setPath(f.getAbsolutePath());
    
    Entry entry = new Entry();
    entry.setField(DexField.FORMAT, "image/png");
    entry.setField(DexField.BYTES, f.length());
    
    job.setEntry(entry);
    return job;
  }
  

  public static DiscoveryJob makeAddURL()
  {
    DiscoveryJob job = new DiscoveryJob();
    job.setAction(DiscoveryAction.ADD);
    job.setPath("http://public.voyagergis.com/kml/TheBestPlaces.kml");
    
    Entry entry = new Entry();
    entry.setField(DexField.NAME, "TheBestPlaces");
    entry.setField(DexField.FORMAT, VoyagerMimeTypes.KML);
    
    job.setEntry(entry);
    return job;
  }
  // 
  
    
  public static DiscoveryJob makeAddRecordWithoutFile()
  {
    DiscoveryJob job = new DiscoveryJob();
    job.setAction(DiscoveryAction.ADD);
    job.setId("record1234");
    
    Entry entry = new Entry();
    entry.setField(DexField.NAME, "Name ("+System.currentTimeMillis()+")");
    entry.setField(DexField.ABSTRACT, "some longer text about what we have");
    entry.setField(DexField.COPYRIGHT, "some copyright message");
    entry.setExtent(new EntryGeo(37.78875904932722, -122.38787244901528));
    
    // Point to raw metadata
    EntryMeta meta = new EntryMeta();
    meta.setBody("<xml>raw XML metadata</xml>");
   // meta.setPath("c:/path/to/meta.xml");
    entry.setMeta(meta);
    
    job.setEntry(entry);
    return job;
  }

  public static DiscoveryJob makeAddRecordTree()
  {
    DiscoveryJob job = new DiscoveryJob();
    job.setAction(DiscoveryAction.ADD);
    job.setId("tree_1234");
    
    Entry entry = new Entry();
    entry.setField(DexField.NAME, "Tree Root");
    entry.setField(DexField.ABSTRACT, "some longer text about what we have");
    job.setEntry(entry);

    Entry sub = new Entry();
    sub.setIndex(false); // this will not get its own entry in the index
    sub.setField(DexField.NAME, "Sub 1");
    sub.setField(DexField.FORMAT, VoyagerMimeTypes.FOLDER);
    entry.addChild(sub);
    
    for( int i=0; i<3; i++) {
      Entry subsub = new Entry();
      subsub.setField(DexField.NAME, "Child "+i);
      subsub.setField(DexField.ABSTRACT, "more info in an entry ("+i+")");
      sub.addChild(subsub);
    }
    return job;
  }
  

  public static DiscoveryJob makeAddRecordWithLinks()
  {
    DiscoveryJob job = new DiscoveryJob();
    job.setAction(DiscoveryAction.ADD);
    job.setId("with_links_1234");
    
    Entry entry = new Entry();
    entry.setField(DexField.NAME, "Item With Links");
    entry.setField(DexField.ABSTRACT, "some longer text about what we have");
    
    EntryGeo extent = new EntryGeo(-88.59375, 24.766785, -78.222656, 31.128199);
    entry.setExtent(extent);
    job.setEntry(entry);
    
    File docs = getDocsFolder();
    EntryLink link = new EntryLink();
    link.setName("linked data");
    link.setFormat("image/png");
    link.setPath(new File(docs,"imgs/links.png").getAbsolutePath());
    link.setRelation("data");
    entry.addLink(link);
    
    link = new EntryLink();
    link.setName("tree data");
    // with a path and no 'format' we will try to detect it
    link.setPath(new File(docs,"imgs/structure.png").getAbsolutePath());
    link.setRelation("data");
    entry.addLink(link);
    
    return job;
  }

  public static DiscoveryJob makeAddRecordWithImageURL()
  {
    DiscoveryJob job = new DiscoveryJob();
    job.setAction(DiscoveryAction.ADD);
    job.setId("with_image_1234");
    
    Entry entry = new Entry();
    entry.setField(DexField.NAME, "An Item with a thumbnail");
    entry.setField(DexField.IMAGE_URL, "https://raw.githubusercontent.com/voyagersearch/quickstart-java/master/docs/imgs/eclipse_2_set_variable.png");
    job.setEntry(entry);
    return job;
  }

  public static DiscoveryJob makeAddRecordTreeAndExtract()
  {
    DiscoveryJob job = new DiscoveryJob();
    job.setAction(DiscoveryAction.ADD);
    job.setId("tree_and_extract");
    
    Entry entry = new Entry();
    entry.setField(DexField.NAME, "Tree Root");
    entry.setField(DexField.ABSTRACT, "some longer text about what we have");
    job.setEntry(entry);

    Entry sub = new Entry();
    sub.setIndex(false); // this will not get its own entry in the index
    sub.setField(DexField.NAME, "Sub 1");
    sub.setField(DexField.FORMAT, VoyagerMimeTypes.FOLDER);
    entry.addChild(sub);
    
    File docs = getDocsFolder();
    Entry e1 = new Entry();
    e1.setName("The Links PNG");
    e1.setField( DexField.FORMAT, "image/png");
    e1.setField( DexField.TITLE, "Some Title From the Job");
    e1.setPath(new File(docs,"imgs/links.png").getAbsolutePath());
    e1.setField(DexField.TO_EXTRACT, true);
    sub.addChild(e1);
    
    e1 = new Entry();
    e1.setName("A Structure PNG");
    e1.setField( DexField.ABSTRACT, "an abstract field");
    e1.setPath(new File(docs,"imgs/structure.png").getAbsolutePath());
    e1.setField(DexField.TO_EXTRACT, true);
    sub.addChild(e1);
    
    return job;
  }
  
  
  
  public static void write(PrintStream out) throws Exception
  {

//    // Add a record without file reference
//    postJobCommitAndVerifyIndex(JobSamples.makeAddRecordWithoutFile());
//   
//    // Record with a tree
//    postJobCommitAndVerifyIndex(JobSamples.makeAddRecordTree());
//
//    // Record with links to data
//    postJobCommitAndVerifyIndex(JobSamples.makeAddRecordWithLinks());
    
    out.println("Sample Discovery Jobs");
    out.println("=====================");
    out.println("> This page was autogenerated by [JobSamples.java](../src/main/java/voyager/quickstart/discovery/JobSamples.java) on "+new Date());
    out.println("");
    
    out.println("\n## Add a file");
    out.println("Add a file to the system");
    out.println( "```json" );
    out.println( makeAddFile().toPrettyJSON() );
    out.println( "```" );

    out.println("\n## Add a URL");
    out.println("Add a URL to the system");
    out.println( "```json" );
    out.println( makeAddURL().toPrettyJSON() );
    out.println( "```" );

    out.println("\n## Add record without a resource reference");
    out.println("Fill in all the explicit properties");
    out.println( "```json" );
    out.println( makeAddRecordWithoutFile().toPrettyJSON() );
    out.println( "```" );
    
    out.println("\n## Entry with structure");
    out.println("children with <code>index: false</code> will appear in the tree, but not have their own record");
    out.println( "\n```json" );
    out.println( makeAddRecordTree().toPrettyJSON() );
    out.println( "```" );
    out.println( "The tree structure is displayed in the ui as:");
    out.println( "\n![structure](imgs/structure.png)\n");


    out.println("\n## Entry with links");
    out.println("Items with links to other data.");
    out.println( "\n```json" );
    out.println( makeAddRecordWithLinks().toPrettyJSON() );
    out.println( "```" );
    out.println( "Links are displayed in the ui as:");
    out.println( "\n![structure](imgs/links.png)\n");
    

    out.println("\n## Entry with a preview/thumbnail image");
    out.println( "\n```json" );
    out.println( makeAddRecordWithImageURL().toPrettyJSON() );
    out.println( "```" );
    out.println( "NOTE: setting the field '"+DexField.PATH_TO_THUMB.name+"' expects the thumbnail to be in the ${meta.dir} folder");
    
    
    out.println("\n## Entry with structure and child documents get indexed");
    out.println("When you add the '"+DexField.TO_EXTRACT.name+"', field, the child items are added to the extraction queue.");
    out.println( "\n```json" );
    out.println( makeAddRecordTreeAndExtract().toPrettyJSON() );
    out.println( "```" );
    out.println( "After extraction, all fields configured by the parent will exist in the child.");
  }
  
  
  public static void main(String[] args) throws Exception
  {
    write(System.out);
  }
}
