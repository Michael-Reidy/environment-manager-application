package com.scratchy.env.repository;

import com.scratchy.env.domain.LogicalLocation;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the LogicalLocation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LogicalLocationRepository extends JpaRepository<LogicalLocation, Long> {}
