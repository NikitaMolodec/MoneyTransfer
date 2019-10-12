package com.molodec.nikita.transfer.resources;

import com.molodec.nikita.transfer.db.CurrencyRateDAO;
import com.molodec.nikita.transfer.model.Currency;
import com.molodec.nikita.transfer.model.CurrencyRate;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(DropwizardExtensionsSupport.class)
public class CurrencyRateResourceTest {
    private static final CurrencyRateDAO CURRENCY_RATE_DAO = mock(CurrencyRateDAO.class);
    public static final ResourceExtension RULE = ResourceExtension.builder()
            .addResource(new CurrencyRateResource(CURRENCY_RATE_DAO))
            .setTestContainerFactory(new GrizzlyWebTestContainerFactory())
            .build();
    private ArgumentCaptor<CurrencyRate> currencyRateCaptor = ArgumentCaptor.forClass(CurrencyRate.class);
    private CurrencyRate currencyRate;

    @BeforeEach
    public void setUp() {
        currencyRate = new CurrencyRate(1, Currency.USD, Currency.EUR, BigDecimal.valueOf(1));
    }

    @AfterEach
    public void tearDown() {
        reset(CURRENCY_RATE_DAO);
    }


    @Test
    public void createCurrencyRate() {
        when(CURRENCY_RATE_DAO.create(any(CurrencyRate.class))).thenReturn(currencyRate);

        Response response = RULE.target("/currency/create/")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(currencyRate, MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
        assertThat(response.readEntity(CurrencyRate.class)).isEqualTo(currencyRate);
        verify(CURRENCY_RATE_DAO).create(currencyRateCaptor.capture());
        assertThat(currencyRateCaptor.getValue()).isEqualTo(currencyRate);
    }
}
