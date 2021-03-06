/* this is the utility class used to connect to S3 and do
 * operations like upload and download files from buckets
 */
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class S3Connector {
    private AmazonS3 s3;
    private String currentBucket;

    public S3Connector() {
        this.s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_WEST_2).build();
    }

    public void listBuckets() {
        for (Bucket bucket : this.s3.listBuckets()) {
            System.out.println(" - " + bucket.getName());
        }
        System.out.println();
    }

    public void downloadFile(String keyName) {
        try {
            S3Object obj = this.s3.getObject(currentBucket, keyName);
            S3ObjectInputStream s3is = obj.getObjectContent();
            FileOutputStream fos = new FileOutputStream(new File(keyName));

            byte[] read_buf = new byte[1024];
            int read_len = s3is.read(read_buf);

            while (read_len > 0) {
                fos.write(read_buf, 0, read_len);
                read_len = s3is.read(read_buf);
            }
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } finally {
            s3is.close();
            fos.close();
        }
    }

    public void uploadFile(File file) {
        try {
            s3.putObject(this.currentBucket, file.getName(), file);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
    }

    public List<Bucket> buckets() {
        return this.s3.listBuckets();
    }

    public AmazonS3 getS3() {
        return this.s3;
    }

    public void setS3(AmazonS3 s3) {
        this.s3 = s3;
    }

    public String getCurrentBucket() {
        return this.currentBucket;
    }

    public void setCurrentBucket(String currentBucket) {
        this.currentBucket = currentBucket;
    }
}
