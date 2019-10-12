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
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "currency", nullable = false)
    private Currency currency;

    public Account(Integer id, BigDecimal balance, Integer userId, Currency currency) {
        this.id = id;
        this.balance = balance;
        this.userId = userId;
        this.currency = currency;
    }

    public Account() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public void applyDelta(BigDecimal delta) throws BalanceModificationException {
        balance = balance.add(delta);
        if (!hasValidBalance()) {
            throw new BalanceModificationException(
                    String.format("Account with id=%d has not valid balance=%s", id, balance)
            );
        }
    }

    public boolean hasValidBalance() {
        return balance.compareTo(BigDecimal.ZERO) >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id) &&
                Objects.equals(balance, account.balance) &&
                Objects.equals(userId, account.userId) &&
                currency == account.currency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, balance, userId, currency);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", balance=" + balance +
                ", userId=" + userId +
                ", currency=" + currency.currencyCode() +
                '}';
    }
}
