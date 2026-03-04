package com.github.spring.data.jpa.event.producer.mapper;

public interface EntityToEventMapper<E> {

  String map(DatabaseAction action, E entity);

  enum DatabaseAction {
    INSERT,
    UPDATE,
    DELETE
  }
}
