package com.event.tickets.service.impl;

import com.event.tickets.entity.CreateEventRequest;
import com.event.tickets.entity.UpdateEventRequest;
import com.event.tickets.entity.UpdateTicketTypeRequest;
import com.event.tickets.entity.dto.GetEventDetailsResponseDto;
import com.event.tickets.entity.enums.EventStatusEnum;
import com.event.tickets.entity.modal.Event;
import com.event.tickets.entity.modal.TicketType;
import com.event.tickets.entity.modal.User;
import com.event.tickets.exceptions.EventNotFoundException;
import com.event.tickets.exceptions.EventUpdateException;
import com.event.tickets.exceptions.TicketTypeNotFoundException;
import com.event.tickets.exceptions.UserNotFoundException;
import com.event.tickets.repository.EventRepository;
import com.event.tickets.repository.UserRepository;
import com.event.tickets.service.EventService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
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

    @Override
    public Page<Event> listEventsForOrganizer(UUID organizerId, Pageable pageable) {
        return eventRepository.findByOrganizerId(organizerId, pageable);
    }

    @Override
    public Optional<Event> getEventByIdAndOrganizerId(UUID organizerId, UUID eventId) {
        return eventRepository.findByIdAndOrganizerId(eventId, organizerId);
    }

    @Override
    @Transactional
    public Event updateEventForOrganizer(UUID organizerId, UUID eventId, UpdateEventRequest event) {
        if (null == event.getId()) {
            throw new EventUpdateException("Event update request is invalid or missing event ID.");
        }

        if (!event.getId().equals(eventId)) {
            throw new EventUpdateException("Event ID in the request does not match the path variable.");
        }

        Event existingEvent = eventRepository.findByIdAndOrganizerId(eventId, organizerId)
                .orElseThrow(() -> new EventNotFoundException(
                        String.format("Event with id %s not found for organizer %s", eventId, organizerId)
                ));
        existingEvent.setName(event.getName());
        existingEvent.setStart(event.getStart());
        existingEvent.setEnd(event.getEnd());
        existingEvent.setVenue(event.getVenue());
        existingEvent.setSaleStart(event.getSaleStart());
        existingEvent.setSaleEnd(event.getSaleEnd());
        existingEvent.setStatus(event.getStatus());

        Set<UUID> requestTicketTypeIds = event.getTicketTypes()
                .stream()
                .map(UpdateTicketTypeRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        existingEvent.getTicketTypes().
                removeIf(ticketType -> !requestTicketTypeIds.contains(ticketType.getId()));

        Map<UUID, TicketType> existingTicketTypeIndex = existingEvent.getTicketTypes().stream().collect(
                Collectors.toMap(
                        TicketType::getId,
                        Function.identity()
                )
        );

        for (UpdateTicketTypeRequest ticketType : event.getTicketTypes()) {
            if (null == ticketType.getId()) {
                TicketType newTicketType = new TicketType();
                newTicketType.setName(ticketType.getName());
                newTicketType.setPrice(ticketType.getPrice());
                newTicketType.setDescription(ticketType.getDescription());
                newTicketType.setTotalAvailable(ticketType.getTotalAvailable());
                newTicketType.setEvent(existingEvent);
                existingEvent.getTicketTypes().add(newTicketType);

            } else if (existingTicketTypeIndex.containsKey(ticketType.getId())) {
                TicketType exiTicketType = existingTicketTypeIndex.get(ticketType.getId());
                exiTicketType.setName(ticketType.getName());
                exiTicketType.setPrice(ticketType.getPrice());
                exiTicketType.setDescription(ticketType.getDescription());
                exiTicketType.setTotalAvailable(ticketType.getTotalAvailable());
            } else {

                throw new TicketTypeNotFoundException(
                        String.format("Ticket type with id %s not found in event %s", ticketType.getId(), eventId)
                );
            }
        }
        return eventRepository.save(existingEvent);


    }

    @Override
    public void deleteEventForOrganizer(UUID organizerId, UUID eventId) {
        getEventByIdAndOrganizerId(organizerId, eventId).ifPresent(eventRepository::delete);
    }

    @Override
    public Page<Event> listEvents(Pageable pageable) {
        return eventRepository.findByStatus(EventStatusEnum.PUBLISHED, pageable);
    }

    @Override
    public Optional<Event> getPublishedEventById(UUID eventId) {
        return eventRepository.findByIdAndStatus(eventId, EventStatusEnum.PUBLISHED);
    }

}
