package com.github.spring.data.jpa.event.producer.sample;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.github.spring.data.jpa.event.producer.EntityEvent.Action;
import com.github.spring.data.jpa.event.producer.sample.organization.Organization;
import com.github.spring.data.jpa.event.producer.sample.organization.OrganizationService;
import com.github.spring.data.jpa.event.producer.sample.user.UserService;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext
@EmbeddedKafka(count = 3, topics = {"organization", "user"})
public class SampleApplicationIT {

	private static final String USERNAME = "Jacob";
	private static final String EMAIL = "jacob@example.com";
	private static final String UPDATED_EMAIL = "updated-email-" + EMAIL;

	@Autowired
	private TestListener testListener;

	@Autowired
	private OrganizationService orgService;
	@Autowired
	private UserService userService;

	private Organization defaultOrg;

	@BeforeEach
	void beforeTestSetup() throws Exception {
		// clean DB
		userService.deleteAll();
		orgService.deleteAll();

		// create default organization
		defaultOrg = orgService.create("default org", "org@email.com");

		// ignore events
		Thread.sleep(1000);
		testListener.reset();
	}

	@Test
	void saveJpaEntity_thenCreateEventProduced() throws Exception {
		// when
		userService.create(USERNAME, EMAIL, defaultOrg.getId(), false);
		// then
		await().atMost(Duration.ofSeconds(5)).until(() -> testListener.getUserEvents().size() == 1);
		var userEvent = testListener.getUserEvents().get(0);
		assertThat(userEvent.getAction()).isEqualTo(Action.CREATED);
		assertThat(userEvent.getTimestamp()).isNotNull();
		var userEntity = userEvent.getEntity();
		assertThat(userEntity.getId()).isNotNull();
		assertThat(userEntity.getName()).isEqualTo(USERNAME);
		assertThat(userEntity.getEmail()).isEqualTo(EMAIL);
	}

	@Test
	void saveJpaEntity_givenTransactionFail_thenNothingCommited() throws Exception {
		// when
		ignoreException(() -> userService.create(USERNAME, EMAIL, defaultOrg.getId(), true));
		// then
		Thread.sleep(1000);
		assertThat(testListener.getUserEvents()).hasSize(0);
		assertThat(userService.getAll()).hasSize(0);
	}

	@Test
	void saveJpaEntity_givenDBTransactionFail_thenNothingCommited() throws Exception {
		// given
		userService.create("conflict email user", EMAIL, defaultOrg.getId(), false);
		// when
		ignoreException(() -> userService.create(USERNAME, EMAIL, defaultOrg.getId(), false));
		// then
		Thread.sleep(1000);
		assertThat(testListener.getUserEvents()).hasSize(1);
		assertThat(userService.getAll()).hasSize(1);
	}

	@Test
	void updateJpaEntity_thenUpdateEventProduced() throws Exception {
		// given
		var user = userService.create(USERNAME, EMAIL, defaultOrg.getId(), false);
		// when
		user.setEmail(UPDATED_EMAIL);
		userService.update(user, false);

		// then
		await().atMost(Duration.ofSeconds(5)).until(() -> testListener.getUserEvents().size() == 2);
		var userEvent = testListener.getUserEvents().get(1);
		assertThat(userEvent.getAction()).isEqualTo(Action.UPDATED);
		assertThat(userEvent.getTimestamp()).isNotNull();
		var userEntity = userEvent.getEntity();
		assertThat(userEntity.getId()).isNotNull();
		assertThat(userEntity.getName()).isEqualTo(USERNAME);
		assertThat(userEntity.getEmail()).isEqualTo(UPDATED_EMAIL);
	}

	@Test
	void deleteJpaEntity_thenDeleteEventProduced() throws Exception {
		// given
		var user = userService.create(USERNAME, EMAIL, defaultOrg.getId(), false);
		// when
		userService.delete(user.getId(), false);

		// then
		await().atMost(Duration.ofSeconds(5)).until(() -> testListener.getUserEvents().size() == 2);
		var userEvent = testListener.getUserEvents().get(1);
		assertThat(userEvent.getAction()).isEqualTo(Action.DELETED);
		assertThat(userEvent.getTimestamp()).isNotNull();
		var userEntity = userEvent.getEntity();
		assertThat(userEntity.getId()).isNotNull();
		assertThat(userEntity.getName()).isEqualTo(USERNAME);
		assertThat(userEntity.getEmail()).isEqualTo(EMAIL);
	}

	private static void ignoreException(Runnable runnable) {
		try {
			runnable.run();
		} catch (Exception e) {
			// ignore any execptions
		}
	}
}
