package com.scratchy.env.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.scratchy.env.domain.Namespace;
import com.scratchy.env.repository.NamespaceRepository;
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
 * Spring Data Elasticsearch repository for the {@link Namespace} entity.
 */
public interface NamespaceSearchRepository extends ElasticsearchRepository<Namespace, Long>, NamespaceSearchRepositoryInternal {}

interface NamespaceSearchRepositoryInternal {
    Page<Namespace> search(String query, Pageable pageable);

    Page<Namespace> search(Query query);

    void index(Namespace entity);
}

class NamespaceSearchRepositoryInternalImpl implements NamespaceSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final NamespaceRepository repository;

    NamespaceSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, NamespaceRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Namespace> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<Namespace> search(Query query) {
        SearchHits<Namespace> searchHits = elasticsearchTemplate.search(query, Namespace.class);
        List<Namespace> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Namespace entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
