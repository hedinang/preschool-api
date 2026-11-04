package org.example.preschool.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CopyRequest(
        @JsonProperty("bucket") String bucket,
        @JsonProperty("content_type") String contentType,
        @JsonProperty("main_path") String mainPath
) {
}
