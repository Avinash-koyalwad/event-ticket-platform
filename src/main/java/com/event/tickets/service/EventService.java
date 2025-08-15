package com.event.tickets.service;

import com.event.tickets.entity.CreateEventRequest;
import com.event.tickets.entity.modal.Event;

import java.util.UUID;

public interface EventService {
    Event createEvent(UUID organizerId, CreateEventRequest event);
}
