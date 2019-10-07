package com.molodec.nikita.transfer.resources;


import com.molodec.nikita.transfer.db.UserDAO;
import com.molodec.nikita.transfer.model.User;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(DropwizardExtensionsSupport.class)
public class UserResourceTest {
    private static final UserDAO USER_DAO = mock(UserDAO.class);
    public static final ResourceExtension RULE = ResourceExtension.builder()
            .addResource(new UserResource(USER_DAO))
            .setTestContainerFactory(new GrizzlyWebTestContainerFactory())
            .build();
    private ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    private User user;

    @BeforeEach
    public void setUp() {
        user = new User(1, "exampleLogin");
    }

    @AfterEach
    public void tearDown() {
        reset(USER_DAO);
    }

    @Test
    public void getUser() {
        when(USER_DAO.findById(1)).thenReturn(Optional.of(user));

        Response response = RULE.target("/user/get/1")
                .request()
                .get();

        assertThat(response.readEntity(User.class)).isEqualTo(user);
        verify(USER_DAO).findById(1);
    }

    @Test
    public void createUser() {
        when(USER_DAO.create(any(User.class))).thenReturn(user);

        Response response = RULE.target("/user/create/")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(user, MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
        verify(USER_DAO).create(userCaptor.capture());
        assertThat(userCaptor.getValue()).isEqualTo(user);
    }
}
