package com.molodec.nikita.transfer.resources;

import com.molodec.nikita.transfer.db.CurrencyRateDAO;
import com.molodec.nikita.transfer.model.CurrencyRate;
import io.dropwizard.hibernate.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Objects;

@Path("/currency")
public class CurrencyRateResource {
    private static final Logger logger = LoggerFactory.getLogger(UserResource.class);

    private final CurrencyRateDAO currencyRateDAO;

    public CurrencyRateResource(CurrencyRateDAO currencyRateDAO) {
        this.currencyRateDAO = currencyRateDAO;
    }


    @POST
    @Path("create/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Response createRate(@NotNull @Valid CurrencyRate currencyRate) {
        logger.info("About to create currencyRate: {}", currencyRate);
        CurrencyRate createdCurrencyRate = currencyRateDAO.create(currencyRate);
        if (Objects.nonNull(createdCurrencyRate)) {
            logger.info("Successfully create currencyRate: {}", createdCurrencyRate);
            return Response.ok()
                    .entity(createdCurrencyRate)
                    .build();
        } else {
            logger.warn("Cannot create currencyRate: {}", currencyRate);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
