package com.scratchy.env.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.scratchy.env.domain.LogicalLocation;
import com.scratchy.env.repository.LogicalLocationRepository;
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
 * Spring Data Elasticsearch repository for the {@link LogicalLocation} entity.
 */
public interface LogicalLocationSearchRepository
    extends ElasticsearchRepository<LogicalLocation, Long>, LogicalLocationSearchRepositoryInternal {}

interface LogicalLocationSearchRepositoryInternal {
    Page<LogicalLocation> search(String query, Pageable pageable);

    Page<LogicalLocation> search(Query query);

    void index(LogicalLocation entity);
}

class LogicalLocationSearchRepositoryInternalImpl implements LogicalLocationSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final LogicalLocationRepository repository;

    LogicalLocationSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, LogicalLocationRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<LogicalLocation> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<LogicalLocation> search(Query query) {
        SearchHits<LogicalLocation> searchHits = elasticsearchTemplate.search(query, LogicalLocation.class);
        List<LogicalLocation> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(LogicalLocation entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
