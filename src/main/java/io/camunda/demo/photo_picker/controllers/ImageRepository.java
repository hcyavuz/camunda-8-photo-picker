package io.camunda.demo.photo_picker.controllers;
import io.camunda.demo.photo_picker.models.ImageDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends MongoRepository<ImageDocument, String> {
    Optional<ImageDocument> findByName(String name);
}

