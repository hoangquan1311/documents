package org.example.Service;

import org.example.Connection.OracleConnection;
import org.example.Connection.RedisConfig;
import org.example.Entity.FeeTransaction;
import org.example.Resquest.FeeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FeeCommandTransactionService {
    public Logger logger = LoggerFactory.getLogger(FeeCommandTransactionService.class);

    public void addFeeCommandAndTransactions(FeeRequest feeRequest) {
        if (isRequestIdDuplicate(feeRequest.getRequestId())) {
            logger.error("Duplicate requestId in a day: " + feeRequest.getRequestId());
            throw new RuntimeException("Duplicate requestId in a day: " + feeRequest.getRequestId());
        }
        if (isRequestTimeValid(feeRequest.getRequestTime())) {
            logger.error("Request time exceeds the 10-minute limit.");
            throw new RuntimeException("Request time exceeds the 10-minute limit.");
        }
        pushToRedis(feeRequest.getRequestId());
        String insertCommandSql = "INSERT INTO FEE_COMMAND (ID, COMMAND_CODE, TOTAL_RECORD, TOTAL_FEE, CREATED_USER, CREATED_DATE) " +
                "VALUES (?,?,?,?,?,TO_DATE(?, 'YYYYMMDDHH24MISS'))";
        String insertTransactionSql = "INSERT INTO FEE_TRANSACTION (ID, TRANSACTION_CODE, COMMAND_CODE, FEE_AMOUNT, STATUS, ACCOUNT_NUMBER, TOTAL_SCAN, REMARK, CREATED_DATE, MODIFIED_DATE) " +
                "VALUES (?,?,?,?,?,?,?,?,TO_DATE(?, 'YYYYMMDDHH24MISS'),SYSTIMESTAMP)";
        Connection connection = null;
        try {
            connection = OracleConnection.getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertCommandSql);
                 PreparedStatement preparedStatement1 = connection.prepareStatement(insertTransactionSql)
            ) {
                preparedStatement.setString(1, generateUniqueStringId());
                preparedStatement.setString(2, feeRequest.getCommandCode());
                preparedStatement.setInt(3, feeRequest.getTotalRecord());
                preparedStatement.setDouble(4, feeRequest.getTotalFee());
                preparedStatement.setString(5, feeRequest.getCreatedUser());
                preparedStatement.setString(6, feeRequest.getCreatedDate());
                preparedStatement.executeUpdate();
                preparedStatement1.setString(1, generateUniqueStringId());
                preparedStatement1.setString(2, generateTransactionCode());
                preparedStatement1.setString(3, feeRequest.getCommandCode());
                preparedStatement1.setDouble(4, feeRequest.getTotalFee() / feeRequest.getTotalRecord());
                preparedStatement1.setString(5, "01");
                preparedStatement1.setString(6, "0393421396");
                preparedStatement1.setInt(7, 0);
                preparedStatement1.setString(8, "INIT");
                preparedStatement1.setString(9, feeRequest.getCreatedDate());
                preparedStatement1.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public String generateUniqueStringId() {
        return UUID.randomUUID().toString();
    }

    public String generateTransactionCode() {
        return "TXN" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10).toUpperCase();
    }


    public List<FeeTransaction> getFeeCommand(String commandCode) {
        String sql = "SELECT * FROM FEE_TRANSACTION WHERE COMMAND_CODE = ?";
        List<FeeTransaction> transactions = new ArrayList<>();

        try (Connection connection = OracleConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, commandCode);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    String id = resultSet.getString("ID");
                    FeeTransaction transaction = new FeeTransaction();
                    transaction.setId(resultSet.getString("ID"));
                    transaction.setTransactionCode(resultSet.getString("TRANSACTION_CODE"));
                    transaction.setCommandCode(resultSet.getString("COMMAND_CODE"));
                    transaction.setFeeAmount(resultSet.getDouble("FEE_AMOUNT"));
                    transaction.setStatus(resultSet.getString("STATUS"));
                    transaction.setAccountNumber(resultSet.getString("ACCOUNT_NUMBER"));
                    transaction.setTotalScan(resultSet.getInt("TOTAL_SCAN"));
                    transaction.setRemark(resultSet.getString("REMARK"));

                    Timestamp createdDateTimestamp = resultSet.getTimestamp("CREATED_DATE");
                    Timestamp modifiedDateTimestamp = resultSet.getTimestamp("MODIFIED_DATE");
                    if (createdDateTimestamp != null) {
                        transaction.setCreatedDate(createdDateTimestamp.toLocalDateTime());
                    }
                    if (modifiedDateTimestamp != null) {
                        transaction.setModifiedDate(modifiedDateTimestamp.toLocalDateTime());
                    }

                    transactions.add(transaction);
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error querying FEE_TRANSACTION table: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return transactions;
    }


    public void updateFeeTransactions() {
        String updateSql = "UPDATE FEE_TRANSACTION " +
                "SET TOTAL_SCAN = 1, REMARK = 'PAY', MODIFIED_DATE = SYSTIMESTAMP, STATUS = '02' " +
                "WHERE STATUS = '01'";

        Connection connection = null;
        try {
            connection = OracleConnection.getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateSql)) {
                int rowsAffected = preparedStatement.executeUpdate();
                connection.commit();
                System.out.println("Updated " + rowsAffected + " rows.");
                logger.info("Updated " + rowsAffected);
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException("Error updating FEE_TRANSACTION table: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to database: " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException("Error closing connection: " + e.getMessage(), e);
                }
            }
        }
    }

    public boolean isRequestIdDuplicate(String requestId) {
        RedisConfig redisConfig = new RedisConfig();
        try (Jedis jedis = redisConfig.getJedis()) {
            String key = "requestId:" + requestId;
            return jedis.exists(key);
        }
    }

    public boolean isRequestTimeValid(String requestTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime requestDateTime = LocalDateTime.parse(requestTime, formatter);
        LocalDateTime now = LocalDateTime.now();
        long minutesDifference = ChronoUnit.MINUTES.between(requestDateTime, now);
        return minutesDifference < 10;
    }

    public void pushToRedis(String requestId) {
        RedisConfig redisConfig = new RedisConfig();
        try (Jedis jedis = redisConfig.getJedis()) {
            String key = "requestId:" + requestId;
            jedis.setex(key, 864000, requestId);
        }
    }

    public void createCronjob() {

        String sql = "SELECT * FROM FEE_TRANSACTION WHERE TOTAL_SCAN < 5 AND STATUS = '02'";
        try (Connection connection = OracleConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
            connection.setAutoCommit(false);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString("ID");
                String transactionCode = resultSet.getString("TRANSACTION_CODE");
                String commandCode1 = resultSet.getString("COMMAND_CODE");
                String status = resultSet.getString("STATUS");
                String accountNumber = resultSet.getString("ACCOUNT_NUMBER");
                Integer totalScan = resultSet.getInt("TOTAL_SCAN");
                String remark = resultSet.getString("REMARK");
                Timestamp createdDate = resultSet.getTimestamp("CREATED_DATE");
                Timestamp modifiedDate = resultSet.getTimestamp("MODIFIED_DATE");
                logger.info("ID: " + id + ", Transaction Code: " + transactionCode + ", Command Code: "
                        + commandCode1 + ", Status: " + status + ", Account Number: "
                        + accountNumber + ", Total Scan: " + totalScan + ", Remark: " + remark + ", Created Date: " + createdDate
                        + ", ModifiedDate: " + modifiedDate);
            }

        }
        catch (SQLException e) {
            System.err.println("Error updating fee transactions: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateChargeScanAndUpdateStatus() {
        String updateSql = "UPDATE FEE_TRANSACTION " +
                "SET TOTAL_SCAN = TOTAL_SCAN + 1, REMARK = 'PAY', MODIFIED_DATE = SYSTIMESTAMP " +
                "WHERE TOTAL_SCAN < 5 AND STATUS = '02'";

        String stopSql = "UPDATE FEE_TRANSACTION " +
                "SET STATUS = '03', REMARK = 'STOP', MODIFIED_DATE = SYSTIMESTAMP " +
                "WHERE TOTAL_SCAN = 5";
        try (Connection connection = OracleConnection.getConnection();
             PreparedStatement updateStatement = connection.prepareStatement(updateSql);
             PreparedStatement stopStatement = connection.prepareStatement(stopSql)) {

            connection.setAutoCommit(false);
            int updatedRows = updateStatement.executeUpdate();
            int stoppedRows = stopStatement.executeUpdate();
            connection.commit();
            System.out.println("Updated " + updatedRows + " transactions for payment.");
            System.out.println("Stopped " + stoppedRows + " transactions.");

        } catch (SQLException e) {
            System.err.println("Error updating fee transactions: " + e.getMessage());
            e.printStackTrace();
        }
    }
}