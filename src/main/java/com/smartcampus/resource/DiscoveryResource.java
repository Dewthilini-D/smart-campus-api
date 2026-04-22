/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;

@Path("/")
public class DiscoveryResource {
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getApiMetadata() {
        
        Map<String, Object> metadata = new HashMap<>();
        
        metadata.put("apiName", "Smart Campus Sensor & Room Management API");
        metadata.put("version", "1.0.0");
        metadata.put("description", "RESTful API to manage rooms and sensors across the Smart Campus infrastructure");
        
        Map<String, String> contact = new HashMap<>();
        contact.put("name", "Smart Campus Admin");
        contact.put("email", "admin@smartcampus.edu");
        contact.put("organization", "University of Westminster");
        metadata.put("contact", contact);
        
        Map<String, String> links = new HashMap<>();
        links.put("self", "/api/v1");
        links.put("rooms", "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");
        metadata.put("_links", links);
        
        return Response.ok(metadata).build();
    }
}