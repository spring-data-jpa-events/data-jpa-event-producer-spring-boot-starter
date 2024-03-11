package com.github.spring.data.jpa.event.producer;

public interface EntityEventListener<I, E> {
	void onPostInsert(I id, E entity);
	void onPostUpdate(I id, E entity);
	void onPostDelete(I id, E entity);
}
