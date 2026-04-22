package com.smartcampus.resource;

import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.Room;
import com.smartcampus.storage.DataStore;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/rooms")
public class RoomResource {
    
    private final DataStore dataStore = DataStore.getInstance();
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRooms() {
        Collection<Room> allRooms = dataStore.getRooms().values();
        List<Room> roomList = new ArrayList<>(allRooms);
        return Response.ok(roomList).build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRoom(Room room) {
        
        if (room.getId() == null || room.getId().trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Room ID is required");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
        
        if (dataStore.roomExists(room.getId())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Room with ID '" + room.getId() + "' already exists");
            return Response.status(Response.Status.CONFLICT).entity(error).build();
        }
        
        if (room.getSensorIds() == null) {
            room.setSensorIds(new ArrayList<>());
        }
        
        dataStore.addRoom(room);
        
        return Response.status(Response.Status.CREATED).entity(room).build();
    }
    
    @GET
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = dataStore.getRoom(roomId);
        
        if (room == null) {
            throw new NotFoundException("Room with ID '" + roomId + "' not found");
        }
        
        return Response.ok(room).build();
    }
    
    @DELETE
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        
        Room room = dataStore.getRoom(roomId);
        
        if (room == null) {
            throw new NotFoundException("Room with ID '" + roomId + "' not found");
        }
        
        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(roomId, room.getSensorIds().size());
        }
        
        dataStore.removeRoom(roomId);
        
        Map<String, String> successMessage = new HashMap<>();
        successMessage.put("message", "Room '" + roomId + "' deleted successfully");
        return Response.ok(successMessage).build();
    }
}