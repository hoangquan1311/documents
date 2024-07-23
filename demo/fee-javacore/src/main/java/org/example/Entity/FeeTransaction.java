package org.example.Entity;

import java.beans.Introspector;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

public class FeeTransaction {
    private String id;
    private String transactionCode;
    private String commandCode;
    private Double feeAmount;
    private String status;
    private String accountNumber;
    private Integer totalScan;
    private String remark;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public FeeTransaction(String id, String transactionCode, String commandCode, Double feeAmount, String status, String accountNumber, Integer totalScan, String remark, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.transactionCode = transactionCode;
        this.commandCode = commandCode;
        this.feeAmount = feeAmount;
        this.status = status;
        this.accountNumber = accountNumber;
        this.totalScan = totalScan;
        this.remark = remark;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public FeeTransaction() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getCommandCode() {
        return commandCode;
    }

    public void setCommandCode(String commandCode) {
        this.commandCode = commandCode;
    }

    public Double getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(Double feeAmount) {
        this.feeAmount = feeAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Integer getTotalScan() {
        return totalScan;
    }

    public void setTotalScan(Integer totalScan) {
        this.totalScan = totalScan;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
}
