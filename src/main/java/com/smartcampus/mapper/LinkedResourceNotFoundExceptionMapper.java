package com.smartcampus.mapper;

import com.smartcampus.exception.LinkedResourceNotFoundException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import java.util.HashMap;
import java.util.Map;

@Provider
public class LinkedResourceNotFoundExceptionMapper 
        implements ExceptionMapper<LinkedResourceNotFoundException> {
    
    private static final int UNPROCESSABLE_ENTITY = 422;
    
    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("status", UNPROCESSABLE_ENTITY);
        errorBody.put("error", "Unprocessable Entity");
        errorBody.put("message", exception.getMessage());
        errorBody.put("referencedResource", exception.getResourceType());
        errorBody.put("referencedId", exception.getResourceId());
        errorBody.put("hint", "Ensure the referenced resource exists before creating this entity.");
        
        return Response.status(UNPROCESSABLE_ENTITY)
                .type(MediaType.APPLICATION_JSON)
                .entity(errorBody)
                .build();
    }
}