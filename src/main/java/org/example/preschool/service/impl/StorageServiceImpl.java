package org.example.preschool.service.impl;

import java.net.URI;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

import org.example.preschool.model.dto.CompleteMultipartDto;
import org.example.preschool.model.request.CopyRequest;
import org.example.preschool.service.StorageService;
import org.example.preschool.util.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.CopyObjectResponse;

@Service
public class StorageServiceImpl implements StorageService {
    private static final String STORAGE_PATH_FORMAT = "%s/%s/%s";
    private static final DateTimeFormatter DATE_FOLDER_FORMAT = DateTimeFormatter.BASIC_ISO_DATE;

    @Value("${storage.minio.public-endpoint}")
    private String publicEndpoint;

    private final S3Client s3Client;

    public StorageServiceImpl(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public CompleteMultipartDto copy(CopyRequest request) {
        if (request == null || !StringUtils.hasText(request.mainPath())) {
            throw new IllegalArgumentException("main_path không được để trống");
        }
        if (!StringUtils.hasText(request.bucket())) {
            throw new IllegalArgumentException("bucket không được để trống");
        }
        if (!StringUtils.hasText(request.contentType())) {
            throw new IllegalArgumentException("content_type không được để trống");
        }

        URI sourceUri;
        URI configuredPublicEndpoint = URI.create(publicEndpoint);
        try {
            sourceUri = URI.create(request.mainPath().trim());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("main_path không phải URL hợp lệ", exception);
        }

        if (!Objects.equals(sourceUri.getScheme(), configuredPublicEndpoint.getScheme())
                || !Objects.equals(sourceUri.getHost(), configuredPublicEndpoint.getHost())
                || sourceUri.getPort() != configuredPublicEndpoint.getPort()) {
            throw new IllegalArgumentException("main_path không thuộc storage public endpoint");
        }

        String sourcePath = sourceUri.getPath();
        if (sourcePath == null || sourcePath.length() <= 1) {
            throw new IllegalArgumentException("main_path thiếu bucket và object key");
        }

        String[] pathParts = sourcePath.substring(1).split("/", 2);
        if (pathParts.length != 2 || !StringUtils.hasText(pathParts[0]) || !StringUtils.hasText(pathParts[1])) {
            throw new IllegalArgumentException("main_path phải có dạng /<bucket>/<object-key>");
        }

        String targetKey = STORAGE_PATH_FORMAT.formatted(
                sanitizeContentType(request.contentType()),
                LocalDate.now(ZoneOffset.UTC).format(DATE_FOLDER_FORMAT),
                StringUtil.generateId()
        );

        CopyObjectResponse response = s3Client.copyObject(CopyObjectRequest.builder()
                .copySource(pathParts[0] + "/" + pathParts[1])
                .destinationBucket(request.bucket())
                .destinationKey(targetKey)
                .build());

        String location = STORAGE_PATH_FORMAT.formatted(publicEndpoint, request.bucket(), targetKey);
        String eTag = response.copyObjectResult() == null
                ? null
                : response.copyObjectResult().eTag();

        return new CompleteMultipartDto(
                request.bucket(),
                targetKey,
                location,
                null,
                null,
                null,
                null,
                eTag
        );
    }

    private String sanitizeContentType(String contentType) {
        if (!StringUtils.hasText(contentType)) {
            return "UNDEFINED";
        }

        String normalizedContentType = contentType
                .trim()
                .toLowerCase(Locale.ROOT)
                .split(";", 2)[0]
                .trim();

        if (normalizedContentType.startsWith("image/")) return "IMAGE";
        if (normalizedContentType.startsWith("video/")) return "VIDEO";
        if (normalizedContentType.startsWith("audio/")) return "AUDIO";

        return switch (normalizedContentType) {
            case "application/pdf",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "application/vnd.ms-excel",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "application/vnd.ms-powerpoint",
                    "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                    "application/vnd.oasis.opendocument.text",
                    "application/vnd.oasis.opendocument.spreadsheet",
                    "application/vnd.oasis.opendocument.presentation",
                    "application/rtf",
                    "text/rtf",
                    "application/x-hwp",
                    "application/haansofthwp",
                    "application/vnd.hancom.hwp",
                    "application/octet-stream",
                    "text/plain",
                    "text/csv",
                    "application/zip",
                    "application/x-rar-compressed",
                    "application/x-7z-compressed" -> "FILE";
            default -> "UNDEFINED";
        };
    }
}
