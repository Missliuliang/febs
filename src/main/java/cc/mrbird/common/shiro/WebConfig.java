package cc.mrbird.common.shiro;

import cc.mrbird.common.config.FebsProperies;
import cc.mrbird.common.xss.XssFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class WebConfig {

    @Autowired
    private FebsProperies febsProperies;

    @Bean
    public FilterRegistrationBean xxsFilterRegistrationBean(){
        FilterRegistrationBean filterRegistrationBean =new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new XssFilter());
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.setEnabled(true);
        filterRegistrationBean.addUrlPatterns("/*");
        Map<String ,String> initParameters= new HashMap<>();
        initParameters.put("excludes", "/favicon.ico,/img/*,/js/*,/css/*");
        initParameters.put("isIncludeRichText", "true");
        filterRegistrationBean.setInitParameters(initParameters);
        return  filterRegistrationBean;
    }

    @Bean
    public ObjectMapper objectMapper(){
        ObjectMapper objectMapper =new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat(febsProperies.getTimeFormat()));
        return  objectMapper;
    }
}
