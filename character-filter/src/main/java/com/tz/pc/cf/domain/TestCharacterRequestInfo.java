package com.tz.pc.cf.domain;

import lombok.Data;

/**
 * <p></p>
 *
 * @author tuanzuo
 * @version 1.7.1
 * @time 2021-11-28 20:26
 **/
@Data
public class TestCharacterRequestInfo {

    /**
     * 姓名
     */
    private String name;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 备注
     */
    private String note;

}
