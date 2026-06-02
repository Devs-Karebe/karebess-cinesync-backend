package karebes.movies.backend;

import org.junit.jupiter.api.BeforeEach;

/**
 * Base Unit Test Class
 * Provides common setup for unit tests
 */
public abstract class BaseUnitTest {

    @BeforeEach
    public void setUp() {
        // Common setup for unit tests
        initializeTest();
    }

    protected abstract void initializeTest();
}
