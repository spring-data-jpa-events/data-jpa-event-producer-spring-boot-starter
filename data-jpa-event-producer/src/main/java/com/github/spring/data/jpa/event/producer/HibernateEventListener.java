package com.github.spring.data.jpa.event.producer;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;

@Slf4j
@SuppressWarnings("unchecked")
public class HibernateEventListener
		implements
			PostInsertEventListener,
			PostUpdateEventListener,
			PostDeleteEventListener {

	@SuppressWarnings("rawtypes")
	private Map<String, EntityEventListener> entityListeners;

	public HibernateEventListener(Set<EntityEventToKafkaEventHandler<?, ?>> entityListeners) {
		this.entityListeners = entityListeners.stream()
				.collect(toMap(listener -> listener.getEntityClassName(), listener -> listener));
	}

	@Override
	public void onPostInsert(PostInsertEvent event) {
		log.info("HIBERNATE INSERT LISTENERRRRRR");
		var entity = event.getEntity();
		if (entity != null) {
			var listener = entityListeners.get(entity.getClass().getName());
			if (listener != null) {
				listener.onPostInsert(event.getId(), entity);
			}
		}
	}

	@Override
	public void onPostUpdate(PostUpdateEvent event) {
		log.info("HIBERNATE UPDATE LISTENERRRRRR");
		var entity = event.getEntity();
		if (entity != null) {
			var listener = entityListeners.get(entity.getClass().getName());
			if (listener != null) {
				listener.onPostUpdate(event.getId(), entity);
			}
		}
	}

	@Override
	public void onPostDelete(PostDeleteEvent event) {
		log.info("HIBERNATE DELETE LISTENERRRRRR");
		var entity = event.getEntity();
		if (entity != null) {
			var listener = entityListeners.get(entity.getClass().getName());
			if (listener != null) {
				listener.onPostDelete(event.getId(), entity);
			}
		}
	}

	@Override
	public boolean requiresPostCommitHandling(EntityPersister persister) {
		return false;
	}
}
