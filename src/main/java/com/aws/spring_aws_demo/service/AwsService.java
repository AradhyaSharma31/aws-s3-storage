package com.aws.spring_aws_demo.service;

import com.amazonaws.AmazonClientException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface AwsService {
    void uploadFile(
            final String bucketName,
            final String keyName,
            final Long contentLength,
            final String contentType,
            final InputStream value
            ) throws AmazonClientException;

    ByteArrayOutputStream downloadFile(
            final String bucketName,
            final String keyName
    ) throws IOException, AmazonClientException;

    List<String> listFiles(
            final String bucketName
    ) throws AmazonClientException;

    void deleteFiles(
            final String bucketName,
            final String keyName
    ) throws AmazonClientException;
}
