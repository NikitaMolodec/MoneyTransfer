package com.molodec.nikita.transfer.resources;

import com.molodec.nikita.transfer.db.UserDAO;
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

@Path("/user")
public class UserResource {

    private static final Logger logger = LoggerFactory.getLogger(UserResource.class);

    private final UserDAO userDAO;


    public UserResource(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @GET
    @Path("get/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Response getUser(@PathParam("id") @Min(0) int id) {
        logger.info("About to find user by id={}", id);
        return userDAO.findById(id)
                .map(user -> Response.ok()
                        .entity(user)
                        .build())
                .orElseThrow(() -> new WebApplicationException(Response.Status.NOT_FOUND));
    }

    @POST
    @Path("create/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Response createUser(@NotNull @Valid User user) {
        logger.info("About to create user: {}", user);
        User createdUser = userDAO.create(user);
        if (Objects.nonNull(createdUser)) {
            logger.info("Successfully create user: {}", user);
            return Response.ok()
                    .entity(createdUser)
                    .build();
        } else {
            logger.warn("Cannot create user: {}", user);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
