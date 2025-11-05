package ma.pharmachain.service;

import ma.pharmachain.dto.TransferCreateRequest;
import ma.pharmachain.dto.TransferUpdateRequest;
import ma.pharmachain.entity.BordereauTransfer;
import ma.pharmachain.enums.TransferStatus;
import org.springframework.data.domain.Page;

public interface BordereauTransferService {

    BordereauTransfer createTransfer(String bordereauNumber, TransferCreateRequest request);

    Page<BordereauTransfer> listTransfers(int page, int size);

    BordereauTransfer getTransferById(Long id);

    BordereauTransfer updateTransfer(Long id, TransferUpdateRequest request);

    BordereauTransfer updateTransferStatus(Long id, TransferStatus status);

    void deleteTransfer(Long id);
}

