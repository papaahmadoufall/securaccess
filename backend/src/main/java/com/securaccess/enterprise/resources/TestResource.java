package com.securaccess.enterprise.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/test")
public class TestResource {
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response test() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "SecurAccess Enterprise Backend is working!");
        response.put("timestamp", System.currentTimeMillis());
        response.put("version", "1.0.0");
        
        return Response.ok(response).build();
    }
    
    @GET
    @Path("/health")
    @Produces(MediaType.APPLICATION_JSON)
    public Response health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("database", "Connected");
        health.put("service", "SecurAccess Enterprise");
        
        return Response.ok(health).build();
    }
}