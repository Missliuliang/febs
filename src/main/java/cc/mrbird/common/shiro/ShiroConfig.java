package cc.mrbird.common.shiro;


import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import cc.mrbird.common.config.FebsProperies;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionContext;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.crazycake.shiro.StringSerializer;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;

@Configuration
public class ShiroConfig {

    @Autowired
    private FebsProperies febsProperies;

    @Value("${spring.redis.host}")
    private  String host;

    @Value("${spring.redis.port}")
    private int port ;

    @Value("${spring.redis.timeout}")
    private int timeout;



    @Bean
    public RedisManager redisManager(){
        RedisManager manager=new RedisManager();
        manager.setHost(host);
        manager.setPort(port);
        manager.setTimeout(timeout);
        manager.setExpire(febsProperies.getShiro().getExpireIn());
        return  manager;
    }

    @Bean
    public RedisCacheManager redisCacheManager(){
        RedisCacheManager cacheManager=new RedisCacheManager();
        cacheManager.setRedisManager(redisManager());
//        cacheManager.setKeySerializer(new StringSerializer());
//        cacheManager.setValueSerializer(new StringSerializer());
        return  cacheManager;
    }

    /**
     * rememberMe cookie 效果是重开浏览器后无需重新登录
     *
     * @return SimpleCookie
     */
    @Bean
    public SimpleCookie rememberMecookie(){
        SimpleCookie simpleCookie=new SimpleCookie("rememberMe");
        simpleCookie.setMaxAge(febsProperies.getShiro().getCookieTimeout());
        return  simpleCookie;
    }

    /**
     * cookie管理对象
     *
     * @return CookieRememberMeManager
     */
    @Bean
    public CookieRememberMeManager rememberMeManager(){
        CookieRememberMeManager rememberMeManager =new CookieRememberMeManager();
        rememberMeManager.setCookie(rememberMecookie());
        // rememberMe cookie 加密的密钥
        rememberMeManager.setCipherKey(Base64.decode("4AvVhmFLUs0KTA3Kprsdag=="));
        return  rememberMeManager;
    }

    @Bean
    public ShiroRealm getRealm(){
        return  new ShiroRealm();
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
        sessionManager.setGlobalSessionTimeout(febsProperies.getShiro().getSessionTimeout());
        sessionManager.setSessionListeners(listeners);
        sessionManager.setSessionDAO(redisSessionDAO());
        return  sessionManager;
    }

    @Bean
    public SecurityManager securityManager(){
        DefaultWebSecurityManager manager =new DefaultWebSecurityManager();
        manager.setCacheManager(redisCacheManager());
        manager.setRealm(getRealm());
        manager.setRememberMeManager(rememberMeManager());
        manager.setCacheManager(redisCacheManager());
        manager.setSessionManager(sessionManager());
        return manager;
    }


    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager){
        ShiroFilterFactoryBean shiroFilter =new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);
        shiroFilter.setLoginUrl(febsProperies.getShiro().getLoginUrl());
        shiroFilter.setSuccessUrl(febsProperies.getShiro().getSuccessUrl());
        shiroFilter.setUnauthorizedUrl(febsProperies.getShiro().getUnauthorizedUrl());


        LinkedHashMap<String ,String> filterChainDefinitionMap =new LinkedHashMap<>();
        // 设置免认证 url
        String[] anonUrls = StringUtils.splitByWholeSeparatorPreserveAllTokens(febsProperies.getShiro().getAnonUrl(), ",");
        for (String url : anonUrls){
            filterChainDefinitionMap.put(url,"anno");
        }
        // 配置退出过滤器，其中具体的退出代码 Shiro已经替我们实现了
        filterChainDefinitionMap.put(febsProperies.getShiro().getLogoutUrl(),"logout");
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
