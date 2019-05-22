package com.zrar.tools.mleapcontroller.service;

/**
 * @author Jingfeng Zhou
 */
public interface RemoteService {
    String createExecCommand(String cmd);
    String createScpCommand(String from, String to);
}
