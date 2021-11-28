package com.tz.pc.cf.filter;

import com.google.common.collect.Sets;
import org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration;
import org.springframework.boot.web.servlet.filter.OrderedCharacterEncodingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * 自定义字符编码过滤器：对于指定的url使用“GBK”编码 <>br</>
 * 参考 {@link HttpEncodingAutoConfiguration}
 *
 * @author tuanzuo
 * @version 1.7.1
 * @time 2021-11-28 20:26
 * @see HttpEncodingAutoConfiguration
 **/
public class OrderedCharacterEncodingFilterEx extends OrderedCharacterEncodingFilter {

    private static final String CHARACTER_ENCODING_GBK = "GBK";
    //使用‘GBK’进行编码的url
    private Set<String> urlListToGBK = Sets.newHashSet("/test/character/gbk");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String encoding = getEncoding();
        /**重新设置编码*/
        encoding = this.setEncoding(request, encoding);
        if (encoding != null) {
            if (isForceRequestEncoding() || request.getCharacterEncoding() == null) {
                request.setCharacterEncoding(encoding);
            }
            if (isForceResponseEncoding()) {
                response.setCharacterEncoding(encoding);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String setEncoding(HttpServletRequest request, String encoding) {
        String url = request.getRequestURI();
        /**重新设置编码为GBK*/
        if (urlListToGBK.contains(url)) {
            encoding = CHARACTER_ENCODING_GBK;
        }
        return encoding;
    }


}