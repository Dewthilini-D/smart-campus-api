package com.smartcampus.storage;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory data store for Smart Campus API.
 * Uses Singleton pattern - only one instance exists across the application.
 * 
 * IMPORTANT: We use ConcurrentHashMap instead of HashMap because JAX-RS
 * creates a new resource instance per request, and multiple requests can
 * access this data simultaneously (race conditions).
 */
public class DataStore {
    
    // Singleton instance
    private static final DataStore INSTANCE = new DataStore();
    
    // Thread-safe maps to store data
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    
    // Map: sensorId -> List of readings for that sensor
    private final Map<String, List<SensorReading>> sensorReadings = new ConcurrentHashMap<>();
    
    // Private constructor - prevents external instantiation
    private DataStore() {
        seedSampleData();
    }
    
    // Public method to get the single instance
    public static DataStore getInstance() {
        return INSTANCE;
    }
    
    // ========== ROOM OPERATIONS ==========
    
    public Map<String, Room> getRooms() {
        return rooms;
    }
    
    public Room getRoom(String id) {
        return rooms.get(id);
    }
    
    public void addRoom(Room room) {
        rooms.put(room.getId(), room);
    }
    
    public Room removeRoom(String id) {
        return rooms.remove(id);
    }
    
    public boolean roomExists(String id) {
        return rooms.containsKey(id);
    }
    
    // ========== SENSOR OPERATIONS ==========
    
    public Map<String, Sensor> getSensors() {
        return sensors;
    }
    
    public Sensor getSensor(String id) {
        return sensors.get(id);
    }
    
    public void addSensor(Sensor sensor) {
        sensors.put(sensor.getId(), sensor);
        // Also link sensor to its room
        Room room = rooms.get(sensor.getRoomId());
        if (room != null) {
            room.addSensorId(sensor.getId());
        }
        // Initialize empty readings list for this sensor
        sensorReadings.put(sensor.getId(), new ArrayList<>());
    }
    
    public Sensor removeSensor(String id) {
        Sensor removed = sensors.remove(id);
        if (removed != null) {
            // Remove sensor from its room's list
            Room room = rooms.get(removed.getRoomId());
            if (room != null) {
                room.removeSensorId(id);
            }
            // Remove all readings for this sensor
            sensorReadings.remove(id);
        }
        return removed;
    }
    
    public boolean sensorExists(String id) {
        return sensors.containsKey(id);
    }
    
    // ========== SENSOR READING OPERATIONS ==========
    
    public List<SensorReading> getReadingsForSensor(String sensorId) {
        return sensorReadings.getOrDefault(sensorId, new ArrayList<>());
    }
    
    public void addReading(String sensorId, SensorReading reading) {
        sensorReadings.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(reading);
    }
    
    // ========== SAMPLE DATA ==========
    
    private void seedSampleData() {
        // Add sample rooms
        Room room1 = new Room("LIB-301", "Library Quiet Study", 50);
        Room room2 = new Room("LAB-101", "Computer Science Lab", 30);
        Room room3 = new Room("HALL-A", "Main Lecture Hall", 200);
        
        rooms.put(room1.getId(), room1);
        rooms.put(room2.getId(), room2);
        rooms.put(room3.getId(), room3);
        
        // Add sample sensors
        Sensor sensor1 = new Sensor("TEMP-001", "Temperature", "ACTIVE", 22.5, "LIB-301");
        Sensor sensor2 = new Sensor("CO2-001", "CO2", "ACTIVE", 400.0, "LIB-301");
        Sensor sensor3 = new Sensor("OCC-001", "Occupancy", "ACTIVE", 15.0, "LAB-101");
        Sensor sensor4 = new Sensor("TEMP-002", "Temperature", "MAINTENANCE", 0.0, "HALL-A");
        
        sensors.put(sensor1.getId(), sensor1);
        sensors.put(sensor2.getId(), sensor2);
        sensors.put(sensor3.getId(), sensor3);
        sensors.put(sensor4.getId(), sensor4);
        
        // Link sensors to their rooms
        room1.addSensorId("TEMP-001");
        room1.addSensorId("CO2-001");
        room2.addSensorId("OCC-001");
        room3.addSensorId("TEMP-002");
        
        // Initialize empty readings list for each sensor
        sensorReadings.put("TEMP-001", new ArrayList<>());
        sensorReadings.put("CO2-001", new ArrayList<>());
        sensorReadings.put("OCC-001", new ArrayList<>());
        sensorReadings.put("TEMP-002", new ArrayList<>());
    }
}