package com.molodec.nikita.transfer;


import com.molodec.nikita.transfer.conf.MoneyTransferConfiguration;
import com.molodec.nikita.transfer.core.MoneyTransactionProcessor;
import com.molodec.nikita.transfer.db.AccountDAO;
import com.molodec.nikita.transfer.db.CurrencyRateDAO;
import com.molodec.nikita.transfer.db.InMemoryDatabaseManager;
import com.molodec.nikita.transfer.db.MoneyTransactionDAO;
import com.molodec.nikita.transfer.db.UserDAO;
import com.molodec.nikita.transfer.messaging.MessageReceiver;
import com.molodec.nikita.transfer.messaging.MessageSender;
import com.molodec.nikita.transfer.model.Account;
import com.molodec.nikita.transfer.model.CurrencyRate;
import com.molodec.nikita.transfer.model.MoneyTransaction;
import com.molodec.nikita.transfer.model.User;
import com.molodec.nikita.transfer.resources.AccountResource;
import com.molodec.nikita.transfer.resources.CurrencyRateResource;
import com.molodec.nikita.transfer.resources.MoneyTransactionResource;
import com.molodec.nikita.transfer.resources.UserResource;
import io.dropwizard.Application;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import io.dropwizard.lifecycle.JettyManaged;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;

public class MoneyTransferApplication extends Application<MoneyTransferConfiguration> {

    private static final String MONEY_TRANSACTION_QUEUE = "money.transactions";
    private static final String CONNECTION_URL = "tcp://localhost:61616";

    public static void main(String[] args) throws Exception {
        new MoneyTransferApplication().run(args);
    }

    private final HibernateBundle<MoneyTransferConfiguration> hibernate = new HibernateBundle<MoneyTransferConfiguration>(User.class, MoneyTransaction.class, Account.class, CurrencyRate.class) {
        @Override
        public PooledDataSourceFactory getDataSourceFactory(MoneyTransferConfiguration moneyTransferConfiguration) {
            return moneyTransferConfiguration.getDataSourceFactory();
        }
    };

    @Override
    public String getName() {
        return "money-transfer";
    }

    @Override
    public void initialize(Bootstrap<MoneyTransferConfiguration> bootstrap) {
        bootstrap.addBundle(hibernate);
        bootstrap.addBundle(new MigrationsBundle<MoneyTransferConfiguration>() {
            @Override
            public PooledDataSourceFactory getDataSourceFactory(MoneyTransferConfiguration moneyTransferConfiguration) {
                return moneyTransferConfiguration.getDataSourceFactory();
            }
        });
    }

    @Override
    @UnitOfWork
    public void run(MoneyTransferConfiguration moneyTransferConfiguration, Environment environment) throws Exception {
        final UserDAO userDAO = new UserDAO(hibernate.getSessionFactory());
        final MoneyTransactionDAO moneyTransactionDAO = new MoneyTransactionDAO(hibernate.getSessionFactory());
        final AccountDAO accountDAO = new AccountDAO(hibernate.getSessionFactory());
        final CurrencyRateDAO currencyRateDAO = new CurrencyRateDAO(hibernate.getSessionFactory());

        final BrokerService broker = BrokerFactory.createBroker("broker:(" + CONNECTION_URL + ")");
        broker.start();

        final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(CONNECTION_URL);
        connectionFactory.setTrustAllPackages(true);

        final MoneyTransactionProcessor moneyTransactionProcessor = new UnitOfWorkAwareProxyFactory(hibernate).create(
                MoneyTransactionProcessor.class,
                new Class[] {AccountDAO.class, MoneyTransactionDAO.class, CurrencyRateDAO.class},
                new Object[] {accountDAO, moneyTransactionDAO, currencyRateDAO});

        environment.jersey().register(new UserResource(userDAO));
        environment.jersey().register(new MoneyTransactionResource(moneyTransactionDAO, new MessageSender<>(connectionFactory, MONEY_TRANSACTION_QUEUE)));
        environment.jersey().register(new AccountResource(accountDAO));
        environment.jersey().register(new CurrencyRateResource(currencyRateDAO));

        environment.lifecycle().getManagedObjects().add(0, new JettyManaged(new InMemoryDatabaseManager(moneyTransferConfiguration.getDataSourceFactory().build(environment.metrics(), "money_transfer_datasource"))));
        environment.lifecycle().manage(new MessageReceiver<>(connectionFactory, MONEY_TRANSACTION_QUEUE, moneyTransactionProcessor));
    }
}
