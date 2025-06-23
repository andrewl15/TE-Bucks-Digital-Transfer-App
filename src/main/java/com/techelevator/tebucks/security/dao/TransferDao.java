package com.techelevator.tebucks.security.dao;

import com.techelevator.tebucks.security.model.Transfer;

import java.util.List;

public interface TransferDao {
    Transfer createTransfer(Transfer transfer);

    List<Transfer> getTransfersByUserId(int userId);

    Transfer getTransferById(int transferId);

    Transfer approveTransfer(int transferId, int approveUserId);

    Transfer rejectTransfer(int transferId, int approveUserId);
}
