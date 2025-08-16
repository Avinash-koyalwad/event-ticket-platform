package com.event.tickets.service;

import com.event.tickets.entity.modal.QrCode;
import com.event.tickets.entity.modal.Ticket;

public interface QrCodeService {

    QrCode generateQrCode(Ticket ticket);
}
