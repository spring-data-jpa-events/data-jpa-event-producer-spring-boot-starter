# data-jpa-event-producer-spring-boot-starter

This is a Spring Boot starter for automatically configuring an event producer linked to the data-jpa entity changes. The goal is to let developper quickly generate CRUD events for every entity changes without having to deal with transactions issues and extra code.

This is an opiniated implemention not meant to solve all event-driven usecases.

## Dependency
__Warning:__ _Not available in maven central yet but can be fetched from github repo._

### maven
``` xml
<dependency>
  <groupId>com.github.spring-data-jpa-events</groupId>
  <artifactId>data-jpa-event-producer-spring-boot-starter</artifactId>
  <version>0.0.1</version>
</dependency>
```
### gradle
``` yaml
implementation 'com.github.spring-data-jpa-events:data-jpa-event-producer-spring-boot-starter:0.0.1'
```

## Example

See the sample-app module for a fully working example.

Once you added the starter as a dependency to your project you can simply add the following annotation `@KafkaEvents` to a JPA entity and the lib will start publishing CREATE/UPDATE/DELETE events for it. 

The lib only support Kafka as of today:
``` java
@Entity
@KafkaEvents(topic = "organization")
@Table(name = "organization")
@Data
public class Organization {
  @Id 
  private UUID id;
  private String name;
  ...
}
```

Here is an example of a produced event in the `organization` topic based on the previous example:
``` json
{
  "action" : "CREATED",
  "timestamp" : "2024-01-01T00:00:00Z",
  "entity" : {
    "id" : "e0d2c165-4c5c-45bb-ba9b-d12af9a69bb4",
    "name" : "538-production",
    ...
  }
}
```