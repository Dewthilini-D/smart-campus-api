package com.smartcampus;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * JAX-RS Application Configuration.
 * 
 * @ApplicationPath defines the base URI for all endpoints.
 * All resources will be accessible under /api/v1/*
 */
@ApplicationPath("/api/v1")
public class MyApplication extends Application {
    // Jersey will auto-discover all @Path annotated classes in the package
}