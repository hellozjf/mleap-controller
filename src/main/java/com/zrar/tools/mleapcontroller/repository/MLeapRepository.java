package com.zrar.tools.mleapcontroller.repository;

import com.zrar.tools.mleapcontroller.entity.MLeapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Jingfeng Zhou
 */
public interface MLeapRepository extends JpaRepository<MLeapEntity, String> {
    MLeapEntity findByModelName(String modelName);
    @Transactional
    void deleteByModelName(String modelName);
}
