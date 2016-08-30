package voyager.quickstart.location.instagram;

import voyager.api.discovery.location.service.ServiceLocation;

public class InstagramLocation extends ServiceLocation {

    public static final String TYPE = "instagram";

    public static final String TOKEN_PROPETY = "acess token";

    @Override
    public String getLocationType() {
        return TYPE;
    }

}
