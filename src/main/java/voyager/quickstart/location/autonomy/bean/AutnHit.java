package voyager.quickstart.location.autonomy.bean;

public class AutnHit {
  public String reference;
  public String id;
  public String section;
  public String weight;
  public String links;
  public String database;
  public String title;
  
  public void reset() {
    reference = null;
    id = null;
    section = null;
    weight = null;
    links = null;
    database = null;
    title = null;
  }

  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getSection() {
    return section;
  }

  public void setSection(String section) {
    this.section = section;
  }

  public String getWeight() {
    return weight;
  }

  public void setWeight(String weight) {
    this.weight = weight;
  }

  public String getLinks() {
    return links;
  }

  public void setLinks(String links) {
    this.links = links;
  }

  public String getDatabase() {
    return database;
  }

  public void setDatabase(String database) {
    this.database = database;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}
