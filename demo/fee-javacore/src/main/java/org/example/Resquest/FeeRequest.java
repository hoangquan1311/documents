package org.example.Resquest;

public class FeeRequest {
    private String requestId;
    private String requestTime;
    private String commandCode;
    private int totalRecord;
    private double totalFee;
    private String createdUser;
    private String createdDate;

    public FeeRequest(String requestId, String requestTime, String commandCode, int totalRecord, double totalFee, String createdUser, String createdDate) {
        this.requestId = requestId;
        this.requestTime = requestTime;
        this.commandCode = commandCode;
        this.totalRecord = totalRecord;
        this.totalFee = totalFee;
        this.createdUser = createdUser;
        this.createdDate = createdDate;
    }

    public FeeRequest() {
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getCommandCode() {
        return commandCode;
    }

    public void setCommandCode(String commandCode) {
        this.commandCode = commandCode;
    }

    public int getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(int totalRecord) {
        this.totalRecord = totalRecord;
    }

    public double getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(double totalFee) {
        this.totalFee = totalFee;
    }

    public String getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(String createdUser) {
        this.createdUser = createdUser;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    // Các phương thức getter và setter

    @Override
    public String toString() {
        return "FeeCommand{" +
                "requestId='" + requestId + '\'' +
                ", requestTime='" + requestTime + '\'' +
                ", commandCode='" + commandCode + '\'' +
                ", totalRecord=" + totalRecord +
                ", totalFee=" + totalFee +
                ", createdUser='" + createdUser + '\'' +
                ", createdDate='" + createdDate + '\'' +
                '}';
    }

}
