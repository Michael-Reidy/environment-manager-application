package com.scratchy.env.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.scratchy.env.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NamespaceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(NamespaceDTO.class);
        NamespaceDTO namespaceDTO1 = new NamespaceDTO();
        namespaceDTO1.setId(1L);
        NamespaceDTO namespaceDTO2 = new NamespaceDTO();
        assertThat(namespaceDTO1).isNotEqualTo(namespaceDTO2);
        namespaceDTO2.setId(namespaceDTO1.getId());
        assertThat(namespaceDTO1).isEqualTo(namespaceDTO2);
        namespaceDTO2.setId(2L);
        assertThat(namespaceDTO1).isNotEqualTo(namespaceDTO2);
        namespaceDTO1.setId(null);
        assertThat(namespaceDTO1).isNotEqualTo(namespaceDTO2);
    }
}
