package com.molodec.nikita.transfer.resources;

import com.molodec.nikita.transfer.db.AccountDAO;
import com.molodec.nikita.transfer.db.UserDAO;
import com.molodec.nikita.transfer.model.Account;
import com.molodec.nikita.transfer.model.User;
import io.dropwizard.hibernate.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Objects;

@Path("/account")
public class AccountResource {
    private static final Logger logger = LoggerFactory.getLogger(UserResource.class);

    private final AccountDAO accountDAO;

    public AccountResource(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    @GET
    @Path("get/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Response getAccount(@PathParam("id") @Min(0) int id) {
        logger.info("About to find account by id={}", id);
        return accountDAO.findById(id)
                .map(account -> Response.ok()
                        .entity(account)
                        .build())
                .orElseThrow(() -> new WebApplicationException(Response.Status.NOT_FOUND));
    }

    @POST
    @Path("create/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Response createAccount(@NotNull @Valid Account account) {
        logger.info("About to create user: {}", account);
        Account createdAccount = accountDAO.create(account);
        if (Objects.nonNull(createdAccount)) {
            logger.info("Successfully create user: {}", createdAccount);
            return Response.ok()
                    .entity(createdAccount)
                    .build();
        } else {
            logger.warn("Cannot create user: {}", account);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
