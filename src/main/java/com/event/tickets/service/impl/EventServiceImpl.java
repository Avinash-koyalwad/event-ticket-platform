package com.event.tickets.service.impl;

import com.event.tickets.entity.CreateEventRequest;
import com.event.tickets.entity.modal.Event;
import com.event.tickets.entity.modal.TicketType;
import com.event.tickets.entity.modal.User;
import com.event.tickets.exceptions.UserNotFoundException;
import com.event.tickets.repository.EventRepository;
import com.event.tickets.repository.UserRepository;
import com.event.tickets.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public Event createEvent(UUID organizerId, CreateEventRequest event) {

        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("User with id %s not found", organizerId)
                ));

        Event newEvent = new Event();

        List<TicketType> ticketTypes = event.getTicketTypes().stream().map(
                ticketType -> {
                    TicketType newTicketType = new TicketType();
                    newTicketType.setName(ticketType.getName());
                    newTicketType.setPrice(ticketType.getPrice());
                    newTicketType.setDescription(ticketType.getDescription());
                    newTicketType.setTotalAvailable(ticketType.getTotalAvailable());
                    newTicketType.setEvent(newEvent);
                    return newTicketType;
                }
        ).toList();

        newEvent.setName(event.getName());
        newEvent.setStart(event.getStart());
        newEvent.setEnd(event.getEnd());
        newEvent.setVenue(event.getVenue());
        newEvent.setSaleStart(event.getSalesStart());
        newEvent.setSaleEnd(event.getSalesEnd());
        newEvent.setStatus(event.getStatus());
        newEvent.setOrganizer(organizer);
        newEvent.setTicketTypes(ticketTypes);

        return eventRepository.save(newEvent);
    }
}
