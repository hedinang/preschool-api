package org.example.preschool.service;

import org.example.preschool.model.dto.CompleteMultipartDto;
import org.example.preschool.model.request.CopyRequest;

public interface StorageService {
    CompleteMultipartDto copy(CopyRequest request);
}
