package com.molodec.nikita.transfer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

@Entity
@Table(name = "currency_rates")
public class CurrencyRate {

    private static final MathContext MATH_CONTEXT_FOR_DIVISION = new MathContext(100, RoundingMode.HALF_UP);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "from_code", nullable = false)
    private Currency fromCode;

    @Column(name = "to_code", nullable = false)
    private Currency toCode;

    @Column(name = "rate", nullable = false)
    private BigDecimal rate;

    public CurrencyRate(Integer id, Currency fromCode, Currency toCode, BigDecimal rate) {
        this.id = id;
        this.fromCode = fromCode;
        this.toCode = toCode;
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

    public Currency getFromCode() {
        return fromCode;
    }

    public void setFromCode(Currency fromCode) {
        this.fromCode = fromCode;
    }

    public Currency getToCode() {
        return toCode;
    }

    public void setToCode(Currency toCode) {
        this.toCode = toCode;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal convertFrom(BigDecimal amount) {
        return amount.multiply(rate);
    }

    public BigDecimal convertTo(BigDecimal amount) {
        return amount.divide(rate, MATH_CONTEXT_FOR_DIVISION);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CurrencyRate)) return false;
        CurrencyRate that = (CurrencyRate) o;
        return Objects.equals(id, that.id) &&
                fromCode == that.fromCode &&
                toCode == that.toCode &&
                Objects.equals(rate, that.rate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fromCode, toCode, rate);
    }

    @Override
    public String toString() {
        return "CurrencyRate{" +
                "id=" + id +
                ", fromCode=" + fromCode +
                ", toCode=" + toCode +
                ", rate=" + rate +
                '}';
    }
}
