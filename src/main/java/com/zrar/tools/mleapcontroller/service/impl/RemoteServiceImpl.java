package com.zrar.tools.mleapcontroller.service.impl;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import com.zrar.tools.mleapcontroller.config.RemoteConfig;
import com.zrar.tools.mleapcontroller.service.RemoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Jingfeng Zhou
 */
@Service
@Slf4j
public class RemoteServiceImpl implements RemoteService {

    @Autowired
    private RemoteConfig remoteConfig;

    @Override
    public boolean scp(String absoluteFilePath) {
        try {
            Connection conn = new Connection(remoteConfig.getAddress(), remoteConfig.getPort());
            conn.connect();
            boolean isAuth = conn.authenticateWithPassword(remoteConfig.getUsername(), remoteConfig.getPassword());
            if (!isAuth) {
                log.error("connect to {}, {}/{} 认证失败",
                        remoteConfig.getAddress(),
                        remoteConfig.getUsername(),
                        remoteConfig.getPassword());
                return false;
            }
            SCPClient scpClient = conn.createSCPClient();
            scpClient.put(absoluteFilePath, remoteConfig.getFolder());
            conn.close();
            return true;
        } catch (Exception e) {
            log.error("e = {}", e);
            return false;
        }
    }
}
