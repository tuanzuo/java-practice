### 1、背景
在web应用中默认的编码方式为“UTF-8”，如果在应用中我们会调用第三方的接口进行支付，支付成功后第三方会回调我们的接口，但是第三方
回调我们接口时采用的是“GBK”的编码，而且回调的参数中有中文，这样我们接收到的数据就是乱码的了

### 2、原因分析
因为org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration#characterEncodingFilter()方法中会对编码进行设置
```java
        @Bean
	@ConditionalOnMissingBean
	public CharacterEncodingFilter characterEncodingFilter() {
		CharacterEncodingFilter filter = new OrderedCharacterEncodingFilter();
        //设置编码
		filter.setEncoding(this.properties.getCharset().name());
		filter.setForceRequestEncoding(this.properties.shouldForce(Encoding.Type.REQUEST));
		filter.setForceResponseEncoding(this.properties.shouldForce(Encoding.Type.RESPONSE));
		return filter;
	}
```
其中“filter.setEncoding(this.properties.getCharset().name());”这段代码对编码进行设置时“this.properties.getCharset().name()”取的是“org.springframework.boot.web.servlet.server.Encoding.charset”属性的值，charset属性的默认值就为“UTF-8”

```java
package org.springframework.boot.web.servlet.server;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

public class Encoding {

	/**
	 * Default HTTP encoding for Servlet applications.
	 */
	public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

	/**
	 * Charset of HTTP requests and responses. Added to the "Content-Type" header if not set explicitly.
	 */
    //默认值为"UTF-8"
	private Charset charset = DEFAULT_CHARSET;
}
```



### 3、解决方法
3.1 自定义一个字符编码过滤器(OrderedCharacterEncodingFilterEx)继承org.springframework.boot.web.servlet.filter.OrderedCharacterEncodingFilter，
重写doFilterInternal()方法，对特定的url设置编码为“GBK”

```java
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

    //重写doFilterInternal方法
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
```

3.2 定义一个配置类(HttpEncodingAutoConfigurationEx)配置一个OrderedCharacterEncodingFilterEx类型的Bean

```java
import com.tz.pc.cf.filter.OrderedCharacterEncodingFilterEx;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.server.Encoding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;

/**
 * 参考 {@link HttpEncodingAutoConfiguration}
 * {@link EnableAutoConfiguration Auto-configuration} for configuring the encoding to use
 * in web applications.
 *
 * @author Stephane Nicoll
 * @author Brian Clozel
 * @since 2.0.0
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ServerProperties.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(OrderedCharacterEncodingFilterEx.class)
@ConditionalOnProperty(prefix = "server.servlet.encoding", value = "enabled", matchIfMissing = true)
public class HttpEncodingAutoConfigurationEx {

	private final Encoding properties;

	public HttpEncodingAutoConfigurationEx(ServerProperties properties) {
		this.properties = properties.getServlet().getEncoding();
	}

	/**
	 * 自定义CharacterEncodingFilter <>br</>
	 * 参考 {@link HttpEncodingAutoConfiguration#characterEncodingFilter()}
	 */
	@Bean
	public CharacterEncodingFilter characterEncodingFilter() {
        //使用自定义的OrderedCharacterEncodingFilterEx
		CharacterEncodingFilter filter = new OrderedCharacterEncodingFilterEx();
		filter.setEncoding(this.properties.getCharset().name());
		filter.setForceRequestEncoding(this.properties.shouldForce(Encoding.Type.REQUEST));
		filter.setForceResponseEncoding(this.properties.shouldForce(Encoding.Type.RESPONSE));
		return filter;
	}

}
```



### 4、测试
4.0 定义两个接口“/test/character/gbk”和“/test/character”，第一个接口在OrderedCharacterEncodingFilterEx中会设置为“GBK”编码

4.1 执行CharacterFilterTest.testCharacter01()方法：中文参数正常解析不乱码

4.2 执行CharacterFilterTest.testCharacter02()方法：中文参数会乱码

4.3 执行CharacterFilterTest.testCharacter01()方法：中文参数会乱码

4.4 执行CharacterFilterTest.testCharacter02()方法：中文参数正常解析不乱码
