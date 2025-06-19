package com.technokratos.service.interfaces;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface EventMediaService {
    Long saveImage(Long eventId, MultipartFile image);
    void updateImage(Long eventId, Long imageId, MultipartFile image);
    void deleteImage(Long eventId, Long imageId);
    Resource getImage(Long eventId, Long imageId);

    void saveVideo(Long eventId, MultipartFile video);
    void updateVideo(Long eventId, MultipartFile video);
    void deleteVideo(Long eventId);
    Resource getVideoPart(Long eventId, String range);
}
