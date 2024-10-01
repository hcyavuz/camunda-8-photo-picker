package io.camunda.demo.photo_picker.controllers;

import io.camunda.demo.photo_picker.models.ImageDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;

@Controller
public class PhotoController {

    @Autowired
    private ImageRepository imageRepository;

    @GetMapping("/api/image/{name}")
    public ResponseEntity<byte[]> getImageByName(@PathVariable String name) {
        Optional<ImageDocument> imageDocument = imageRepository.findByName(name);

        if (imageDocument.isPresent()) {
            byte[] imageData = imageDocument.get().getData();

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "image/jpg");

            return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/api/images")
    @ResponseBody
    public ResponseEntity<List<ImageDocument>> getAllImages() {
        List<ImageDocument> allImages = imageRepository.findAll();

        if (!allImages.isEmpty()) {
            return new ResponseEntity<>(allImages, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}
