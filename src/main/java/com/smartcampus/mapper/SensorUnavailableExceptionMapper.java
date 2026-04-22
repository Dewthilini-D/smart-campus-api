package com.smartcampus.mapper;

import com.smartcampus.exception.SensorUnavailableException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import java.util.HashMap;
import java.util.Map;

@Provider
public class SensorUnavailableExceptionMapper 
        implements ExceptionMapper<SensorUnavailableException> {
    
    @Override
    public Response toResponse(SensorUnavailableException exception) {
        
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("status", 403);
        errorBody.put("error", "Forbidden");
        errorBody.put("message", exception.getMessage());
        errorBody.put("sensorId", exception.getSensorId());
        errorBody.put("currentStatus", exception.getStatus());
        errorBody.put("hint", "Sensor must be ACTIVE to accept readings.");
        
        return Response.status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON)
                .entity(errorBody)
                .build();
    }
}