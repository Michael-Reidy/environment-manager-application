package com.scratchy.env.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NamespaceMapperTest {

    private NamespaceMapper namespaceMapper;

    @BeforeEach
    public void setUp() {
        namespaceMapper = new NamespaceMapperImpl();
    }
}
