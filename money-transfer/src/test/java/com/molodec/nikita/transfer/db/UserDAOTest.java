package com.molodec.nikita.transfer.db;

import com.molodec.nikita.transfer.model.User;
import io.dropwizard.testing.junit5.DAOTestExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(DropwizardExtensionsSupport.class)
public class UserDAOTest {

    public DAOTestExtension daoTestExtension = DAOTestExtension.newBuilder()
            .addEntityClass(User.class)
            .build();

    private UserDAO userDAO;

    @BeforeEach
    public void setUp() {
        userDAO = new UserDAO(daoTestExtension.getSessionFactory());
    }

    @Test
    public void createUser() {
//        final User user = daoTestExtension.inTransaction(() -> userDAO.create(new User("userLogin")));
        System.out.println(daoTestExtension.inTransaction(() -> userDAO.create(new User("userLogin"))));
        System.out.println(daoTestExtension.inTransaction(() -> userDAO.create(new User("userLogin1"))));
        System.out.println(daoTestExtension.inTransaction(() -> userDAO.create(new User("userLogin2"))));
        System.out.println(daoTestExtension.inTransaction(() -> userDAO.create(new User("userLogin3"))));
        System.out.println(daoTestExtension.inTransaction(() -> userDAO.create(new User("userLogin4"))));
        System.out.println(daoTestExtension.inTransaction(() -> userDAO.create(new User("userLogin5"))));
    }

}
