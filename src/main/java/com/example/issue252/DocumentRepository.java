package com.example.issue252;

import org.springframework.content.rest.StoreRestResource;
import org.springframework.data.jpa.repository.JpaRepository;

@StoreRestResource(path = "documents", linkRel = "documents")
public interface DocumentRepository extends JpaRepository<Document, Long> {
}
