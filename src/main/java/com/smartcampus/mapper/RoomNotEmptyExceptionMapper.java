package com.smartcampus.mapper;

import com.smartcampus.exception.RoomNotEmptyException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import java.util.HashMap;
import java.util.Map;

@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {
    
    @Override
    public Response toResponse(RoomNotEmptyException exception) {
        
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("status", 409);
        errorBody.put("error", "Conflict");
        errorBody.put("message", exception.getMessage());
        errorBody.put("roomId", exception.getRoomId());
        errorBody.put("activeSensorCount", exception.getSensorCount());
        errorBody.put("hint", "Remove or reassign all sensors before deleting the room.");
        
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(errorBody)
                .build();
    }
}