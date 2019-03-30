package cc.mrbird.common.shiro;


import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.AnonymousFilter;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.filter.authc.UserFilter;
import org.apache.shiro.web.filter.authz.RolesAuthorizationFilter;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.servlet.Filter;
import java.util.*;

@Configuration
public class ShiroConfig {





    //设置redis
    public RedisManager redisManager(){
        RedisManager manager=new RedisManager();

        manager.setHost("111.231.66.170");
        manager.setPort(6379);
        manager.setTimeout(100000);

        return  manager;
    }


    //redis 缓存的实现
    public RedisCacheManager cacheManager(){
        RedisCacheManager cacheManager=new RedisCacheManager();
        cacheManager.setRedisManager(redisManager());
//        cacheManager.setKeySerializer(new StringSerializer());
//        cacheManager.setValueSerializer(new StringSerializer());
        return  cacheManager;
    }
    @Bean
    public RedisSessionDAO redisSessionDAO(){
        RedisSessionDAO dao =new RedisSessionDAO();
        dao.setRedisManager(redisManager());
        return  dao;
    }

    @Bean
    public DefaultWebSessionManager sessionManager(){
        DefaultWebSessionManager sessionManager =new DefaultWebSessionManager();
        Collection<SessionListener> listeners=new ArrayList<>();
        listeners.add(new ShiroSessionListener());
        sessionManager.setGlobalSessionTimeout(10000);
        sessionManager.setSessionListeners(listeners);
        sessionManager.setSessionDAO(redisSessionDAO());
        return  sessionManager;
    }

    /**
     * rememberMe cookie 效果是重开浏览器后无需重新登录
     *
     * @return SimpleCookie
     */
    public SimpleCookie rememberMecookie(){
        SimpleCookie simpleCookie=new SimpleCookie("rememberMe");
        simpleCookie.setMaxAge(86400);
        return  simpleCookie;
    }

    /**
     * cookie管理对象
     *
     * @return CookieRememberMeManager
     */
    public CookieRememberMeManager rememberMeManager(){
        CookieRememberMeManager MeManager =new CookieRememberMeManager();
        MeManager.setCookie(rememberMecookie());
        // rememberMe cookie 加密的密钥
        MeManager.setCipherKey(Base64.decode("4AvVhmFLUs0KTA3Kprsdag=="));
        return  MeManager;
    }

    @Bean
    @Primary
    public ShiroRealm shiroRealm(){
        return  new ShiroRealm();
    }


    @Bean
    @Primary
    public SecurityManager securityManager(){
        DefaultWebSecurityManager manager =new DefaultWebSecurityManager();
        manager.setRealm(shiroRealm ());
        manager.setRememberMeManager(rememberMeManager());
        manager.setCacheManager(cacheManager());
        manager.setSessionManager(sessionManager());
        return manager;
    }

    @Bean
    @Primary
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager){
        ShiroFilterFactoryBean shiroFilter =new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);
        shiroFilter.setLoginUrl("/login");
        shiroFilter.setSuccessUrl("/index");
        shiroFilter.setUnauthorizedUrl("/403");
        Map<String , Filter> filterMap =new HashMap<>();
        filterMap.put("anno" ,new AnonymousFilter());
        filterMap.put("anthc" ,new FormAuthenticationFilter());
        filterMap.put("logout" ,new LogoutFilter());
        filterMap.put("role" ,new RolesAuthorizationFilter());
        filterMap.put("user" ,new UserFilter());
        shiroFilter.setFilters(filterMap);



        LinkedHashMap<String ,String> filterChainDefinitionMap =new LinkedHashMap<>();
        // 设置免认证 url
        String urlconf="/css/**,/js/**,/fonts/**,/img/**,/druid/**,/user/regist,/gifCode,/";
        String[] anonUrls = StringUtils.splitByWholeSeparatorPreserveAllTokens(urlconf, ",");
        for (String url : anonUrls){
            filterChainDefinitionMap.put(url,"anno");
        }
        // 配置退出过滤器，其中具体的退出代码 Shiro已经替我们实现了
        filterChainDefinitionMap.put("/logout","logout");
        // 除上以外所有 url都必须认证通过才可以访问，未通过认证自动访问 LoginUrl
        filterChainDefinitionMap.put("/**" ,"user");
        shiroFilter.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilter;
    }



    /**
     * 用于开启 Thymeleaf 中的 shiro 标签的使用
     *
     * @return ShiroDialect shiro 方言对象
     */
    @Bean
    public ShiroDialect shiroDialect(){
        return  new ShiroDialect();
    }



    /**
     * DefaultAdvisorAutoProxyCreator 和 AuthorizationAttributeSourceAdvisor 用于开启 shiro 注解的使用
     * 如 @RequiresAuthentication， @RequiresUser， @RequiresPermissions 等
     *
     * @return DefaultAdvisorAutoProxyCreator
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator(){
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator =new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return  advisorAutoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager){
            AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor=new AuthorizationAttributeSourceAdvisor();
            authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
            return  authorizationAttributeSourceAdvisor;
    }
    // 生命周期
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
        return  new LifecycleBeanPostProcessor();
    }

}
