package com.molodec.nikita.transfer.resources;

import com.molodec.nikita.transfer.db.MoneyTransactionDAO;
import com.molodec.nikita.transfer.messaging.MessageSender;
import com.molodec.nikita.transfer.model.MoneyTransaction;
import com.molodec.nikita.transfer.model.TransactionStatus;
import io.dropwizard.hibernate.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

@Path("/transaction")
public class MoneyTransactionResource {
    private static final Logger logger = LoggerFactory.getLogger(MoneyTransactionResource.class);

    private final MoneyTransactionDAO moneyTransactionDAO;
    private final MessageSender<MoneyTransaction> messageSender;

    public MoneyTransactionResource(MoneyTransactionDAO moneyTransactionDAO, MessageSender<MoneyTransaction> messageSender) {
        this.moneyTransactionDAO = moneyTransactionDAO;
        this.messageSender = messageSender;
    }

    @GET
    @Path("get/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Response getTransaction(@PathParam("id") @Min(0) int id) {
        return moneyTransactionDAO.findById(id)
                .map(t -> Response.ok()
                        .entity(t)
                        .build())
                .orElseThrow(() -> new WebApplicationException(Response.Status.NOT_FOUND));
    }

    @POST
    @Path("create/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Response createTransaction(@NotNull @Valid MoneyTransaction transaction) {
        logger.info("About to create moneyTransaction: {}", transaction);
        transaction.setTransactionStatus(TransactionStatus.REGISTER);
        transaction.setCreationTime(LocalDateTime.now(ZoneOffset.UTC));
        transaction.setLastUpdatedTime(LocalDateTime.now(ZoneOffset.UTC));
        MoneyTransaction createdTransaction = moneyTransactionDAO.create(transaction);
        if (Objects.nonNull(createdTransaction)) {
            try {
                messageSender.send(createdTransaction);
            } catch (JMSException e) {
                throw new WebApplicationException(e.getMessage(), e, Response.Status.INTERNAL_SERVER_ERROR);
            }
            return Response.ok().entity(createdTransaction).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
