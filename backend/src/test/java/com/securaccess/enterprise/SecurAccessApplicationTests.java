package com.securaccess.enterprise;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class SecurAccessApplicationTests {

    @Test
    void contextLoads() {
        // This test verifies that the Spring application context loads successfully
        // No additional logic needed - if context fails to load, the test will fail
    }

    @Test
    void applicationStarts() {
        // This test ensures the application can start without errors
        // Success is implicit if the test runs without throwing exceptions
        assert true;
    }
}