package com.molodec.nikita.transfer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "money_transactions")
public class MoneyTransaction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "from_account_id", nullable = false)
    private Integer fromAccountId;

    @Column(name = "to_account_id", nullable = false)
    private Integer toAccountId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "last_updated_time", nullable = false)
    private LocalDateTime lastUpdatedTime;

    @Column(name = "transaction_status", nullable = false)
    private TransactionStatus transactionStatus;

    @Column(name = "currency", nullable = false)
    private Currency currency;

    public MoneyTransaction(Integer id, Integer fromAccountId, Integer toAccountId, BigDecimal amount, Currency currency) {
        this.id = id;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.currency = currency;
    }

    public MoneyTransaction() {
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public LocalDateTime getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(LocalDateTime lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(Integer fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public Integer getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(Integer toAccountId) {
        this.toAccountId = toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MoneyTransaction)) return false;
        MoneyTransaction that = (MoneyTransaction) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(fromAccountId, that.fromAccountId) &&
                Objects.equals(toAccountId, that.toAccountId) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(creationTime, that.creationTime) &&
                Objects.equals(lastUpdatedTime, that.lastUpdatedTime) &&
                transactionStatus == that.transactionStatus &&
                currency == that.currency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fromAccountId, toAccountId, amount, creationTime, lastUpdatedTime, transactionStatus, currency);
    }

    @Override
    public String toString() {
        return "MoneyTransaction{" +
                "id=" + id +
                ", fromAccountId=" + fromAccountId +
                ", toAccountId=" + toAccountId +
                ", amount=" + amount +
                ", creationTime=" + creationTime +
                ", lastUpdatedTime=" + lastUpdatedTime +
                ", transactionStatus=" + transactionStatus +
                ", currency=" + currency +
                '}';
    }

    public static class Builder {
        private Integer id;
        private Integer fromAccountId;
        private Integer toAccountId;
        private BigDecimal amount;
        private LocalDateTime creationTime;
        private LocalDateTime lastUpdatedTime;
        private TransactionStatus transactionStatus;
        private Currency currency;

        public Builder() {
        }

        public Builder(MoneyTransaction moneyTransaction) {
            this.id = moneyTransaction.id;
            this.fromAccountId = moneyTransaction.fromAccountId;
            this.toAccountId = moneyTransaction.toAccountId;
            this.amount = moneyTransaction.amount;
            this.creationTime = moneyTransaction.creationTime;
            this.lastUpdatedTime = moneyTransaction.lastUpdatedTime;
            this.transactionStatus = moneyTransaction.transactionStatus;
            this.currency = moneyTransaction.currency;
        }

        public Builder withId(Integer id) {
            this.id = id;
            return this;
        }

        public Builder withFromAccountId(Integer id) {
            this.fromAccountId = id;
            return this;
        }

        public Builder withToAccountId(Integer id) {
            this.toAccountId = id;
            return this;
        }

        public Builder withAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder withCreationTime(LocalDateTime creationTime) {
            this.creationTime = creationTime;
            return this;
        }

        public Builder withLastUpdatedTime(LocalDateTime lastUpdatedTime) {
            this.lastUpdatedTime = lastUpdatedTime;
            return this;
        }

        public Builder withTransactionStatus(TransactionStatus transactionStatus) {
            this.transactionStatus = transactionStatus;
            return this;
        }

        public Builder withCurrency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public MoneyTransaction build() {
            MoneyTransaction moneyTransaction = new MoneyTransaction();
            moneyTransaction.setId(this.id);
            moneyTransaction.setFromAccountId(this.fromAccountId);
            moneyTransaction.setToAccountId(this.toAccountId);
            moneyTransaction.setAmount(this.amount);
            moneyTransaction.setCreationTime(this.creationTime);
            moneyTransaction.setLastUpdatedTime(this.lastUpdatedTime);
            moneyTransaction.setTransactionStatus(this.transactionStatus);
            moneyTransaction.setCurrency(this.currency);
            return moneyTransaction;
        }

    }
}
