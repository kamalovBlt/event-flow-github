package com.technokratos.controller;

import com.technokratos.annotation.FileSize;
import com.technokratos.api.EventMediaApi;
import com.technokratos.service.interfaces.EventMediaService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class EventMediaController implements EventMediaApi {

    private final EventMediaService eventMediaService;

    @Override
    public Resource findImage(Long eventId, Long imageId) {
        return eventMediaService.getImage(eventId, imageId);
    }

    @Override
    @PreAuthorize("(hasAuthority('ORGANIZER') and @eventServiceImpl.isCreator(#eventId, authentication)) or hasAuthority('ADMIN')")
    @Validated
    public Long saveImage(Long eventId,
                          @NotNull(message = "Файл обязателен")
                          @FileSize(max = 1024 * 1024, message = "Файл не должен превышать 1 МБ") MultipartFile image)  {
        return eventMediaService.saveImage(eventId, image);
    }

    @Override
    public Resource findVideoPart(Long eventId, String range) {
        return eventMediaService.getVideoPart(eventId, range);
    }

    @Override
    @PreAuthorize("(isAuthenticated() and hasAuthority('ORGANIZER') and @eventServiceImpl.isCreator(#eventId, authentication)) or hasAuthority('ADMIN')")
    @Validated
    public void saveVideo(Long eventId,
                          @FileSize(max = 10 * 1024 * 1024, message = "Файл не должен превышать 10 МБ")
                          MultipartFile video) {
            eventMediaService.saveVideo(eventId, video);
    }

    @Override
    @PreAuthorize("(isAuthenticated() and hasAuthority('ORGANIZER') and @eventServiceImpl.isCreator(#eventId, authentication)) or hasAuthority('ADMIN')")
    @Validated
    public void updateImage(Long eventId,
                            Long imageId,
                            @FileSize(max = 1024 * 1024, message = "Файл не должен превышать 1 МБ")
                            MultipartFile image) {
        eventMediaService.updateImage(eventId, imageId, image);
    }

    @Override
    @PreAuthorize("(isAuthenticated() and hasAuthority('ORGANIZER') and @eventServiceImpl.isCreator(#eventId, authentication)) or hasAuthority('ADMIN')")
    public void deleteImage(Long eventId, Long imageId) {
        eventMediaService.deleteImage(eventId, imageId);
    }

    @Override
    @PreAuthorize("(isAuthenticated() and hasAuthority('ORGANIZER') and @eventServiceImpl.isCreator(#eventId, authentication)) or hasAuthority('ADMIN')")
    @Validated
    public void updateVideo(Long eventId,
                            @FileSize(max = 10 * 1024 * 1024, message = "Файл не должен превышать 10 МБ")
                            MultipartFile video) {
        eventMediaService.updateVideo(eventId, video);
    }

    @Override
    @PreAuthorize("(isAuthenticated() and hasAuthority('ORGANIZER') and @eventServiceImpl.isCreator(#eventId, authentication)) or hasAuthority('ADMIN')")
    public void deleteVideo(Long eventId) {
        eventMediaService.deleteVideo(eventId);
    }
}
