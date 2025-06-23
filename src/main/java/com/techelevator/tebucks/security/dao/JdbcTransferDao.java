package com.techelevator.tebucks.security.dao;

import com.techelevator.tebucks.security.model.Transfer;
import com.techelevator.tebucks.security.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import com.techelevator.tebucks.exception.DaoException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{
    private final JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Transfer createTransfer(Transfer transfer) {
        Transfer newTransfer = null;
        int transferFrom = transfer.getUserFrom().getId();
        int transferTo = transfer.getUserTo().getId();
        String sql = "INSERT INTO transfer (transfer_type, transfer_status, user_from, user_to, amount)\n" +
                "VALUES (?,?,?,?,?)\n" +
                "RETURNING transfer_id;";
        try{
            int newTransferId = jdbcTemplate.queryForObject(sql, int.class, transfer.getTransferType(),
                    transfer.getTransferStatus(), transferFrom,
                    transferTo, transfer.getAmount());
            newTransfer = getTransferById(newTransferId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return newTransfer;
    }

    @Override
    public List<Transfer> getTransfersByUserId(int userId) {
        List<Transfer> output = new ArrayList<>();
        String sql = "SELECT transfer_id, transfer_type, transfer_status, user_from, user_to, amount, \n" +
                "       uf.username AS from_username, uo.username AS to_username\n" +
                "FROM transfer\n" +
                "JOIN users AS uf ON uf.user_id = transfer.user_from\n" +
                "JOIN users AS uo ON uo.user_id = transfer.user_to\n" +
                "WHERE user_from = ? OR user_to = ?;\n";
        try{
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId, userId);
            while (results.next()) {
                output.add(mapRowToTransfer(results));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return output;
    }

    @Override
    public Transfer getTransferById(int transferId) {
        Transfer transfer = null;
        String sql = "SELECT transfer_id, transfer_type, transfer_status, user_from, user_to, amount, " +
                "uf.username AS from_username, uo.username AS to_username " +
                "FROM transfer " +
                "JOIN users AS uf ON uf.user_id = transfer.user_from " +
                "JOIN users AS uo ON uo.user_id = transfer.user_to " +
                "WHERE transfer_id = ?;";

        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, transferId);
            if (result.next()) {
                System.out.println("Found transfer with ID: " + transferId); // Debugging
                transfer = mapRowToTransfer(result);
            } else {
                System.out.println("No transfer found for ID: " + transferId); // Debugging
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return transfer;
    }

    @Override
    public Transfer approveTransfer(int transferId, int approveUserId) {
        Transfer approvedTransfer = null;
        String sql = "update transfer set transfer_status = 'Approved' where transfer_id = ? and user_to = ? and transfer_status ilike 'pending';";
        try {
            int rowsAffected = jdbcTemplate.update(sql, transferId, approveUserId);
            if (rowsAffected == 0) {
                throw new DaoException("Zero rows affected, expected at least one");
            }
            approvedTransfer = getTransferById(transferId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return approvedTransfer;
    }

    @Override
    public Transfer rejectTransfer(int transferId, int approveUserId) {
        Transfer rejectedTransfer = null;
        String sql = "update transfer set transfer_status = 'Rejected' where transfer_id = ? and user_to = ? and transfer_status ilike 'pending';";
        try {
            int rowsAffected = jdbcTemplate.update(sql, transferId, approveUserId);
            if (rowsAffected == 0) {
                throw new DaoException("Zero rows affected, expected at least one");
            }
            rejectedTransfer = getTransferById(transferId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return rejectedTransfer;
    }

    private Transfer mapRowToTransfer(SqlRowSet rs) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(rs.getInt("transfer_id"));
        transfer.setTransferType(rs.getString("transfer_type"));
        transfer.setTransferStatus(rs.getString("transfer_status"));
        transfer.setAmount(rs.getBigDecimal("amount"));

        User fromUser = new User();
        fromUser.setId(rs.getInt("user_from"));
        fromUser.setUsername(rs.getString("from_username"));
        transfer.setUserFrom(fromUser);

        User toUser = new User();
        toUser.setId(rs.getInt("user_to"));
        toUser.setUsername(rs.getString("to_username"));
        transfer.setUserTo(toUser);

        return transfer;
    }



}
