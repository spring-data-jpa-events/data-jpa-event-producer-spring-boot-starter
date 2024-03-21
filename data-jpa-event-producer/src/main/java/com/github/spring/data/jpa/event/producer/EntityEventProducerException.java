package com.github.spring.data.jpa.event.producer;

public class EntityEventProducerException extends RuntimeException {

  public EntityEventProducerException() {}

  public EntityEventProducerException(String message) {
    super(message);
  }

  public EntityEventProducerException(String message, Throwable cause) {
    super(message, cause);
  }

  public EntityEventProducerException(Throwable cause) {
    super(cause);
  }

  protected EntityEventProducerException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
