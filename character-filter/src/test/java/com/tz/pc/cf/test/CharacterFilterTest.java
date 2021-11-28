package com.tz.pc.cf.test;

import com.alibaba.fastjson.JSON;
import com.tz.pc.cf.CharacterFilterApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(classes = CharacterFilterApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CharacterFilterTest {

    @Autowired
    private RestTemplate restTemplate;

    //---------------------------------调用"/test/character/gbk"接口-------------------------------------------------

    @Test
    public void testCharacter01() {
        MultiValueMap<String, String> postParameters = new LinkedMultiValueMap<>();
        postParameters.add("name", "小明");
        HttpHeaders headers = new HttpHeaders();
        //设置参数的编码方式为gbk
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=gbk");
        HttpEntity<MultiValueMap<String, String>> r = new HttpEntity<>(postParameters, headers);
        /**会走到 {@link FormHttpMessageConverter#serializeForm(org.springframework.util.MultiValueMap, java.nio.charset.Charset)} 中对参数进行编码*/
        ResponseEntity<String> result = restTemplate.postForEntity("http://127.0.0.1/test/character/gbk", r, String.class);

        /**
         * 中文的入参正常解析不会乱码
         */
        System.err.println(JSON.toJSONString(result));
    }


    @Test
    public void testCharacter02() {
        MultiValueMap<String, String> postParameters = new LinkedMultiValueMap<>();
        postParameters.add("name", "小明");
        HttpHeaders headers = new HttpHeaders();
        //设置参数的编码方式为utf-8
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        HttpEntity<MultiValueMap<String, String>> r = new HttpEntity<>(postParameters, headers);
        ResponseEntity<String> result = restTemplate.postForEntity("http://127.0.0.1/test/character/gbk", r, String.class);

        /**
         * 中文的入参会乱码
         */
        System.err.println(JSON.toJSONString(result));
    }

    //---------------------------------调用"/test/character"接口-------------------------------------------------

    @Test
    public void testCharacter03() {
        MultiValueMap<String, String> postParameters = new LinkedMultiValueMap<>();
        postParameters.add("name", "小明");
        HttpHeaders headers = new HttpHeaders();
        //设置参数的编码方式为gbk
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=gbk");
        HttpEntity<MultiValueMap<String, String>> r = new HttpEntity<>(postParameters, headers);
        /**会走到 {@link FormHttpMessageConverter#serializeForm(org.springframework.util.MultiValueMap, java.nio.charset.Charset)} 中对参数进行编码*/
        ResponseEntity<String> result = restTemplate.postForEntity("http://127.0.0.1/test/character", r, String.class);

        /**
         * 中文的入参会乱码
         */
        System.err.println(JSON.toJSONString(result));
    }


    @Test
    public void testCharacter04() {
        MultiValueMap<String, String> postParameters = new LinkedMultiValueMap<>();
        postParameters.add("name", "小明");
        HttpHeaders headers = new HttpHeaders();
        //设置参数的编码方式为utf-8
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        HttpEntity<MultiValueMap<String, String>> r = new HttpEntity<>(postParameters, headers);
        ResponseEntity<String> result = restTemplate.postForEntity("http://127.0.0.1/test/character", r, String.class);

        /**
         * 中文的入参正常解析不会乱码
         */
        System.err.println(JSON.toJSONString(result));
    }
}