package com.github.spring.data.jpa.event.producer.sample.user;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserService {

	private UserRepository repository;

	@Transactional
	public User create(String username, String email, UUID orgId, boolean simulateError) {
		try {

			User userToCreate = new User();
			userToCreate.setId(UUID.randomUUID());
			userToCreate.setName(username);
			userToCreate.setEmail(email);
			userToCreate.setOrganizationId(orgId);

			var user = repository.save(userToCreate);

			if (simulateError) {
				Thread.sleep(1000);
				throw new RuntimeException("Fake exception to test transaction rollback behaviour.");
			}

			return user;

		} catch (InterruptedException e) {
			throw new IllegalStateException("Should not happen.", e);
		}
	}

	@Transactional
	public User update(User userToUpdate, boolean simulateError) {
		try {
			var user = repository.save(userToUpdate);

			if (simulateError) {
				Thread.sleep(1000);
				throw new RuntimeException("Fake exception to test transaction rollback behaviour.");
			}

			return user;

		} catch (InterruptedException e) {
			throw new IllegalStateException("Should not happen.", e);
		}
	}

	@Transactional
	public void delete(UUID userId, boolean simulateError) {
		try {
			repository.deleteById(userId);

			if (simulateError) {
				Thread.sleep(1000);
				throw new RuntimeException("Fake exception to test transaction rollback behaviour.");
			}
		} catch (InterruptedException e) {
			throw new IllegalStateException("Should not happen.", e);
		}
	}

	public List<User> getAll() {
		return repository.findAll();
	}

	public void deleteAll() {
		repository.deleteAll();
	}
}
