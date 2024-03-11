package com.github.spring.data.jpa.event.producer.sample.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.spring.data.jpa.event.producer.autoconfigure.KafkaEvents;
import com.github.spring.data.jpa.event.producer.sample.organization.Organization;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Data;

@Entity
@KafkaEvents(topic = "user")
@Table(name = "\"user\"")
@Data
public class User {

	@Id
	private UUID id;

	private String name;

	private String email;

	@Column(name = "organization_id")
	private UUID organizationId;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "organization_id", insertable = false, updatable = false)
	private Organization organization;
}
