package com.scratchy.env.repository;

import com.scratchy.env.domain.Environment;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Environment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EnvironmentRepository extends JpaRepository<Environment, Long> {}
