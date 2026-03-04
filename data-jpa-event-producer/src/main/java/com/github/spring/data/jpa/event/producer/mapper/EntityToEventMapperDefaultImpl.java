package com.github.spring.data.jpa.event.producer.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.spring.data.jpa.event.producer.mapper.EntityEvent.Action;
import java.time.Instant;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EntityToEventMapperDefaultImpl<E> implements EntityToEventMapper<E> {

  private final ObjectMapper objectMapper;

  @Override
  public String map(DatabaseAction dbAction, E entity) throws EventMappingException {
    try {

      Action action =
          switch (dbAction) {
            case INSERT -> Action.CREATED;
            case UPDATE -> Action.UPDATED;
            case DELETE -> Action.DELETED;
          };

      var event = new EntityEvent<>(action, entity, Instant.now());
      return objectMapper.writeValueAsString(event);

    } catch (JsonProcessingException e) {
      throw new EventMappingException("Error mapping entity to event", e);
    }
  }
}
