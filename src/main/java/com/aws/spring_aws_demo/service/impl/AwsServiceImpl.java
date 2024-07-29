package com.aws.spring_aws_demo.service.impl;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.aws.spring_aws_demo.service.AwsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AwsServiceImpl implements AwsService {

    private AmazonS3Client amazonS3Client;

    @Override
    public void uploadFile(String bucketName, String keyName, Long contentLength, String contentType, InputStream value) throws AmazonClientException {
        ObjectMetadata
                metadata = new ObjectMetadata();

        metadata.setContentLength(contentLength);
        metadata.setContentType(contentType);

        amazonS3Client.putObject(bucketName, keyName, value, metadata);

        log.info("File uploaded to bucket ({}): {}", bucketName, keyName);
    }

    @Override
    public ByteArrayOutputStream downloadFile(String bucketName, String keyName) throws IOException, AmazonClientException {
        S3Object s3Object = amazonS3Client.getObject(bucketName, keyName);

        InputStream inputStream = s3Object.getObjectContent();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        int len;
        byte[] buffer = new byte[4096];

        while((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
            outputStream.write(buffer, 0, len);
        }

        log.info("File downloaded from bucket({}): {}", bucketName, keyName);

        return outputStream;
    }

    @Override
    public List<String> listFiles(String bucketName) throws AmazonClientException {
        List<String> keys = new ArrayList<>();
        ObjectListing objectListing = amazonS3Client.listObjects(bucketName);

        while(true) {
            List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();
            if(objectSummaries.isEmpty()) {
                break;
            }

            objectSummaries.stream()
                    .filter(item ->
                            !item.getKey().endsWith("/"))
                    .map(S3ObjectSummary::getKey)
                    .forEach(keys::add);

            objectListing = amazonS3Client.listNextBatchOfObjects(objectListing);
        }

        log.info("Files found in bucket({}): {}", bucketName, keys);
        return keys;
    }

    @Override
    public void deleteFiles(String bucketName, String keyName) throws AmazonClientException {
        amazonS3Client.deleteObject(bucketName, keyName);
        log.info("File deleted from bucket ({}): {}", bucketName, keyName);
    }
}
