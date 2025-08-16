package com.event.tickets.service.impl;

import com.event.tickets.entity.enums.TicketStatusEnum;
import com.event.tickets.entity.modal.QrCode;
import com.event.tickets.entity.modal.Ticket;
import com.event.tickets.entity.modal.TicketType;
import com.event.tickets.entity.modal.User;
import com.event.tickets.exceptions.TicketSoldOutException;
import com.event.tickets.exceptions.TicketTypeNotFoundException;
import com.event.tickets.exceptions.UserNotFoundException;
import com.event.tickets.repository.TicketRepository;
import com.event.tickets.repository.TicketTypeRepository;
import com.event.tickets.repository.UserRepository;
import com.event.tickets.service.QrCodeService;
import com.event.tickets.service.TicketTypeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketTypeServiceImpl implements TicketTypeService {

    private final UserRepository userRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final QrCodeService qrCodeService;
    private final TicketRepository ticketRepository;


    @Override
    @Transactional
    public Ticket purchaeTicket(UUID uuid, UUID ticketTypeId) {

        User user = userRepository.findById(uuid).orElseThrow(
                () -> new UserNotFoundException("User not found with id: " + uuid)
        );

        TicketType ticketType = ticketTypeRepository.findByIdWithLock(ticketTypeId).orElseThrow(
                () -> new TicketTypeNotFoundException("Ticket type not found with id: " + ticketTypeId)
        );

        int purchasedTickets = ticketRepository.countByTicketTypeId(ticketType.getId());

        Integer totalAvailable = ticketType.getTotalAvailable();

        if (purchasedTickets + 1 > totalAvailable) {
            throw new TicketSoldOutException("No more tickets available for this type.");
        }

        Ticket ticket = new Ticket();
        ticket.setStatus(TicketStatusEnum.PURCHASED);
        ticket.setTicketType(ticketType);
        ticket.setPurchaser(user);

        Ticket savedTicket = ticketRepository.save(ticket);

        qrCodeService.generateQrCode(savedTicket);

        return ticketRepository.save(savedTicket);
    }


}
