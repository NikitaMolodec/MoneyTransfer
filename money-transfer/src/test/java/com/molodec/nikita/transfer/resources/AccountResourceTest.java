package com.molodec.nikita.transfer.resources;

import com.molodec.nikita.transfer.db.AccountDAO;
import com.molodec.nikita.transfer.model.Account;
import com.molodec.nikita.transfer.model.Currency;
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
import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(DropwizardExtensionsSupport.class)
public class AccountResourceTest {
    private static final AccountDAO ACCOUNT_DAO = mock(AccountDAO.class);
    public static final ResourceExtension RULE = ResourceExtension.builder()
            .addResource(new AccountResource(ACCOUNT_DAO))
            .setTestContainerFactory(new GrizzlyWebTestContainerFactory())
            .build();
    private ArgumentCaptor<Account> accoutCaptor = ArgumentCaptor.forClass(Account.class);
    private Account account;

    @BeforeEach
    public void setUp() {
        account = new Account(1, BigDecimal.valueOf(100), 1, Currency.USD);
    }

    @AfterEach
    public void tearDown() {
        reset(ACCOUNT_DAO);
    }

    @Test
    public void getAccount() {
        when(ACCOUNT_DAO.findById(1)).thenReturn(Optional.of(account));

        Response response = RULE.target("/account/get/1")
                .request()
                .get();

        assertThat(response.readEntity(Account.class)).isEqualTo(account);
        verify(ACCOUNT_DAO).findById(1);
    }

    @Test
    public void createAccount() {
        when(ACCOUNT_DAO.create(any(Account.class))).thenReturn(account);

        Response response = RULE.target("/account/create/")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(account, MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
        assertThat(response.readEntity(Account.class)).isEqualTo(account);
        verify(ACCOUNT_DAO).create(accoutCaptor.capture());
        assertThat(accoutCaptor.getValue()).isEqualTo(account);
    }
}
