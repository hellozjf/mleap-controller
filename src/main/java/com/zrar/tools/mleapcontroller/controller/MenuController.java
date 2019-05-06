package com.zrar.tools.mleapcontroller.controller;

import com.zrar.tools.mleapcontroller.entity.MLeapEntity;
import com.zrar.tools.mleapcontroller.repository.MLeapRepository;
import com.zrar.tools.mleapcontroller.service.NetworkService;
import com.zrar.tools.mleapcontroller.vo.MLeapFileVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jingfeng Zhou
 */
@Controller
@Slf4j
public class MenuController {

    @Autowired
    private NetworkService networkService;

    @Autowired
    private MLeapRepository mLeapRepository;

    @GetMapping("/")
    public String main(Model model) {
        List<String> reachableMleapList = networkService.getReachableMleapList();
        List<MLeapFileVO> mLeapFileVOList = reachableMleapList.stream()
                .map(mleap -> {
                    MLeapEntity mLeapEntity = mLeapRepository.findByMleapName(mleap);
                    log.debug("mleapEntity = {}", mLeapEntity);
                    if (mLeapEntity != null) {
                        MLeapFileVO mLeapFileVO = new MLeapFileVO();
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
                        ZoneId zone = ZoneId.of("Asia/Shanghai");
                        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);

                        String fileDate = localDateTime.getYear() + "年" +
                                localDateTime.getMonthValue() + "月" +
                                localDateTime.getDayOfMonth() + "日 " +
                                localDateTime.getHour() + "时" +
                                localDateTime.getMinute() + "分" +
                                localDateTime.getSecond() + "秒";
                        mLeapFileVO.setFileDate(fileDate);
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
        model.addAttribute("mleapFileVOList", mLeapFileVOList);
        return "main";
    }
}
