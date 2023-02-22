package com.scratchy.env.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.scratchy.env.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LogicalLocationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LogicalLocation.class);
        LogicalLocation logicalLocation1 = new LogicalLocation();
        logicalLocation1.setId(1L);
        LogicalLocation logicalLocation2 = new LogicalLocation();
        logicalLocation2.setId(logicalLocation1.getId());
        assertThat(logicalLocation1).isEqualTo(logicalLocation2);
        logicalLocation2.setId(2L);
        assertThat(logicalLocation1).isNotEqualTo(logicalLocation2);
        logicalLocation1.setId(null);
        assertThat(logicalLocation1).isNotEqualTo(logicalLocation2);
    }
}
