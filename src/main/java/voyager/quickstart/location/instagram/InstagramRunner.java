package voyager.quickstart.location.instagram;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;

import org.apache.commons.httpclient.auth.AuthenticationException;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import voyager.api.discovery.jobs.JobSubmitter;
import voyager.api.domain.model.entry.DexField;
import voyager.api.domain.model.entry.Entry;
import voyager.api.domain.model.entry.EntryGeo;
import voyager.discovery.location.BaseDiscoveryRunner;

public class InstagramRunner extends BaseDiscoveryRunner<InstagramLocation> {

    static final Logger log = LoggerFactory.getLogger(InstagramRunner.class);

    public InstagramRunner(InstagramLocation location, SolrClient solr, JobSubmitter jobs) {
        super(location, solr, jobs);
    }

    @Override
    protected void doCrawl() {
        String token = this.location.getProperty(InstagramLocation.TOKEN_PROPETY);
        InputStream stream;
        try {
            URL dataurl = new URL("https://api.instagram.com/v1/users/self/media/recent?access_token=" + token);
            stream = dataurl.openStream();
            processInstagramJson(stream);
        } catch (Exception e) {            
            log.error("Error reading feed, might be invalid Token", e);
            return;
        }            
    }

    protected void processInstagramJson(InputStream stream) throws IOException, ParseException, SolrServerException, AuthenticationException {

        int i = 0;
        setProcessedCount(i);      
        BufferedReader in = new BufferedReader(new InputStreamReader(stream));

        JSONParser parser = new JSONParser();

        JSONObject jsonObject = (JSONObject) parser.parse(in);

        JSONArray data = (JSONArray)jsonObject.get("data");

        if (data != null) {       

            Iterator<JSONObject> items = data.iterator();
            setItemsToProcess(data.size());

            while (items.hasNext()) {
                JSONObject item = items.next();

                /* Location */
                JSONObject loc = (JSONObject) item.get("location");
                Double lat = (Double) loc.get("latitude");
                Double lon = (Double) loc.get("longitude");
                String name = (String) loc.get("name");

                /* Image */
                JSONObject image = (JSONObject) item.get("images");
                JSONObject st_resolution = (JSONObject) image.get("standard_resolution");
                String url = (String) st_resolution.get("url");
                JSONObject thumbnail = (JSONObject) image.get("thumbnail");
                String url_thumbnail = (String) thumbnail.get("url");

                /* User */
                JSONObject user = (JSONObject) item.get("user");
                String full_name = (String) user.get("full_name");

                String id = (String) item.get("id");

                EntryGeo point = new EntryGeo(lat, lon);
                Entry e = new Entry();
                e.addField(DexField.ID, id);
                e.addField(DexField.AUTHOR, full_name);
                e.addField(DexField.URI, url);
                e.addField(DexField.FORMAT, "jpg");
                e.addField(DexField.IMAGE_URL, url_thumbnail);
                e.addField(DexField.SPATIAL_REFERENCE, name);              
                e.setExtent(point);

                addDoc(e);
                setProcessedCount(++i);
            }
        } else {
            throw new ParseException(ParseException.ERROR_UNEXPECTED_TOKEN);
        }
    }

}
