package com.molodec.nikita.transfer.resources;

import com.molodec.nikita.transfer.core.MoneyTransactionProcessor;
import com.molodec.nikita.transfer.db.AccountDAO;
import com.molodec.nikita.transfer.db.CurrencyRateDAO;
import com.molodec.nikita.transfer.db.MoneyTransactionDAO;
import com.molodec.nikita.transfer.model.Account;
import com.molodec.nikita.transfer.model.Currency;
import com.molodec.nikita.transfer.model.CurrencyRate;
import com.molodec.nikita.transfer.model.MoneyTransaction;
import com.molodec.nikita.transfer.model.TransactionStatus;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(DropwizardExtensionsSupport.class)
public class MoneyTransactionResourceTest {
    private static final AccountDAO ACCOUNT_DAO = mock(AccountDAO.class);
    private static final CurrencyRateDAO CURRENCY_RATE_DAO = mock(CurrencyRateDAO.class);
    private static final MoneyTransactionDAO MONEY_TRANSACTION_DAO = mock(MoneyTransactionDAO.class);
    public static final ResourceExtension RULE = ResourceExtension.builder()
            .addResource(new MoneyTransactionResource(MONEY_TRANSACTION_DAO
                    ,new MoneyTransactionProcessor(ACCOUNT_DAO, MONEY_TRANSACTION_DAO, CURRENCY_RATE_DAO)))
            .setTestContainerFactory(new GrizzlyWebTestContainerFactory())
            .build();
    private MoneyTransaction moneyTransaction;
    private Account accountFrom;
    private Account accountTo;
    private CurrencyRate usdToRubRate;
    private CurrencyRate rubToEurRate;

    @BeforeEach
    public void setUp() {
        moneyTransaction = new MoneyTransaction.Builder()
                .withId(1)
                .withFromAccountId(1)
                .withToAccountId(2)
                .withAmount(BigDecimal.valueOf(100))
                .withCurrency(Currency.RUB)
                .withCreationTime(LocalDateTime.now(ZoneOffset.UTC))
                .withLastUpdatedTime(LocalDateTime.now(ZoneOffset.UTC))
                .withTransactionStatus(TransactionStatus.PROCESSING)
                .build();
        accountFrom = new Account(1, BigDecimal.valueOf(1000), 1, Currency.USD);
        accountTo = new Account(2, BigDecimal.valueOf(1000), 1, Currency.EUR);
        usdToRubRate = new CurrencyRate(1, Currency.USD, Currency.RUB, BigDecimal.valueOf(1));
        rubToEurRate = new CurrencyRate(2, Currency.RUB, Currency.EUR, BigDecimal.valueOf(1));
    }

    @AfterEach
    public void tearDown() {
        reset(ACCOUNT_DAO, MONEY_TRANSACTION_DAO, CURRENCY_RATE_DAO);
    }

    @Test
    public void getTransaction() {
        when(MONEY_TRANSACTION_DAO.findById(1)).thenReturn(Optional.of(moneyTransaction));

        Response response = RULE.target("/transaction/get/1")
                .request()
                .get();

        assertThat(response.readEntity(MoneyTransaction.class)).isEqualTo(moneyTransaction);
        verify(MONEY_TRANSACTION_DAO).findById(1);
    }

    @Test
    public void processValidTransaction() {
        when(MONEY_TRANSACTION_DAO.create(any(MoneyTransaction.class))).thenReturn(moneyTransaction);
        when(ACCOUNT_DAO.findById(1)).thenReturn(Optional.of(accountFrom));
        when(ACCOUNT_DAO.findById(2)).thenReturn(Optional.of(accountTo));
        when(CURRENCY_RATE_DAO.findByCurrency(Currency.USD, Currency.RUB)).thenReturn(Optional.of(usdToRubRate));
        when(CURRENCY_RATE_DAO.findByCurrency(Currency.RUB, Currency.EUR)).thenReturn(Optional.of(rubToEurRate));

        Response response = RULE.target("/transaction/create/")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(moneyTransaction, MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
        assertThat(response.readEntity(MoneyTransaction.class)).isEqualTo(moneyTransaction);

        assertThat(accountTo.hasValidBalance()).isEqualTo(true);
        assertThat(accountFrom.hasValidBalance()).isEqualTo(true);
        assertThat(accountTo.getBalance()).isEqualTo(BigDecimal.valueOf(1100));
        assertThat(accountFrom.getBalance()).isEqualTo(BigDecimal.valueOf(900));
        assertThat(moneyTransaction.getTransactionStatus()).isEqualTo(TransactionStatus.DONE);
    }

    @Test()
    public void notValidFromAccountId() {
        moneyTransaction.setFromAccountId(0);

        when(MONEY_TRANSACTION_DAO.create(any(MoneyTransaction.class))).thenReturn(moneyTransaction);
        when(ACCOUNT_DAO.findById(0)).thenReturn(Optional.empty());

        Response response = RULE.target("/transaction/create/")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(moneyTransaction, MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.BAD_REQUEST);
        assertThat(response.readEntity(String.class).contains("id=0")).isEqualTo(true);

        assertThat(moneyTransaction.getTransactionStatus()).isEqualTo(TransactionStatus.FAILED);
    }

    @Test()
    public void notValidToAccountId() {
        moneyTransaction.setToAccountId(3);

        when(MONEY_TRANSACTION_DAO.create(any(MoneyTransaction.class))).thenReturn(moneyTransaction);
        when(ACCOUNT_DAO.findById(1)).thenReturn(Optional.of(accountFrom));
        when(ACCOUNT_DAO.findById(3)).thenReturn(Optional.empty());

        Response response = RULE.target("/transaction/create/")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(moneyTransaction, MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.BAD_REQUEST);
        assertThat(response.readEntity(String.class).contains("id=3")).isEqualTo(true);

        assertThat(moneyTransaction.getTransactionStatus()).isEqualTo(TransactionStatus.FAILED);
    }

    @Test()
    public void notValidCurrency() {
        moneyTransaction.setCurrency(Currency.EUR);

        when(MONEY_TRANSACTION_DAO.create(any(MoneyTransaction.class))).thenReturn(moneyTransaction);
        when(ACCOUNT_DAO.findById(1)).thenReturn(Optional.of(accountFrom));
        when(ACCOUNT_DAO.findById(2)).thenReturn(Optional.of(accountTo));
        when(CURRENCY_RATE_DAO.findByCurrency(Currency.USD, Currency.EUR)).thenReturn(Optional.empty());

        Response response = RULE.target("/transaction/create/")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(moneyTransaction, MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.BAD_REQUEST);
        assertThat(response.readEntity(String.class).contains("USD->EUR")).isEqualTo(true);

        assertThat(moneyTransaction.getTransactionStatus()).isEqualTo(TransactionStatus.FAILED);
    }

    @Test
    public void notValidFromAccountBalance() {
        accountFrom.setBalance(BigDecimal.ZERO);

        when(MONEY_TRANSACTION_DAO.create(any(MoneyTransaction.class))).thenReturn(moneyTransaction);
        when(ACCOUNT_DAO.findById(1)).thenReturn(Optional.of(accountFrom));
        when(ACCOUNT_DAO.findById(2)).thenReturn(Optional.of(accountTo));
        when(CURRENCY_RATE_DAO.findByCurrency(Currency.USD, Currency.RUB)).thenReturn(Optional.of(usdToRubRate));
        when(CURRENCY_RATE_DAO.findByCurrency(Currency.RUB, Currency.EUR)).thenReturn(Optional.of(rubToEurRate));

        Response response = RULE.target("/transaction/create/")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(moneyTransaction, MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.BAD_REQUEST);
        assertThat(response.readEntity(String.class).contains("balance=0")).isEqualTo(true);

        assertThat(accountTo.hasValidBalance()).isEqualTo(true);
        assertThat(accountFrom.hasValidBalance()).isEqualTo(true);
        assertThat(accountTo.getBalance()).isEqualTo(BigDecimal.valueOf(1000));
        assertThat(accountFrom.getBalance()).isEqualTo(BigDecimal.valueOf(0));
        assertThat(moneyTransaction.getTransactionStatus()).isEqualTo(TransactionStatus.FAILED);
    }

    @Test
    public void createEmptyTransaction() {
        Response response = RULE.target("/transaction/create/")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(new MoneyTransaction(), MediaType.APPLICATION_JSON_TYPE));
        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.BAD_REQUEST);
    }
}
