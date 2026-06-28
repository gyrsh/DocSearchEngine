package com.searchengine.documentservice.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.searchengine.shared.models.Document;

@Repository
public interface DocumentDAO extends CrudRepository<Document, String> {
}
