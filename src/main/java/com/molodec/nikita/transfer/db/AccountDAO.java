package com.molodec.nikita.transfer.db;

import com.molodec.nikita.transfer.model.Account;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.Optional;

public class AccountDAO extends AbstractDAO<Account> {
    public AccountDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<Account> findById(Integer id) {
        return Optional.ofNullable(get(id));
    }

    public Account create(Account account) {
        return persist(account);
    }

    public void update(Account account) {
        persist(account);
    }
}
