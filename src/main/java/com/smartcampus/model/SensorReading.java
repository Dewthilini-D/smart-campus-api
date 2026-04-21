package com.smartcampus.model;

import java.util.UUID;

public class SensorReading {
    
    private String id;          // Unique reading event ID (UUID)
    private long timestamp;     // Epoch time (ms) when reading was captured
    private double value;       // The actual metric value recorded
    
    // Default constructor (required for JSON deserialization)
    public SensorReading() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
    }
    
    // Constructor with value only - auto-generates ID and timestamp
    public SensorReading(double value) {
        this.id = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
        this.value = value;
    }
    
    // Full constructor
    public SensorReading(String id, long timestamp, double value) {
        this.id = id;
        this.timestamp = timestamp;
        this.value = value;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public double getValue() {
        return value;
    }
    
    public void setValue(double value) {
        this.value = value;
    }
}