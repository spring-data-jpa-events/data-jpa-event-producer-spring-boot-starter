package com.github.spring.data.jpa.event.producer.sample;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.spring.data.jpa.event.producer.EntityEvent;
import com.github.spring.data.jpa.event.producer.sample.user.User;

@Slf4j
@Getter
@Service
@AllArgsConstructor
public class TestListener {

	private ObjectMapper objectMapper;

	private List<String> organizationEvents = new ArrayList<>();
	private List<EntityEvent<User>> userEvents = new ArrayList<>();

	@KafkaListener(topics = "organization")
	public void listenOrganizationEvents(String orgEvent) {
		log.info("Consummed orgEvent: " + orgEvent);
		organizationEvents.add(orgEvent);
	}

	@KafkaListener(topics = "user")
	public void listenUsersEvents(String event) {
		log.info("Consummed userEvent: " + event);
		try {
			EntityEvent<User> userEvent = objectMapper.readValue(event, new TypeReference<EntityEvent<User>>() {
			});
			userEvents.add(userEvent);
		} catch (Exception e) {
			log.error("Error parsing user event.", e);
		}
	}

	public void reset() {
		organizationEvents.clear();
		userEvents.clear();
	}
}
