package com.github.spring.data.jpa.event.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.spring.data.jpa.event.producer.EntityEvent.Action;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@AllArgsConstructor
public class EntityEventToKafkaEventHandler<I, E> implements EntityEventListener<I, E> {

	private String entityClassName;
	private String kafkaTopicName;
	private KafkaTemplate<String, String> kafkaTemplate;
	private ObjectMapper objectMapper;

	public String getEntityClassName() {
		return entityClassName;
	}

	@Override
	public void onPostInsert(I id, E entity) {
		log.info("PostInsert listener");
		try {
			var event = new EntityEvent<>(Action.CREATED, entity, Instant.now());
			var jsonEvent = objectMapper.writeValueAsString(event);
			log.info("Producing event on topic [{}] : {}", kafkaTopicName, jsonEvent);
			if (id instanceof UUID uuid) {
				log.info("ENTITY ID: " + uuid);
				kafkaTemplate.send(kafkaTopicName, uuid.toString(), jsonEvent);
			}

		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			log.error("NOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO", e);
		}
	}

	@Override
	public void onPostUpdate(I id, E entity) {
		log.info("PostUpdate listener");
		try {
			var event = new EntityEvent<>(Action.UPDATED, entity, Instant.now());
			var jsonEvent = objectMapper.writeValueAsString(event);
			log.info("Producing event on topic [{}] : {}", kafkaTopicName, jsonEvent);
			kafkaTemplate.send(kafkaTopicName, jsonEvent);

		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			log.error("NOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO", e);
		}
	}

	@Override
	public void onPostDelete(I id, E entity) {
		log.info("PostDelete listener.");
		try {
			var event = new EntityEvent<>(Action.DELETED, entity, Instant.now());
			var jsonEvent = objectMapper.writeValueAsString(event);
			log.info("Producing event on topic [{}] : {}", kafkaTopicName, jsonEvent);
			kafkaTemplate.send(kafkaTopicName, jsonEvent);

		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			log.error("NOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO", e);
		}
	}
}
