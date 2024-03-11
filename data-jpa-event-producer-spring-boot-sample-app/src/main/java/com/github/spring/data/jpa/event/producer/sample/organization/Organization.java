package com.github.spring.data.jpa.event.producer.sample.organization;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.spring.data.jpa.event.producer.autoconfigure.KafkaEvents;
import com.github.spring.data.jpa.event.producer.sample.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Entity
@KafkaEvents(topic = "organization")
@Table(name = "organization")
@Data
public class Organization {

	@Id
	private UUID id;

	private String name;

	private String email;

	@JsonIgnore
	@OneToMany(mappedBy = "organization")
	private List<User> users;
}
