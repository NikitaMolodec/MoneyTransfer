package com.molodec.nikita.transfer.db;

import com.molodec.nikita.transfer.model.MoneyTransaction;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.Optional;

public class MoneyTransactionDAO extends AbstractDAO<MoneyTransaction> {
    public MoneyTransactionDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<MoneyTransaction> findById(Integer id) {
        return Optional.ofNullable(get(id));
    }

    public MoneyTransaction create(MoneyTransaction moneyTransaction) {
        return persist(moneyTransaction);
    }

    public void update(MoneyTransaction moneyTransaction) {
        persist(moneyTransaction);
    }
}
