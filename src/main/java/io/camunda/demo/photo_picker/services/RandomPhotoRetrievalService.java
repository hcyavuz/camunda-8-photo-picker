package io.camunda.demo.photo_picker.services;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import io.camunda.demo.photo_picker.enums.PhotoType;
import io.camunda.demo.photo_picker.http.HTTPClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
@Component
public class RandomPhotoRetrievalService {
    private MongoDatabaseFactory mongoDatabaseFactory;

    private final static Logger LOG = LoggerFactory.getLogger(RandomPhotoRetrievalService.class);
    private final String HTTP_URL_CAT = "https://placekitten.com/%d/%d",
    HTTP_URL_DOG = "https://place.dog/%d/%d",
    HTTP_URL_BEAR = "https://placebear.com/%d/%d";
    private HTTPClient httpClient;
    // Constructor injection for MongoDatabaseFactory
    public RandomPhotoRetrievalService(MongoDatabaseFactory mongoDatabaseFactory) {
        this.mongoDatabaseFactory = mongoDatabaseFactory;
        this.httpClient = new HTTPClient();
    }


    public void fetchARandomPhoto(PhotoType type) throws IOException {
        byte[] imageBytes = fetchImage(type);
        String imageName = "img-" + System.currentTimeMillis() + ".jpg";

        if(imageBytes != null) {
            LOG.info("Saving image to the DB");
            saveImageToMongoDB(imageName, imageBytes);
        } else {
            LOG.error("Image has no content!");
        }
    }

    // Method to save image to MongoDB GridFS
    private void saveImageToMongoDB(String imageName, byte[] imageBytes) throws IOException {

        GridFSBucket gridFSBucket = GridFSBuckets.create(mongoDatabaseFactory.getMongoDatabase());

        try (ByteArrayInputStream stream = new ByteArrayInputStream(imageBytes)) {
            GridFSUploadOptions options = new GridFSUploadOptions()
                    .chunkSizeBytes(255 * 1024)
                    .metadata(null);  // Add metadata if needed

            ObjectId fileId = gridFSBucket.uploadFromStream(imageName, stream, options);
            System.out.println("Image stored with ID: " + fileId.toHexString());
        }
    }

    private byte[] fetchImage(PhotoType type) throws IOException {
        String url;
        if(type == PhotoType.CAT) {
            url = HTTP_URL_CAT;
        } else if (type == PhotoType.DOG) {
            url = HTTP_URL_DOG;
        } else if (type == PhotoType.BEAR) {
            url = HTTP_URL_BEAR;
        } else {
            throw new RuntimeException("Provided type "+type.name()+" is not yet implemented");
        }
        url = String.format(url, 300, 200);


        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = httpClient.executeHTTPRequest(request)) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            if(response.body() != null) {
                return response.body().bytes();
            }
            return null;
        }
    }

}
