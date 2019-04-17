package com.zrar.tools.mleapcontroller.repository;

import com.zrar.tools.mleapcontroller.entity.MLeapEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Jingfeng Zhou
 */
public interface MLeapRepository extends JpaRepository<String, MLeapEntity> {
}
