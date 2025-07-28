package com.securaccess.enterprise.resources;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")
public class ApplicationConfig extends Application {
    // Configuration will be automatically scanned for @Path annotated classes
}