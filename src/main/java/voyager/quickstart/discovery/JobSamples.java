package voyager.quickstart.discovery;

import java.util.Random;

import voyager.api.discovery.jobs.DiscoveryAction;
import voyager.api.discovery.jobs.DiscoveryJob;
import voyager.api.domain.model.entry.DexField;
import voyager.api.domain.model.entry.Entry;
import voyager.api.domain.model.entry.EntryLink;
import voyager.api.domain.model.entry.EntryMeta;

public class JobSamples {

  public static DiscoveryJob makeCommitJob()
  {
    DiscoveryJob job = new DiscoveryJob();
    job.setAction(DiscoveryAction.ADD);
    return job;
  }
    
  public static DiscoveryJob makeAddSimpleRectord()
  {
    DiscoveryJob job = new DiscoveryJob();
    job.setAction(DiscoveryAction.ADD);
    job.setId("simple_1234");
    
    Entry entry = new Entry();
    entry.setField(DexField.NAME, "Name ("+System.currentTimeMillis()+")");
    entry.setField(DexField.ABSTRACT, "some longer text about what we have");
    
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
    entry.setField(DexField.NAME, "Root Tree ("+System.currentTimeMillis()+")");
    entry.setField(DexField.ABSTRACT, "some longer text about what we have");
    job.setEntry(entry);
    
    Random rand = new Random();
    for(int i=0; i<10; i++) {
      int v = rand.nextInt(10);
      Entry sub = new Entry("sub"+v);
      if((v%2)==0) {
        // This just displays in the tree.  
        // It does not get a record in the index
        sub.setIndex(false);
      }
      else {
        sub.setField(DexField.AUTHOR, "This is the author tag");
      }
      entry.addChild(sub);
    }
    return job;
  }
  

  public static DiscoveryJob makeAddRecordWithLinks()
  {
    DiscoveryJob job = new DiscoveryJob();
    job.setAction(DiscoveryAction.ADD);
    job.setId("with_links_1234");
    
    Entry entry = new Entry();
    entry.setField(DexField.NAME, "Link ("+System.currentTimeMillis()+")");
    entry.setField(DexField.ABSTRACT, "some longer text about what we have");
    job.setEntry(entry);
    
    EntryLink link = new EntryLink();
    link.setName("linked data");
    link.setPath("c:/path/to/data.xyz");
    link.setRelation("data");
    entry.addLink(link);
    
    link = new EntryLink();
    link.setName("linked data2");
    link.setPath("c:/path/to/data2.xyz");
    link.setRelation("data");
    entry.addLink(link);
    
    return job;
  }
  
  
  public static void main(String[] args) throws Exception
  {
    DiscoveryJob job = makeAddSimpleRectord();
    System.out.println("\n#### Simple Discovery Job");
    System.out.println("A simple job with no external file reference");
    System.out.println( "```json" );
    System.out.println( job.toPrettyJSON() );
    System.out.println( "```" );

    System.out.println("#### Entry with internal structure");
    System.out.println("children with <code>index: false</code> will appear in the tree, but not have their own record");
    job = makeAddRecordTree();
    System.out.println( "\n```json" );
    System.out.println( job.toPrettyJSON() );
    System.out.println( "```" );
    System.out.println( "The tree structure is displayed in the ui as:");
    System.out.println( "![structure](imgs/structure.png)");


    System.out.println("#### Entry with links to data");
    System.out.println("Items with links to other data.");
    job = makeAddRecordWithLinks();
    System.out.println( "\n```json" );
    System.out.println( job.toPrettyJSON() );
    System.out.println( "```" );
    System.out.println( "Links are displayed in the ui as:");
    System.out.println( "![structure](imgs/links.png)");
  }
}
