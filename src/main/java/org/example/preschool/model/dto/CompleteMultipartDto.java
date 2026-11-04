package org.example.preschool.model.dto;

public record CompleteMultipartDto(
        String bucket,
        String objectKey,
        String location,
        String videoImgPreviewPath,
        Integer videoImgPreviewWidth,
        Integer videoImgPreviewHeight,
        Double videoDuration,
        String eTag
) {
}
