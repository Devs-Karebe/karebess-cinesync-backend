package karebes.movies.backend.shared.constant;

/**
 * Profile Constants
 * Contains Spring profile names
 */
public class ProfileConstants {

    private ProfileConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final String DEV = "dev";
    public static final String PROD = "prod";
    public static final String TEST = "test";
}
