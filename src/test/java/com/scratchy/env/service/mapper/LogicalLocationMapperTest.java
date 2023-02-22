package com.scratchy.env.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LogicalLocationMapperTest {

    private LogicalLocationMapper logicalLocationMapper;

    @BeforeEach
    public void setUp() {
        logicalLocationMapper = new LogicalLocationMapperImpl();
    }
}
