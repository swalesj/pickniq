package swalesj.pickniq;

/**
 * Created by swalesj
 */

    public final class AppConfig {

        public static final String TAG = "gplaces";

        public static final String RESULTS = "results";
        public static final String STATUS = "status";

        public static final String OK = "OK";
        public static final String ZERO_RESULTS = "ZERO_RESULTS";
        public static final String REQUEST_DENIED = "REQUEST_DENIED";
        public static final String OVER_QUERY_LIMIT = "OVER_QUERY_LIMIT";
        public static final String UNKNOWN_ERROR = "UNKNOWN_ERROR";
        public static final String INVALID_REQUEST = "INVALID_REQUEST";

        //    Key for nearby places json from google
        public static final String GEOMETRY = "geometry";
        public static final String LOCATION = "location";
        public static final String LATITUDE = "lat";
        public static final String LONGITUDE = "lng";
        public static final String ICON = "icon";
        public static final String SUPERMARKET_ID = "id";
        public static final String NAME = "name";
        public static final String PLACE_ID = "place_id";
        public static final String REFERENCE = "reference";
        public static final String VICINITY = "vicinity";
        public static final String RATING = "rating";
        public static final String TYPES = "types";
        public static final String OPEN_NOW = "open_now";
        public static final String PRICE_LEVEL = "price_level";
        public static final String PLACE_NAME = "place_name";

        // remember to change the browser api key
        public static final String GOOGLE_BROWSER_API_KEY =
                "AIzaSyBQE86eAF8UylrcBwy7WQtJLDSUjQAaJLc";
        public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
        public static final int PROXIMITY_RADIUS = 5000;
        // The minimum distance to change Updates in meters
        public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
        // The minimum time between updates in milliseconds
        public static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    }
