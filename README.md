# smart-campus-api
# JAX-RS RESTful API for managing campus rooms and sensors

- Module: 5COSC022W Client-Server Architectures

- Author: Dewthilini Wanniarachchi 

- UOW ID: w2120161

- IIT ID: 20240939

- GitHub: https://github.com/Dewthilini-D/smart-campus-api

##  Overview

Smart-Campus-API is a RESTful web service designed to manage smart campus resources such as sensors and rooms. It provides endpoints to handle data efficiently in a client-server architecture.

## Key Features

- Manage campus rooms
- Handle sensor data
- RESTful API endpoints
- JSON-based communication
- Scalable backend system

## Technology Stack

- Java 25

- JAX-RS (Jersy 2.32)

- Jackson (JSON)

- Maven

# How to Build and Run

## Prerequisites

- Java 25 installed

- Maven installed

- Git installed

### Step 1 - Clone Repository  

```

git clone https://github.com/Dewthilini-D/smart-campus-api.git

```

### Step 2 - Build the Project

```
mvn clean install

```

### Step 3 - Run the server

```
java -jar target/Smart-Campus-API-1.0-SNAPSHOT.jar

```

### Step 4 - Access the API

The server will start on port 8080.

```
http://localhost:8080/Smart-Campus-API/api/v1

```

### How to Build and Run Your Own PC

- Install JDK 8+
- Install Apache Tomcat
- Clone/download repository
- Build project using Maven
- Deploy WAR file to Tomcat
- Start server and access API

## Project Structure

```
Smart-campus-api/
├── pom.xml
└── src/main/java/com/smartcampus/
    ├── Main.java                    
    ├── MyApplication.java           
    ├── model/
    │   ├── Room.java
    │   ├── Sensor.java
    │   └── SensorReading.java
    ├── storage/
    │   └── DataStore.java          
    ├── resource/
    │   ├── DiscoveryResource.java   
    │   ├── RoomResource.java        
    │   ├── SensorResource.java      
    │   └── SensorReadingResource.java
    ├── exception/
    │   ├── RoomNotEmptyException.java
    │   ├── LinkedResourceNotFoundException.java
    │   └── SensorUnavailableException.java
    ├── mapper/
    │   ├── RoomNotEmptyExceptionMapper.java           
    │   ├── LinkedResourceNotFoundExceptionMapper.java 
    │   ├── SensorUnavailableExceptionMapper.java      
    │   ├── NotFoundExceptionMapper.java               
    │   └── GlobalExceptionMapper.java                
    └── filter/
        └── LoggingFilter.java       
```

---

## Full Endpoint Reference

| Method |               Endpoint                | Description                                        | Success Code |
| :----- | :-----------------------------------: | :------------------------------------------------- | :----------: |
| GET    |               `/api/v1`               | HATEOAS discovery endpoint                         |     200      |
| GET    |            `/api/v1/rooms`            | Retrieve all rooms                                 |     200      |
| POST   |            `/api/v1/rooms`            | Create a new room                                  |     201      |
| GET    |       `/api/v1/rooms/{roomId}`        | Get a specific room by ID                          |     200      |
| DELETE |       `/api/v1/rooms/{roomId}`        | Delete a room (blocked if ACTIVE sensors assigned) |     200      |
| GET    |           `/api/v1/sensors`           | Retrieve all sensors                               |     200      |
| GET    |     `/api/v1/sensors?type={type}`     | Filter sensors by type                             |     200      |
| POST   |           `/api/v1/sensors`           | Register a new sensor (validates roomId)           |     201      |
| GET    |     `/api/v1/sensors/{sensorId}`      | Get a specific sensor by ID                        |     200      |
| GET    | `/api/v1/sensors/{sensorId}/readings` | Get all readings for a specific sensor             |     200      |
| POST   | `/api/v1/sensors/{sensorId}/readings` | Add a new reading (updates sensor currentValue)    |     201      |

# Report - Question Answers

## Part 1.1 - JAX-RS Resource Lifecycle

JAX-RS uses a per-request lifecycle, a brand-new instance of the resource class is created for every incoming HTTP request and discarded afterward. It is not a singleton. This directly impacts data management: since each request gets its own resource instance, cannot store shared data inside the resource class itself. code handles this correctly by using the Singleton DataStore, accessed via DataStore. getInstance(), which holds all data in ConcurrentHashMap.

ConcurrentHashMap is thread-safe and prevents race conditions when multiple requests access or modify data simultaneously. If a plain HashMap were used, two concurrent POST requests could both pass the existence check and insert duplicate data or corrupt the map's internal structure entirely, causing data loss.

## Part 1.2 - HATEOAS

HATEOAS means that API responses embed navigational links to related resources directly within response body, rather than forcing clients to rely on static external documentation. DiscoveryResource demonstrates this principle clearly by including a _links map in response that contains self, rooms, and sensors entries pointing to their respective API paths and embedding this under metadata. put ("_links", links).
This approach offers several advantages over static documentation. First, API becomes entirely self-describing the client that calls /api/v1 receives all available endpoints directly in the response itself, meaning no external document is needed to understand what the API offers. Second, it significantly reduces coupling between the client and server. If a URL path changes in a future version, clients only need to read the discovery endpoint to get the updated links, rather than hunting through codebase to update hard-coded strings. Third, it greatly improves explorability new developers can navigate the entire API simply by following links from one response to the next, in much the same way a user browses a website by clicking hyperlinks rather than memorizing URLs. Finally, the API can evolve gracefully over time since new links can be added to responses without breaking existing clients that simply ignore link keys, do not yet recognise.

## Part 2.1 Full Room Object vs Only ID s

When returning a list of rooms, there are two main design choices to consider: returning only the room IDs or returning the complete room objects. Returning only IDs produces a very minimal payload and conserves bandwidth, but it forces the client to make additional GET requests, one per room, just to retrieve the actual data it needs, which greatly increases network round trips and places a higher burden of complexity on the client side to aggregate all those responses. In contrast, returning full room objects as your ‘getAllRooms()’ implementation does results in a slightly larger payload since each object carries the room's name, capacity, and sensor ID list, but teh client receives everything it needs in a single network call with no further requests or aggregation required. For the Smart Campus API, where room objects are compact and straightforward, the bandwidth overhead of returning full objects is negligible, making it the clearly superior choice. Returning IDs only would only be appropriate in scenarios where the full objects are extremely large.

## 2.2 - Is DELETE Idempotent

The DELETE method is idempotent, meaning that the intended effect of issuing teh same request multiple times should be identical to that of issuing it once. However, upon close examination of the current implementation, this property is not fully satisfied. When a DELETE request is first issued for a valid room identifier, the service locates the corresponding entry within the data store, removes it, and returns an HTTP 200 OK response, whereas a subsequent identical request fails to locate the resource and raises a ‘NotFoundException’, producing an HTTP 404 Not Found response. Although the resultant server state following both calls is equivalent in that the room no longer exists within the system, the divergence in response behaviour indicates that the implementation does not comprehensively honour the idempotency contract as formally defined. A more rigorous adherence to idempotency principles would require the service to recognise the absence of the targeted resource as the desired end state and return an HTTP 204 No content response consistently across all subsequent calls. This inconsistency is further compounded by the business logic constraint that prevents the deletion of a room retaining active sensor assignments, whereby the service raises a ‘RoomNotEmptyException’ and returns an HTTP 409 Conflict, meaning that repeated invocations of the same DELETE request may yield three semantically distinct responses - 409 Conflict, 200 OK, and 404 Not Found contingent upon the evolving state of the targeted resource, rendering the implementation state-dependent in its behaviour rather than unconditionally idempotent in the strictest sense of the specification.

## 3.1 - Usage of ` @Consumes(MediaType.APPLICATION_JSON)`

The @Consumes (MediaType.APPLICATION_JSON) annotation serves as declarative contract that instructs JAX-RS runtime to accept only  requests whose Content-Type header is explicitly declared as application/json. Should a client attempt to submit a request bearing an incompatible media type, such as text/plain or application/xml, the JAX-RS runtime performs content negotiation prior to teh invocation of any application code and automatically returns an HTTP 415 Unsupported Media Type response, ensuring that the method body is never executed, no partial data is processed, and no deserialization is attempted. This behavior is of particular significance in the context of the POST endpoints, which depend entirely upon the JAX-RS message body reader infrastructure to automatically desterilize the incoming JSON payload into the corresponding POJO, such as a Sensor or Room object. In the absence of this safeguard, a request carrying a text/plain body would provide the framework with no viable mechanism for mapping the raw string content to the expected Java type, potentially resulting in a null object being passed into the method or triggering a deserialization failure deep within the processing pipeline, either of which could produce unhandled exceptions and unpredictable application behavior. The Consumes annotation therefore functions not merely as a routing directive but as a first line of defense in ensuring data integrity, eliminating the need for manual content type validation within the method body and enforcing a clean separation between protocol-level concerns and business logic.

## 3.2 - QueryParams and PathParams Filtering

The @QueryParam approach adopted in this implementation represents the more architecturally sound and semantically correct design choice when compared to embedding teh filter criterion within the URL path itself. According to established REST conventions, path segments are intended to identify discrete resources within a hierarchy, whereas query parameters are designated for the purpose of filtering, sorting, or otherwise refining the representation of a collection resource. Encoding the filter as a path segment, such as /sensors/type/CO2, incorrectly implies that type/CO2 constitutes a distinct, addressable resource, thereby violating the principle that the resource identifier should remain stable and independent of the view being requested. By contrast, the query parameter approach correctly treats /api/v1/sensors as the canonical resource and the ?type=CO2 parameter as an optional modifier that refines the representation returned, meaning that omitting the parameter entirely and retrieving all sensors requires no additional route definition. This design also affords considerably greater composability, as multiple filter criteria can be combined naturally and intuitively within a single request, for instance ?type=CO2&status=ACTIVE, whereas an equivalent path-based approach would produce increasingly unwieldy and ambiguous URL structures as the number of filter dimensions grows. Furthermore, from a tooling and documentation perspective, query parameters are modelled as optional inputs within specifications such as Open API, accurately reflecting their optional nature, whereas path segments are treated as required identifiers, which would misrepresent the filtering behaviour to client developers and automated tooling alike. In summary, the @QueryParam approach preserves resource identity, supports optional and composable filtering, and adheres faithfully to the semantic conventions that underpin well-designed RESTful APIs.

## 4 - Architectural Benefits of the Sub-Resource Locator Pattern.

The sub-resource locator pattern demonstrated by the ‘getReadingsResource’ method delivers significant architectural advantages over consolidating all nested endpoints into a single monolithic controller. By delegating ‘/sensors/{sensorId}/readings’ requests to a dedicated ‘SensorReadingResource’, the design enforces a strict separation of concerns: the parent resource manages sensor lifecycle operations while the sub-resource exclusively handles reading-related logic, ensuring each class maintains a single. This delegation naturally prevents controller bloat, keeping components focused, readable, and easier to maintain. Additionally, the locator acts as a validation boundary; by verifying the sensor's existence upfront, it guarantees that the sub-resource always operates with a valid ‘sensorId’, eliminating redundant existence checks downstream and simplifying internal logic. The decoupled structure also greatly enhances testability, as ‘SensorReadingResource’ can be instantiated and unit-tested as a plain Java object without bootstrapping the full JAX-RS runtime. 

## 5.1 - Why is HTTP 402 more Accurate than the 404 Status Code?

Returning HTTP 422 Unprocessable Entity is significantly more semantically accurate than 404 Not Found when a client submits a payload containing a reference to a non-existent resource, such as an invalid ‘roomId’ during sensor creation. Your implementation correctly maps ‘LinkedResourceNotFoundException’ to 422 because a 404 status explicitly signals that the requested URI does not exist. Which is factually incorrect here, as the ‘/api/v1/sensors’ endpoint is active, correctly routed, and successfully received the request. In contrast, 422 precisely communicates that the server fully understood and parsed the request, confirming its syntactic, but rejected it due to a semantic flaw in the payload itself. The failure isn't a routing or endpoint issue; it's that a field within the body references a resource that doesn't exist in the system. Using 404 would mislead clients into troubleshooting URL paths or endpoint discovery, whereas 422 delivers unambiguous, actionable feedback: the request reached the correct destination and is structurally sound but contains an invalid reference that must be corrected. This semantic distinction aligns with RESTful design principles, eliminates client-side confusion, and accelerates integration by clearly separating endpoint availability from business-logic validation.

## 5.2 - Risks Accociated with Exposing Internal Java Stacktrace External API Users

‘GlobalExceptionMapper’s deliberate suppression of raw stack traces in favour of a sanitized, generic error message is a foundational security practice that prevents critical information leakage. Exposing detailed Java stack traces to clients would effectively hand attackers an architectural blueprint and technology fingerprinting would immediately reveal your reliance on Java and JAX-RS, enabling adversaries to cross-reference specific framework versions with known vulnerability databases. Internal package paths like ‘com.smartcampus.storage.DataStore’ expose system's structural layout, while precise file names and line numbers dramatically accelerate the reverse engineering of business logic and the crafting of targeted injection payloads. Furthermore, exceptions propagating from the persistence layer frequently leak sensitive data-layer metadata, including table names, column schemas, or truncated SQL queries, which can directly facilitate database enumeration or injection attacks. Stack frames also inadvertently disclose third-party dependency names and exact versions, allowing attackers to systematically hunt for associated CVEs. By strictly routing the full exception context to server-side logs (‘LOGGER.log (Level. SEVERE, "Unhandled exception occurred", exception)’) while returning only a safe, non-descriptive payload to the client, your implementation cleanly separates internal observability from external communication. This approach preserves comprehensive diagnostic visibility for engineering teams while systematically eliminating exploitable attack surface information, aligning with defines-in-depth principles and secure API design standards.

## Why Use JAX-RS Filters for Logging?

Implementing logging via a JAX-RS ‘LoggingFilter’ that implements both ‘ContainerRequestFilter’ and ‘ContainerResponseFilter’ is architecturally superior to scattering manual ‘Logger.info ()’ calls throughout individual resource methods because it centralizes a cross-cutting concern into a single, maintainable component. By adhering to the DRY principle, the filter eliminates repetitive boilerplate code that would otherwise need to be duplicated across every method in ‘RoomResource’, ‘SensorResource’, and ‘SensorReadingResource’, reducing both development effort and the risk of inconsistent logging practices. This approach enforces a clean separation of concerns: logging infrastructure remains decoupled from business logic, keeping resource methods focused, concise, and easier to reason about. Consistency and completeness are guaranteed because the filter applies universally to all requests and responses and no new endpoint can inadvertently omit logging due to developer oversight. When requirements evolve, such as adding correlation IDs, structured JSON output, or timing metrics, modifications are confined to a single class rather than requiring invasive changes across dozens of resource methods. Furthermore, JAX-RS filters support ordered chaining, enabling layered composition of infrastructure concerns like authentication, rate limiting, compression, and logging without entangling them with core application logic. This modular, declarative approach not only improves code maintainability and testability but also aligns with RESTful design principles by treating observability as a first-class, composable aspect of the request lifecycle rather than an afterthought embedded in business code.





