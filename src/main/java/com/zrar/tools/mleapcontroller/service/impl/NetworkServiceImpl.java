package com.zrar.tools.mleapcontroller.service.impl;

import com.zrar.tools.mleapcontroller.service.NetworkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jingfeng Zhou
 */
@Service
@Slf4j
public class NetworkServiceImpl implements NetworkService {

    @Cacheable("NetworkServiceImpl")
    @Override
    public List<String> getReachableMleapList() {
        String mleap = "mleap";
        List<String> mleapList = new ArrayList<>();
        int i = 1;
        while (true) {
            String tryMleap = mleap + i;
            if (isReachable(tryMleap)) {
                mleapList.add(tryMleap);
            } else {
                break;
            }
            i++;
        }
        return mleapList;
    }

    @Override
    public boolean isReachable(String remoteInetAddr) {
        boolean reachable = false;
        try {
            log.debug("before getByName");
            InetAddress address = InetAddress.getByName(remoteInetAddr);
            log.debug("after getByName address = {}", address);
            reachable = address.isReachable(1000);
            log.debug("reachable = {}", reachable);
        } catch (Exception e) {
            // 说明不可达
        }
        return reachable;
    }
}
