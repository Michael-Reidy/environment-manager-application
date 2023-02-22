package com.scratchy.env.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.scratchy.env.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NamespaceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Namespace.class);
        Namespace namespace1 = new Namespace();
        namespace1.setId(1L);
        Namespace namespace2 = new Namespace();
        namespace2.setId(namespace1.getId());
        assertThat(namespace1).isEqualTo(namespace2);
        namespace2.setId(2L);
        assertThat(namespace1).isNotEqualTo(namespace2);
        namespace1.setId(null);
        assertThat(namespace1).isNotEqualTo(namespace2);
    }
}
