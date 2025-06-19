package com.technokratos.controller.handler;

import com.technokratos.dto.response.EventServiceErrorResponse;
import com.technokratos.exception.EventServiceException;
import com.technokratos.exception.artist.ArtistAlreadyExistsException;
import com.technokratos.exception.s3.S3NotValidException;
import com.technokratos.exception.artist.ArtistNotFoundException;
import com.technokratos.exception.artist.ArtistSaveException;
import com.technokratos.exception.event.EventConflictException;
import com.technokratos.exception.event.EventNotFoundException;
import com.technokratos.exception.event.EventSaveException;
import com.technokratos.exception.s3.*;
import com.technokratos.exception.ticket.TicketNotFoundException;
import com.technokratos.exception.ticket.TicketSaveException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class EventServiceExceptionHandler {

    @ExceptionHandler({EventNotFoundException.class, ImageNotFoundException.class,
            ArtistNotFoundException.class, TicketNotFoundException.class, S3MediaNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public EventServiceErrorResponse handleEventServiceNotFound(EventServiceException ex) {
        return handleEventServiceException(ex);
    }

    @ExceptionHandler({S3NotValidException.class, S3LoadException.class, ImageSaveException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public EventServiceErrorResponse handleEventServiceBadRequest(EventServiceException ex) {
        return handleEventServiceException(ex);
    }

    @ExceptionHandler({ImageMaxCountException.class, EventConflictException.class, ArtistAlreadyExistsException.class,
            ArtistSaveException.class, EventSaveException.class,TicketSaveException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public EventServiceErrorResponse handleEventServiceConflict(EventServiceException ex) {
        return handleEventServiceException(ex);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public EventServiceErrorResponse handleConstraintViolationException(ConstraintViolationException ex) {
        return EventServiceErrorResponse.builder()
                .exceptionName(ex.getClass().getName())
                .message(ex.getMessage())
                .build();
    }

    private EventServiceErrorResponse handleEventServiceException(EventServiceException ex) {
        EventServiceErrorResponse.EventServiceErrorResponseBuilder builder = EventServiceErrorResponse.builder()
                .message(ex.getMessage())
                .exceptionName(ex.getClass().getName());

        if (ex.getEventId() != null) {
            builder.eventId(String.valueOf(ex.getEventId()));
        }

        return builder.build();
    }
}
