package com.event.tickets.entity.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTicketTypeRequestDto {

    @NotEmpty(message = "Ticket type name cannot be empty")
    private String name;

    @NotEmpty(message = "Ticket type price cannot be empty")
    @PositiveOrZero(message = "Ticket type price must be zero or positive")
    private Double price;

    private String description;

    private Integer totalAvailable;
}
