package dk.jarry.todo.boundary;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.QueryParam;

@ApplicationScoped
public class ToDoService {

    @Inject
    MinioClient minioClient;

    // @ConfigProperty(name = "minio.bucket-name")
    // String bucketName = "todo";
    public static final String BUCKET_NAME = "todo";

    private static final long PART_SIZE = 50 * 1024 * 1024;

    public String addObject(String fileName) throws IOException, MinioException, GeneralSecurityException {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(BUCKET_NAME).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET_NAME).build());
        }
        String dummyFile = "Dummy content";
        try (InputStream is = new ByteArrayInputStream((dummyFile.getBytes()))) {
            ObjectWriteResponse response = minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(fileName)
                            .contentType("text/xml") // TODO : Parametrize
                            .stream(is, -1, PART_SIZE)
                            .build());
            return response.bucket() + "/" + response.object();
        } catch (MinioException | GeneralSecurityException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public String getObject(@QueryParam("name") String fileName) {
        String dummyFile = "Dummy content";
        try (InputStream is = new ByteArrayInputStream((dummyFile.getBytes()))) {
            GetObjectResponse response = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(fileName)
                            .build());
            return response.bucket() + "/" + response.object();
        } catch (MinioException | GeneralSecurityException | IOException e) {
            throw new IllegalStateException(e);
        }
    }


}
