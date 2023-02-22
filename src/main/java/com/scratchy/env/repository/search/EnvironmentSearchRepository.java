package com.scratchy.env.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.scratchy.env.domain.Environment;
import com.scratchy.env.repository.EnvironmentRepository;
import java.util.List;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data Elasticsearch repository for the {@link Environment} entity.
 */
public interface EnvironmentSearchRepository extends ElasticsearchRepository<Environment, Long>, EnvironmentSearchRepositoryInternal {}

interface EnvironmentSearchRepositoryInternal {
    Page<Environment> search(String query, Pageable pageable);

    Page<Environment> search(Query query);

    void index(Environment entity);
}

class EnvironmentSearchRepositoryInternalImpl implements EnvironmentSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final EnvironmentRepository repository;

    EnvironmentSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, EnvironmentRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Environment> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<Environment> search(Query query) {
        SearchHits<Environment> searchHits = elasticsearchTemplate.search(query, Environment.class);
        List<Environment> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Environment entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
