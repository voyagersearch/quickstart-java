package voyager.quickstart.location.instagram;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.solr.client.solrj.SolrClient;

import com.google.common.base.Throwables;

import voyager.api.discovery.DiscoveryRunner;
import voyager.api.discovery.jobs.JobSubmitter;
import voyager.api.discovery.location.LocationFactory;
import voyager.api.infrastructure.util.Registry;

public class InstagramLocationFactory implements LocationFactory<InstagramLocation> {

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return InstagramLocation.TYPE;
    }

    @Override
    public InstagramLocation newInstance() {
        return new InstagramLocation();

    }

    @Override
    public DiscoveryRunner<?> newRunner(InstagramLocation loc, boolean delat) throws IOException {
        // TODO Auto-generated method stub
        SolrClient solr = Registry.get(SolrClient.class);
        JobSubmitter jobs = Registry.get(JobSubmitter.class);
        return new InstagramRunner(loc, solr, jobs);
    }

    @Override
    public InstagramLocation newSampleInstance() {

        /**
         * Instagram API is based on token that needs to be validated by the
         * user. To get your token go to:
         * https://www.instagram.com/oauth/authorize/?client_id=92db26033aec47fc8d5a6416ed1e8555&redirect_uri=http://voyagerdemo.com/&response_type=token
         * and copy the token from the received link. *
         */

        InstagramLocation a = new InstagramLocation();
        a.setName("Instagram name");
        a.setProperty(InstagramLocation.TOKEN_PROPETY, "3736757737.92db260.af73dd84045e4d378cbacc29c39a7023");

        try {

            /**
             * Instagram URL is static (just needs a token) so this is not
             * needed however it seems to be obligatory
             *
             */
            a.setURI(new URI("http://your-host-name-and-port"));
        } catch (URISyntaxException e) {
            Throwables.propagate(e);
        }
        return a;
    }

    @Override
    public void validate(InstagramLocation arg0) throws Exception {
        // TODO Auto-generated method stub

    }

}
