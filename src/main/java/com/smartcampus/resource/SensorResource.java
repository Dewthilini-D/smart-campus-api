package com.smartcampus.resource;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.Sensor;
import com.smartcampus.storage.DataStore;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/sensors")
public class SensorResource {
    
    private final DataStore dataStore = DataStore.getInstance();
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSensors(@QueryParam("type") String type) {
        
        Collection<Sensor> allSensors = dataStore.getSensors().values();
        
        if (type != null && !type.trim().isEmpty()) {
            List<Sensor> filtered = allSensors.stream()
                    .filter(sensor -> type.equalsIgnoreCase(sensor.getType()))
                    .collect(Collectors.toList());
            return Response.ok(filtered).build();
        }
        
        List<Sensor> sensorList = new ArrayList<>(allSensors);
        return Response.ok(sensorList).build();
    }
    
    @GET
    @Path("/{sensorId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor sensor = dataStore.getSensor(sensorId);
        
        if (sensor == null) {
            throw new NotFoundException("Sensor with ID '" + sensorId + "' not found");
        }
        
        return Response.ok(sensor).build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSensor(Sensor sensor) {
        
        if (sensor.getId() == null || sensor.getId().trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Sensor ID is required");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
        
        if (sensor.getRoomId() == null || sensor.getRoomId().trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Room ID is required");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
        
        if (dataStore.sensorExists(sensor.getId())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Sensor with ID '" + sensor.getId() + "' already exists");
            return Response.status(Response.Status.CONFLICT).entity(error).build();
        }
        
        if (!dataStore.roomExists(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException("Room", sensor.getRoomId());
        }
        
        if (sensor.getStatus() == null || sensor.getStatus().trim().isEmpty()) {
            sensor.setStatus("ACTIVE");
        }
        
        dataStore.addSensor(sensor);
        
        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }
    
    @DELETE
    @Path("/{sensorId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteSensor(@PathParam("sensorId") String sensorId) {
        
        if (!dataStore.sensorExists(sensorId)) {
            throw new NotFoundException("Sensor with ID '" + sensorId + "' not found");
        }
        
        dataStore.removeSensor(sensorId);
        
        Map<String, String> successMessage = new HashMap<>();
        successMessage.put("message", "Sensor '" + sensorId + "' deleted successfully");
        return Response.ok(successMessage).build();
    }
    
    // Sub-Resource Locator
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingsResource(@PathParam("sensorId") String sensorId) {
        
        if (!dataStore.sensorExists(sensorId)) {
            throw new NotFoundException("Sensor with ID '" + sensorId + "' not found");
        }
        
        return new SensorReadingResource(sensorId);
    }
}