package com.molodec.nikita.transfer.resources;

import com.molodec.nikita.transfer.core.MoneyTransactionProcessor;
import com.molodec.nikita.transfer.db.MoneyTransactionDAO;
import com.molodec.nikita.transfer.model.BalanceModificationException;
import com.molodec.nikita.transfer.model.MoneyTransaction;
import com.molodec.nikita.transfer.model.TransactionStatus;
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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

@Path("/transaction")
public class MoneyTransactionResource {
    private static final Logger logger = LoggerFactory.getLogger(MoneyTransactionResource.class);

    private final MoneyTransactionDAO moneyTransactionDAO;
    private final MoneyTransactionProcessor moneyTransactionProcessor;

    public MoneyTransactionResource(MoneyTransactionDAO moneyTransactionDAO, MoneyTransactionProcessor moneyTransactionProcessor) {
        this.moneyTransactionDAO = moneyTransactionDAO;
        this.moneyTransactionProcessor = moneyTransactionProcessor;
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
        transaction.setTransactionStatus(TransactionStatus.PROCESSING);
        transaction.setCreationTime(LocalDateTime.now(ZoneOffset.UTC));
        transaction.setLastUpdatedTime(LocalDateTime.now(ZoneOffset.UTC));
        MoneyTransaction createdTransaction = moneyTransactionDAO.create(transaction);
        if (Objects.nonNull(createdTransaction)) {
            try {
                moneyTransactionProcessor.process(createdTransaction);
            } catch (BalanceModificationException | IllegalArgumentException e) {
                logger.error(String.format("Exception during process transaction: %s", createdTransaction), e);
                createdTransaction.setTransactionStatus(TransactionStatus.FAILED);
                createdTransaction.setLastUpdatedTime(LocalDateTime.now(ZoneOffset.UTC));
                moneyTransactionDAO.update(createdTransaction);
                return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
            }
            return Response.ok().entity(createdTransaction).build();
        } else {
            logger.info("Cannot save transaction:{}", transaction.toString());
            return Response.status(Response.Status.BAD_REQUEST).entity("Cannot save transaction required fields is null").build();
        }
    }
}
