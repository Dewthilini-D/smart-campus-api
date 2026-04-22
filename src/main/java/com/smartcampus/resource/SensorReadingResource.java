package com.smartcampus.resource;

import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.storage.DataStore;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;

public class SensorReadingResource {
    
    private final DataStore dataStore = DataStore.getInstance();
    private final String sensorId;
    
    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllReadings() {
        List<SensorReading> readings = dataStore.getReadingsForSensor(sensorId);
        return Response.ok(readings).build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading) {
        
        Sensor sensor = dataStore.getSensor(sensorId);
        
        String status = sensor.getStatus();
        if ("MAINTENANCE".equalsIgnoreCase(status) || "OFFLINE".equalsIgnoreCase(status)) {
            throw new SensorUnavailableException(sensorId, status);
        }
        
        dataStore.addReading(sensorId, reading);
        sensor.setCurrentValue(reading.getValue());
        
        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}