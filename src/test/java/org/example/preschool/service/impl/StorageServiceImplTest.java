package org.example.preschool.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.example.preschool.model.dto.CompleteMultipartDto;
import org.example.preschool.model.request.CopyRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.CopyObjectResponse;
import software.amazon.awssdk.services.s3.model.CopyObjectResult;

@ExtendWith(MockitoExtension.class)
class StorageServiceImplTest {
    private static final String PUBLIC_ENDPOINT = "https://storage.example.com:8003";

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private StorageServiceImpl storageService;

    @Test
    void copiesObjectAndReturnsItsLocation() {
        ReflectionTestUtils.setField(storageService, "publicEndpoint", PUBLIC_ENDPOINT);
        when(s3Client.copyObject(any(CopyObjectRequest.class))).thenReturn(
                CopyObjectResponse.builder()
                        .copyObjectResult(CopyObjectResult.builder().eTag("copied-etag").build())
                        .build()
        );

        CompleteMultipartDto result = storageService.copy(new CopyRequest(
                "students",
                "image/png",
                PUBLIC_ENDPOINT + "/temporary/source.png"
        ));

        ArgumentCaptor<CopyObjectRequest> requestCaptor = ArgumentCaptor.forClass(CopyObjectRequest.class);
        verify(s3Client).copyObject(requestCaptor.capture());
        CopyObjectRequest copyRequest = requestCaptor.getValue();
        assertEquals("temporary/source.png", copyRequest.copySource());
        assertEquals("students", copyRequest.destinationBucket());
        assertEquals("IMAGE", result.objectKey().split("/", 2)[0]);
        assertEquals("copied-etag", result.eTag());
        assertEquals(PUBLIC_ENDPOINT + "/students/" + result.objectKey(), result.location());
    }

    @Test
    void rejectsSourceOutsideConfiguredPublicEndpoint() {
        ReflectionTestUtils.setField(storageService, "publicEndpoint", PUBLIC_ENDPOINT);

        assertThrows(IllegalArgumentException.class, () -> storageService.copy(new CopyRequest(
                "students",
                "image/png",
                "https://other.example.com/temporary/source.png"
        )));

        verify(s3Client, never()).copyObject(any(CopyObjectRequest.class));
    }
}
