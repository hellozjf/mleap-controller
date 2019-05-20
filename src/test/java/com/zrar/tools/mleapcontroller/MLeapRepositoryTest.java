package com.zrar.tools.mleapcontroller;

import com.zrar.tools.mleapcontroller.constant.CutMethodEnum;
import com.zrar.tools.mleapcontroller.entity.MLeapEntity;
import com.zrar.tools.mleapcontroller.repository.MLeapRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Jingfeng Zhou
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class MLeapRepositoryTest {

    @Autowired
    private MLeapRepository mLeapRepository;

    /**
     * 添加税务模型
     */
    @Test
    public void addSwModel() {
        MLeapEntity mLeapEntity = new MLeapEntity();
        mLeapEntity.setModelName("swModel");
        mLeapEntity.setModelDesc("税务模型");
        mLeapEntity.setCutMethodName(CutMethodEnum.WORD_CUT.getName());
        mLeapRepository.save(mLeapEntity);
    }
}
