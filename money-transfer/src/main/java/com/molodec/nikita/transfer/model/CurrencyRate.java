package com.molodec.nikita.transfer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "currency_rates")
public class CurrencyRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "from", nullable = false)
    private Currency from;

    @Column(name = "to", nullable = false)
    private Currency to;

    @Column(name = "rate", nullable = false)
    private BigDecimal rate;

    public CurrencyRate(Integer id, Currency from, Currency to, BigDecimal rate) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.rate = rate;
    }

    public CurrencyRate() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Currency getFrom() {
        return from;
    }

    public void setFrom(Currency from) {
        this.from = from;
    }

    public Currency getTo() {
        return to;
    }

    public void setTo(Currency to) {
        this.to = to;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal convert(BigDecimal amount) {
        return amount.multiply(rate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CurrencyRate)) return false;
        CurrencyRate that = (CurrencyRate) o;
        return Objects.equals(id, that.id) &&
                from == that.from &&
                to == that.to &&
                Objects.equals(rate, that.rate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, from, to, rate);
    }

    @Override
    public String toString() {
        return "CurrencyRate{" +
                "id=" + id +
                ", from=" + from +
                ", to=" + to +
                ", rate=" + rate +
                '}';
    }
}
