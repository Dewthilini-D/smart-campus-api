package com.smartcampus.mapper;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import java.util.HashMap;
import java.util.Map;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
    
    @Override
    public Response toResponse(NotFoundException exception) {
        
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("status", 404);
        errorBody.put("error", "Not Found");
        errorBody.put("message", exception.getMessage() != null 
                ? exception.getMessage() 
                : "The requested resource could not be found.");
        
        return Response.status(Response.Status.NOT_FOUND)
                .type(MediaType.APPLICATION_JSON)
                .entity(errorBody)
                .build();
    }
}