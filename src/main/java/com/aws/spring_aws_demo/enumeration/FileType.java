package com.aws.spring_aws_demo.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import org.springframework.http.MediaType;

import java.util.Arrays;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PACKAGE)
public enum FileType {

    JPG("jpg", MediaType.IMAGE_JPEG),
    JPEG("jpeg", MediaType.IMAGE_JPEG),
    TXT("txt", MediaType.TEXT_PLAIN),
    PNG("png", MediaType.IMAGE_PNG),
    PDF("pdf", MediaType.APPLICATION_PDF);

    private final String extension;

    private final MediaType mediaType;

    public static MediaType fromFileName(String fileName) {
        // val is similar to auto in c++
        val dotIndex = fileName.lastIndexOf('.');
        val fileExtension = (dotIndex == -1) ? "": fileName.substring(dotIndex + 1);

        return
                Arrays.stream(values())
                        .filter(e -> e.getExtension().equals(fileExtension))
                        .findFirst()
                        .map(FileType::getMediaType)
                        .orElse(MediaType.APPLICATION_OCTET_STREAM);
    }

}
