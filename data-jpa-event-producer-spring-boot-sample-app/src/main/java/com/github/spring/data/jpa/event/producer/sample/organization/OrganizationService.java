package com.github.spring.data.jpa.event.producer.sample.organization;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class OrganizationService {

	private OrganizationRepository repository;

	@Transactional
	public Organization create(String name, String email) {

		Organization orgToCreate = new Organization();
		orgToCreate.setId(UUID.randomUUID());
		orgToCreate.setName(name);
		orgToCreate.setEmail(email);

		return repository.save(orgToCreate);
	}

	public List<Organization> getAll() {
		return repository.findAll();
	}

	public void deleteAll() {
		repository.deleteAll();
	}
}
