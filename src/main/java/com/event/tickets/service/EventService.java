package com.event.tickets.service;

import com.event.tickets.entity.CreateEventRequest;
import com.event.tickets.entity.UpdateEventRequest;
import com.event.tickets.entity.modal.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface EventService {
    Event createEvent(UUID organizerId, CreateEventRequest event);
    Page<Event>  listEventsForOrganizer(UUID organizerId, Pageable pageable);
    Optional<Event> getEventByIdAndOrganizerId( UUID organizerId,UUID eventId);
    Event updateEventForOrganizer(UUID organizerId, UUID eventId, UpdateEventRequest event);
}
