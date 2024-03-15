package com.github.spring.data.jpa.event.producer;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.AccessLevel;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class EntityEvent<E> {
	Action action;
	E entity;
	Instant timestamp;

	public static enum Action {
		CREATED, UPDATED, DELETED
	}
}
