package com.scratchy.env.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.scratchy.env.domain.Setting;
import com.scratchy.env.repository.SettingRepository;
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
 * Spring Data Elasticsearch repository for the {@link Setting} entity.
 */
public interface SettingSearchRepository extends ElasticsearchRepository<Setting, Long>, SettingSearchRepositoryInternal {}

interface SettingSearchRepositoryInternal {
    Page<Setting> search(String query, Pageable pageable);

    Page<Setting> search(Query query);

    void index(Setting entity);
}

class SettingSearchRepositoryInternalImpl implements SettingSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final SettingRepository repository;

    SettingSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, SettingRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Setting> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<Setting> search(Query query) {
        SearchHits<Setting> searchHits = elasticsearchTemplate.search(query, Setting.class);
        List<Setting> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Setting entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
