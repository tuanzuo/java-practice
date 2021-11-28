package com.tz.pc.cf.config;

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