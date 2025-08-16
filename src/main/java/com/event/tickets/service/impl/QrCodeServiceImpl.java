package com.event.tickets.service.impl;

import com.event.tickets.entity.enums.QrCodeStatusEnum;
import com.event.tickets.entity.modal.QrCode;
import com.event.tickets.entity.modal.Ticket;
import com.event.tickets.exceptions.QrCodeGenerationException;
import com.event.tickets.repository.QrCodeRepository;
import com.event.tickets.service.QrCodeService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class QrCodeServiceImpl implements QrCodeService {

    private static final String QR_CODE_IMAGE_FORMAT = "PNG";
    private static final int QR_CODE_IMAGE_WIDTH = 300;
    private static final int QR_CODE_IMAGE_HEIGHT = 300;

    private final QrCodeRepository qrCodeRepository;
    private final QRCodeWriter qrCodeWriter;

    @Override
    public QrCode generateQrCode(Ticket ticket) {

        try {
            UUID qrCodeId = UUID.randomUUID();
            String qrCodeImage = generateQrCodeImage(qrCodeId);
            QrCode qrCode = new QrCode();
            qrCode.setId(qrCodeId);
            qrCode.setTicket(ticket);
            qrCode.setStatus(QrCodeStatusEnum.ACTIVE);
            qrCode.setValue(qrCodeImage);
            return qrCodeRepository.saveAndFlush(qrCode);
        } catch (IOException | WriterException e) {
            throw new QrCodeGenerationException(
                    "Failed to generate QR code for ticket ID: " + ticket.getId(), e);
        }
    }

    private String generateQrCodeImage(UUID qrCodeId) throws WriterException, IOException {

        BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeId.toString(),
                BarcodeFormat.QR_CODE,
                QR_CODE_IMAGE_WIDTH,
                QR_CODE_IMAGE_HEIGHT);

        BufferedImage qrCodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(qrCodeImage, QR_CODE_IMAGE_FORMAT, baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        }

    }
}
