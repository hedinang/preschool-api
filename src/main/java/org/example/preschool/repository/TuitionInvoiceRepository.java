package org.example.preschool.repository;

import org.example.preschool.model.entity.TuitionInvoice;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TuitionInvoiceRepository extends MongoRepository<TuitionInvoice, String> {
}
