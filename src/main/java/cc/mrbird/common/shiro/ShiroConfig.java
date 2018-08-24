package cc.mrbird.common.shiro;


import cc.mrbird.common.config.FebsProperies;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

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



    private RedisManager redisManager(){
        RedisManager manager=new RedisManager();
        manager.setHost(host);
        manager.setPort(port);
        manager.setTimeout(timeout);
        manager.setExpire(febsProperies.getShiro().getExpireIn());
        return  manager;
    }

    private RedisCacheManager redisCacheManager(){
        RedisCacheManager cacheManager=new RedisCacheManager();
        cacheManager.setRedisManager(redisManager());
//        cacheManager.setKeySerializer(new StringSerializer());
//        cacheManager.setValueSerializer(new StringSerializer());
        return  cacheManager;
    }

    public SecurityManager securityManager(){
        DefaultWebSecurityManager manager =new DefaultWebSecurityManager();
        manager.setCacheManager(redisCacheManager());
        manager.setRealm(new ShiroRealm());
        return manager;
    }

}
