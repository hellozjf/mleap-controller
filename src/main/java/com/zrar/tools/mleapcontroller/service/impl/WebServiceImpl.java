package com.zrar.tools.mleapcontroller.service.impl;

import com.zrar.tools.mleapcontroller.constant.CutMethodEnum;
import com.zrar.tools.mleapcontroller.entity.MLeapEntity;
import com.zrar.tools.mleapcontroller.repository.MLeapRepository;
import com.zrar.tools.mleapcontroller.service.NetworkService;
import com.zrar.tools.mleapcontroller.service.WebService;
import com.zrar.tools.mleapcontroller.vo.MLeapFileVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jingfeng Zhou
 */
@Service
@Slf4j
public class WebServiceImpl implements WebService {

    @Autowired
    private NetworkService networkService;

    @Autowired
    private MLeapRepository mLeapRepository;

    @Override
    public List<MLeapFileVO> getAllMLeapFileVOList() {
        List<String> reachableMleapList = networkService.getReachableMleapList();
        List<MLeapFileVO> mLeapFileVOList = reachableMleapList.stream()
                .map(mleap -> {
                    MLeapEntity mLeapEntity = mLeapRepository.findByMleapName(mleap);
                    log.debug("mleapEntity = {}", mLeapEntity);
                    if (mLeapEntity != null) {
                        MLeapFileVO mLeapFileVO = new MLeapFileVO();
                        mLeapFileVO.setId(mLeapEntity.getId());
                        mLeapFileVO.setUrl(mLeapEntity.getMleapName());

                        File file = new File(mLeapEntity.getModelPath());
                        mLeapFileVO.setFileName(file.getName());

                        // 文件大小
                        if (file.length() < 1024) {
                            mLeapFileVO.setFileSize(file.length() + "B");
                        } else if (file.length() < 1024 * 1024) {
                            mLeapFileVO.setFileSize(String.format("%.3fKB", file.length() / 1024.0));
                        } else if (file.length() < 1024 * 1024 * 1024) {
                            mLeapFileVO.setFileSize(String.format("%.3fMB", file.length() / 1024.0 / 1024.0));
                        }

                        // 文件更新时间
                        Instant instant = Instant.ofEpochMilli(mLeapEntity.getGmtModified());
                        // ZoneId.of("Asia/Shanghai");
                        ZoneId zone = ZoneId.systemDefault();
                        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);

                        String fileDate = localDateTime.getYear() + "年" +
                                localDateTime.getMonthValue() + "月" +
                                localDateTime.getDayOfMonth() + "日 " +
                                localDateTime.getHour() + "时" +
                                localDateTime.getMinute() + "分" +
                                localDateTime.getSecond() + "秒";
                        mLeapFileVO.setFileDate(fileDate);
                        // 查找分词方式
                        for (CutMethodEnum cutMethodEnum : CutMethodEnum.values()) {
                            if (cutMethodEnum.getName().equalsIgnoreCase(mLeapEntity.getCutMethod())) {
                                mLeapFileVO.setCutMethod(cutMethodEnum.getDesc());
                            }
                        }
                        // 模型描述
                        mLeapFileVO.setDesc(mLeapEntity.getDesc());
                        return mLeapFileVO;
                    } else {
                        MLeapFileVO mLeapFileVO = new MLeapFileVO();
                        mLeapFileVO.setUrl(mleap);
                        mLeapFileVO.setFileName("");
                        mLeapFileVO.setFileSize("");
                        mLeapFileVO.setFileDate("");
                        return mLeapFileVO;
                    }
                })
                .collect(Collectors.toList());

        return mLeapFileVOList;
    }
}
