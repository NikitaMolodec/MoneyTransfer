package com.molodec.nikita.transfer.core;

import com.molodec.nikita.transfer.db.AccountDAO;
import com.molodec.nikita.transfer.db.CurrencyRateDAO;
import com.molodec.nikita.transfer.db.MoneyTransactionDAO;
import com.molodec.nikita.transfer.model.Account;
import com.molodec.nikita.transfer.model.BalanceModificationException;
import com.molodec.nikita.transfer.model.CurrencyRate;
import com.molodec.nikita.transfer.model.MoneyTransaction;
import com.molodec.nikita.transfer.model.TransactionStatus;
import io.dropwizard.hibernate.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Consumer;

public class MoneyTransactionProcessor {

    private static final Logger logger = LoggerFactory.getLogger(MoneyTransactionProcessor.class);

    private final AccountDAO accountDAO;
    private final MoneyTransactionDAO moneyTransactionDAO;
    private final CurrencyRateDAO currencyRateDAO;

    public MoneyTransactionProcessor(AccountDAO accountDAO, MoneyTransactionDAO moneyTransactionDAO, CurrencyRateDAO currencyRateDAO) {
        this.accountDAO = accountDAO;
        this.moneyTransactionDAO = moneyTransactionDAO;
        this.currencyRateDAO = currencyRateDAO;
    }

    @UnitOfWork
    public void process(MoneyTransaction moneyTransaction) throws BalanceModificationException {
        logger.info("About to process moneyTransaction: {}", moneyTransaction);

        Account fromAccount = accountDAO.findById(moneyTransaction.getFromAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Cannot find account with id=" + moneyTransaction.getFromAccountId()));
        Account toAccount = accountDAO.findById(moneyTransaction.getToAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Cannot find account with id=" + moneyTransaction.getFromAccountId()));
        logger.info("Process transaction for accounts: from={} to={}", fromAccount, toAccount);

        CurrencyRate currencyRateForFromAccount = currencyRateDAO.findByCurrency(fromAccount.getCurrency(), moneyTransaction.getCurrency())
                .orElseThrow(() -> new IllegalArgumentException("Cannot find currency rate for " + fromAccount.getCurrency() + "->" + moneyTransaction.getCurrency()));
        CurrencyRate currencyRateForToAccount = currencyRateDAO.findByCurrency(moneyTransaction.getCurrency(), toAccount.getCurrency())
                .orElseThrow(() -> new IllegalArgumentException("Cannot find currency rate for " + moneyTransaction.getCurrency() + "->" + toAccount.getCurrency()));

        BigDecimal deltaForFromAccount = currencyRateForFromAccount.convert(moneyTransaction.getAmount()).negate();
        BigDecimal deltaForToAccount = currencyRateForToAccount.convert(moneyTransaction.getAmount());

        fromAccount.applyDelta(deltaForFromAccount);
        toAccount.applyDelta(deltaForToAccount);

        accountDAO.update(fromAccount);
        accountDAO.update(toAccount);
        moneyTransaction.setTransactionStatus(TransactionStatus.DONE);
        moneyTransaction.setLastUpdatedTime(LocalDateTime.now(ZoneOffset.UTC));
        moneyTransactionDAO.update(moneyTransaction);
        logger.info("Successfully process transaction. Accounts after apply balance delta: from={} to={}", fromAccount, toAccount);

    }
}
