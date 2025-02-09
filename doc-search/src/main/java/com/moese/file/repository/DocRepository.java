package com.moese.file.repository;

import com.moese.file.entity.Doc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocRepository extends ElasticsearchRepository<Doc, String> {
}
