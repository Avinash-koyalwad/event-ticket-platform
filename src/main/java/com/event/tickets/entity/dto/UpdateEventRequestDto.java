package com.event.tickets.entity.dto;

import com.event.tickets.entity.enums.EventStatusEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventRequestDto {

    @NotNull(message = "Event ID cannot be null")
    private UUID id;

    @NotBlank(message = "Event name cannot be empty")
    private String name;
    private LocalDateTime start;
    private LocalDateTime end;

    @NotBlank(message = "Venue cannot be empty")
    private String venue;
    private LocalDateTime salesStart;
    private LocalDateTime salesEnd;

    @NotNull(message = "Event status cannot be null")
    private EventStatusEnum status;

    @NotEmpty(message = "Ticket type cannot be empty")
    @Valid
    private List<UpdateTicketTypeRequestDto> ticketType;
}
