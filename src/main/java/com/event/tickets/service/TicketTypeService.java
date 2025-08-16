package com.event.tickets.service;

import com.event.tickets.entity.modal.Ticket;

import java.util.UUID;

public interface TicketTypeService {

    Ticket purchaeTicket(UUID uuid,UUID ticketTypeId);
}
