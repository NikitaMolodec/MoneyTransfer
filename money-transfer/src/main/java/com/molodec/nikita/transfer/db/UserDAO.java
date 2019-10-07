package com.molodec.nikita.transfer.db;

import com.molodec.nikita.transfer.model.User;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.Optional;

public class UserDAO extends AbstractDAO<User>{

    public UserDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<User> findById(Integer id) {
        return Optional.ofNullable(get(id));
    }

    public User create(User user) {
        return persist(user);
    }
}
