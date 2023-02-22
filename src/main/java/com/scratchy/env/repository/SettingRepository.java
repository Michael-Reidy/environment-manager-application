package com.scratchy.env.repository;

import com.scratchy.env.domain.Setting;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Setting entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SettingRepository extends JpaRepository<Setting, Long> {}
