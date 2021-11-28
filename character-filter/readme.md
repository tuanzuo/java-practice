### 1、背景
在web应用中默认的编码方式为“UTF-8”，如果在应用中我们会调用第三方的接口进行支付，支付成功后第三方会回调我们的接口，但是第三方
回调我们接口时采用的是“GBK”的编码，而且回调的参数中有中文，这样我们接收到的数据就是乱码的了

### 2、原因分析
因为
```
org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration.characterEncodingFilter()
```
中会执行“filter.setEncoding(this.properties.getCharset().name());”
这段代码进行编码的设置，
“this.properties.getCharset().name()”取的是“org.springframework.boot.web.servlet.server.Encoding.charset”属性的值，charset属性的默认值就为“UTF-8”

### 3、解决方法
3.1 自定义一个字符编码过滤器(OrderedCharacterEncodingFilterEx)继承org.springframework.boot.web.servlet.filter.OrderedCharacterEncodingFilter，
重写doFilterInternal()方法，对特定的url设置编码为“GBK”

3.2 定义一个配置类(HttpEncodingAutoConfigurationEx)配置一个OrderedCharacterEncodingFilterEx类型的Bean

### 4、测试
4.0 定义两个接口“/test/character/gbk”和“/test/character”，第一个接口在OrderedCharacterEncodingFilterEx中会设置为“GBK”编码

4.1 执行CharacterFilterTest.testCharacter01()方法：中文参数正常解析不乱码

4.2 执行CharacterFilterTest.testCharacter02()方法：中文参数会乱码

4.3 执行CharacterFilterTest.testCharacter01()方法：中文参数会乱码

4.4 执行CharacterFilterTest.testCharacter02()方法：中文参数正常解析不乱码
