package com.github.spring.data.jpa.event.producer.autoconfigure;

import static java.util.stream.Collectors.toSet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.spring.data.jpa.event.producer.EntityEventToKafkaEventHandler;
import com.github.spring.data.jpa.event.producer.HibernateEventListener;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.Set;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;

@Configuration
public class DataJpaEventProducerAutoconfiguration {

	@Bean
	@Primary
	public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}

	@Bean
	public HibernateEventListener hibernateEventListener(EntityManagerFactory entityManagerFactory,
			Set<EntityEventToKafkaEventHandler<?, ?>> entityListeners) {
		var listener = new HibernateEventListener(entityListeners);

		EventListenerRegistry eventRegistry = entityManagerFactory.unwrap(SessionFactoryImpl.class).getServiceRegistry()
				.getService(EventListenerRegistry.class);

		eventRegistry.appendListeners(EventType.POST_INSERT, listener);
		eventRegistry.appendListeners(EventType.POST_UPDATE, listener);
		eventRegistry.appendListeners(EventType.POST_DELETE, listener);

		return listener;
	}

	@Bean
	public Set<EntityEventToKafkaEventHandler<?, ?>> entityListeners(EntityManager entityManager,
			KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {

		return entityManager.getMetamodel().getEntities().stream().map(entity -> entity.getJavaType())
				.filter(entityClass -> entityClass.isAnnotationPresent(KafkaEvents.class))
				.map(entityClass -> new EntityEventToKafkaEventHandler<>(entityClass.getName(),
						entityClass.getAnnotation(KafkaEvents.class).topic(), kafkaTemplate, objectMapper))
				.collect(toSet());
	}
}
