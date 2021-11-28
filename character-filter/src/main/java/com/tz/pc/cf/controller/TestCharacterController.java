package com.tz.pc.cf.controller;

import com.tz.pc.cf.domain.TestCharacterRequestInfo;
import com.tz.pc.cf.filter.OrderedCharacterEncodingFilterEx;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p></p>
 *
 * @author tuanzuo
 * @version 1.7.1
 * @time 2021-11-28 20:26
 **/
@RestController
@RequestMapping("test")
public class TestCharacterController {

    /***
     * 会走到{@link OrderedCharacterEncodingFilterEx}中将编码设置为“GBK”
     */
    @RequestMapping("character/gbk")
    public String testCharacter01(TestCharacterRequestInfo info) {
        info.setNote("备注");
        return info.toString();
    }

    @RequestMapping("character")
    public String testCharacter02(TestCharacterRequestInfo info) {
        info.setNote("备注");
        return info.toString();
    }
}