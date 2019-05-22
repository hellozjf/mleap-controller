package com.zrar.tools.mleapcontroller.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Jingfeng Zhou
 */
@Controller
@Slf4j
public class MenuController {

    @GetMapping("/")
    public String main(Model model) {
        return "main";
    }

    @GetMapping("/main2")
    public String main2(Model model) {
        return "main2";
    }

    @GetMapping("/rowediting")
    public String rowediting() {
        return "rowediting";
    }

    @GetMapping("/iview")
    public String iview() {
        return "iview";
    }
}
