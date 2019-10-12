package com.molodec.nikita.transfer.db;

import com.molodec.nikita.transfer.model.Currency;
import com.molodec.nikita.transfer.model.CurrencyRate;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CurrencyRateDAO extends AbstractDAO<CurrencyRate> {

    public CurrencyRateDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @SuppressWarnings("uncheked")
    public Optional<CurrencyRate> findByCurrency(Currency from, Currency to) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        if (from.equals(to)) return Optional.of(new CurrencyRate(-1, from, to, BigDecimal.ONE));
        List<CurrencyRate> result = query("from CurrencyRate where fromCode=:fromCode and toCode=:toCode")
                .setParameter("fromCode", from)
                .setParameter("toCode", to)
                .getResultList();
        return Optional.ofNullable(result.isEmpty() ? null : result.get(0));
    }

    public CurrencyRate create(CurrencyRate currencyRate) {
        return persist(currencyRate);
    }
}
