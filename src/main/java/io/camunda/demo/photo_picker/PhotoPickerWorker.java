package io.camunda.demo.photo_picker;

import io.camunda.demo.photo_picker.enums.PhotoType;
import io.camunda.demo.photo_picker.services.RandomPhotoRetrievalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.stereotype.Component;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;

import java.io.IOException;

@Component
public class PhotoPickerWorker {
    private final static Logger LOG = LoggerFactory.getLogger(PhotoPickerWorker.class);
    @Autowired
    private MongoDatabaseFactory mongoDatabaseFactory;
    @JobWorker(type = "worker-photo-picker")
    public void pickARandomPhoto(@Variable(name = "photo_type") String photoType) {
        LOG.info("A photo will be fetched with the type {}", photoType);
        PhotoType type;
        try {
            type = PhotoType.valueOf(photoType.toUpperCase());
            RandomPhotoRetrievalService photoRetrievalService = new RandomPhotoRetrievalService(mongoDatabaseFactory);
            photoRetrievalService.fetchARandomPhoto(type);
        } catch (IllegalArgumentException e) {
            LOG.error("Could not parse the photo type",
                    e);
            // error to be handled
        } catch (IOException e) {
            LOG.error("Could not create a image file",
                    e);
        }
    }
}
