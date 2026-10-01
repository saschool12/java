package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AppTest {
    @Test
    void appHasAGreeting() {
        App classUnderTest = new App();
        assertNotNull(classUnderTest.getGreeting(), "app should have a greeting");
        assertEquals("Hello, World! Java Web Server is running.", classUnderTest.getGreeting());
    }

    @Test
    void appConfiguresPort() {
        App app = new App(9090);
        assertEquals(9090, app.getPort());
    }
}
