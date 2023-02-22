package com.scratchy.env.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.scratchy.env.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LogicalLocationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LogicalLocationDTO.class);
        LogicalLocationDTO logicalLocationDTO1 = new LogicalLocationDTO();
        logicalLocationDTO1.setId(1L);
        LogicalLocationDTO logicalLocationDTO2 = new LogicalLocationDTO();
        assertThat(logicalLocationDTO1).isNotEqualTo(logicalLocationDTO2);
        logicalLocationDTO2.setId(logicalLocationDTO1.getId());
        assertThat(logicalLocationDTO1).isEqualTo(logicalLocationDTO2);
        logicalLocationDTO2.setId(2L);
        assertThat(logicalLocationDTO1).isNotEqualTo(logicalLocationDTO2);
        logicalLocationDTO1.setId(null);
        assertThat(logicalLocationDTO1).isNotEqualTo(logicalLocationDTO2);
    }
}
