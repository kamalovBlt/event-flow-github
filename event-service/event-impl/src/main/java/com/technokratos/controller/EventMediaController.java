package com.technokratos.controller;

import com.technokratos.api.EventMediaApi;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class EventMediaController implements EventMediaApi {

    @Override
    public Resource findImage(Long eventId, Long imageId) {
        return null;
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ORGANIZER','ADMIN')")
    /*TODO: @PreAuthorize("(hasAuthority('ORGANIZER') and @eventService.isOwner(#id, principal)) or hasAuthority('ADMIN')")
    тут #id ID мероприятия, principal это ID пользователя, он автоматически берется
     */
    public Long saveImage(Long eventId, MultipartFile image) {
        return 0L;
    }

    @Override
    public Resource findVideoPart(Long eventId, String range) {
        return null;
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ORGANIZER','ADMIN')")
    /*TODO: @PreAuthorize("(hasAuthority('ORGANIZER') and @eventService.isOwner(#id, principal)) or hasAuthority('ADMIN')")
    тут #id ID мероприятия, principal это ID пользователя, он автоматически берется
     */
    public void saveVideo(Long eventId, MultipartFile video) {

    }

    @Override
    @PreAuthorize("hasAnyAuthority('ORGANIZER','ADMIN')")
    /*TODO: @PreAuthorize("(hasAuthority('ORGANIZER') and @eventService.isOwner(#id, principal)) or hasAuthority('ADMIN')")
    тут #id ID мероприятия, principal это ID пользователя, он автоматически берется
     */
    public void updateImage(Long eventId, Long imageId, MultipartFile image) {

    }

    @Override
    @PreAuthorize("hasAnyAuthority('ORGANIZER','ADMIN')")
    /*TODO: @PreAuthorize("(hasAuthority('ORGANIZER') and @eventService.isOwner(#id, principal)) or hasAuthority('ADMIN')")
    тут #id ID мероприятия, principal это ID пользователя, он автоматически берется
     */
    public void deleteImage(Long eventId, Long imageId) {

    }

    @Override
    @PreAuthorize("hasAnyAuthority('ORGANIZER','ADMIN')")
    /*TODO: @PreAuthorize("(hasAuthority('ORGANIZER') and @eventService.isOwner(#id, principal)) or hasAuthority('ADMIN')")
    тут #id ID мероприятия, principal это ID пользователя, он автоматически берется
     */
    public void updateVideo(Long eventId, MultipartFile video) {

    }

    @Override
    @PreAuthorize("hasAnyAuthority('ORGANIZER','ADMIN')")
    /*TODO: @PreAuthorize("(hasAuthority('ORGANIZER') and @eventService.isOwner(#id, principal)) or hasAuthority('ADMIN')")
    тут #id ID мероприятия, principal это ID пользователя, он автоматически берется
     */
    public void deleteVideo(Long eventId) {

    }
}
