package com.technokratos.service.impl;

import com.technokratos.config.properties.YandexS3Properties;
import com.technokratos.exception.s3.S3NotValidException;
import com.technokratos.exception.s3.*;
import com.technokratos.exception.event.EventNotFoundException;
import com.technokratos.model.Event;
import com.technokratos.model.Image;
import com.technokratos.repository.interfaces.EventRepository;
import com.technokratos.repository.interfaces.ImageRepository;
import com.technokratos.service.interfaces.EventMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EventMediaServiceImpl implements EventMediaService {

    private static final long MAX_IMAGE_SIZE = 1024 * 1024;
    private static final long MAX_VIDEO_SIZE = 10 * 1024 * 1024;

    private final EventRepository eventRepository;
    private final ImageRepository imageRepository;
    private final S3Client s3Client;
    private final YandexS3Properties properties;

    @Override
    public Long saveImage(Long eventId, MultipartFile image) {
        validateFile(image, MAX_IMAGE_SIZE, "image");

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Мероприятие с id %s не найдено".formatted(eventId)));

        if (event.getImageIds().size() == 5)
            throw new ImageMaxCountException("Количество изображений не должно превышать 5", eventId);

        String key = generateMediaKey(eventId, image.getOriginalFilename());
        uploadToS3(key, image);

        Image newImage = Image.builder()
                .eventId(eventId)
                .key(key)
                .build();

        return imageRepository.save(newImage)
                .orElseThrow(() -> new ImageSaveException("Не удалось сохранить изображение в репозитории"));
    }

    @Override
    public void updateImage(Long eventId, Long imageId, MultipartFile image) {
        validateFile(image, MAX_IMAGE_SIZE, "image");

        Image repositoryImage = imageRepository.findById(imageId)
                .orElseThrow(() -> new ImageNotFoundException("Изображение с id %s не найдено".formatted(imageId),
                        eventId));

        String oldKey = repositoryImage.getKey();
        String newKey = generateMediaKey(eventId, image.getOriginalFilename());

        deleteFromS3(oldKey);
        uploadToS3(newKey, image);

        repositoryImage.setKey(newKey);
        imageRepository.update(repositoryImage);
    }

    @Override
    public void deleteImage(Long eventId, Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ImageNotFoundException("Изображение с id %s не найдено".formatted(imageId),
                        eventId));
        String key = image.getKey();

        deleteFromS3(key);

        imageRepository.deleteById(imageId);
    }

    @Override
    public Resource getImage(Long eventId, Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ImageNotFoundException("Изображение с id %s не найдено".formatted(imageId),
                        eventId));
        String key = image.getKey();

        return new InputStreamResource(s3Client.getObject(GetObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(key)
                .build()));
    }

    @Override
    public void saveVideo(Long eventId, MultipartFile video) {
        validateFile(video, MAX_VIDEO_SIZE, "video");
        Event event = getEventOrThrow(eventId);

        String key = generateMediaKey(eventId, video.getOriginalFilename());
        uploadToS3(key, video);
        event.setVideoKey(key);
        eventRepository.update(event);
    }

    @Override
    public void updateVideo(Long eventId, MultipartFile video) {
        validateFile(video, MAX_VIDEO_SIZE, "video");
        Event event = getEventOrThrow(eventId);

        if (event.getVideoKey() != null) {
            deleteFromS3(event.getVideoKey());
        }

        String key = generateMediaKey(eventId, video.getOriginalFilename());
        uploadToS3(key, video);
        event.setVideoKey(key);
        eventRepository.update(event);
    }

    @Override
    public void deleteVideo(Long eventId) {
        Event event = getEventOrThrow(eventId);
        if (event.getVideoKey() != null) {
            deleteFromS3(event.getVideoKey());
            event.setVideoKey(null);
            eventRepository.update(event);
        }
    }

    @Override
    public Resource getVideoPart(Long eventId, String range) {
        Event event = getEventOrThrow(eventId);
        String key = event.getVideoKey();
        if (key == null) throw new S3MediaNotFoundException("Видео не найдено", eventId);

        return new InputStreamResource(s3Client.getObject(GetObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(key)
                .build()));
    }

    private Event getEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Мероприятие не найдено", eventId));
    }

    private void validateFile(MultipartFile file, long maxSize, String type) {
        if (file == null || file.isEmpty()) {
            throw new S3NotValidException("Файл %s не может быть пустым".formatted(type));
        }
        if (file.getSize() > maxSize) {
            throw new S3NotValidException("Файл %s превышает допустимый размер".formatted(type));
        }
    }

    private void uploadToS3(String key, MultipartFile file) {
        try {
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(properties.getBucket())
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            throw new S3LoadException(e.getMessage());
        }
    }

    private void deleteFromS3(String key) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(key)
                .build());
    }

    private String generateMediaKey(Long eventId, String originalFilename) {
        String uuid = UUID.randomUUID().toString();
        return "eventsImages/%s/%s-%s".formatted(eventId,uuid,originalFilename);
    }
}
