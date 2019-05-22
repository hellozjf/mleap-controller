package com.zrar.tools.mleapcontroller.service.impl;

import com.zrar.tools.mleapcontroller.config.CustomConfig;
import com.zrar.tools.mleapcontroller.service.RemoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Jingfeng Zhou
 */
@Service
public class RemoteServiceImpl implements RemoteService {

    @Autowired
    private CustomConfig customConfig;

    @Override
    public String createExecCommand(String cmd) {
        String command = "ssh " +
                customConfig.getRemoteUsername() + "@" + customConfig.getBridgeIp()
                + " \"" + cmd + "\"";
        return command;
    }

    @Override
    public String createScpCommand(String from, String to) {
        String command = "scp " + from
                + " " + customConfig.getRemoteUsername() + "@" + customConfig.getBridgeIp() + ":" + to;
        return command;
    }
}
