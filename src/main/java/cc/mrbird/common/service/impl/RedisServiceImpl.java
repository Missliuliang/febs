package cc.mrbird.common.service.impl;

import cc.mrbird.common.domain.RedisInfo;
import cc.mrbird.common.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Client;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;
import java.util.function.Function;

@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    JedisPool jedisPool;


    private  Object excuteByjedis(Function<Jedis ,Object> f){
        try(Jedis jedis =jedisPool.getResource()){
            return f.apply(jedis);
        }catch (Exception e){
            e.printStackTrace();
            return  null ;
        }
    }

    @Override
    public List<RedisInfo> getRedisInfo() {
        String info = (String) this.excuteByjedis(jedis -> {
            Client client=jedis.getClient();
            client.info();
            return  client.getBulkReply();
        });
        List<RedisInfo> infolist =new ArrayList<>();
        String[] strings = Objects.requireNonNull(info).split("\n");
        RedisInfo redisInfo;
        if (strings.length>0){
            for (String str: strings) {
                redisInfo=new RedisInfo();
                String[] split = str.split(":");
                if (split.length>1){
                    redisInfo.setKey(split[0]);
                    redisInfo.setValue(split[1]);
                    infolist.add(redisInfo);
                }

            }
        }
        return infolist;
    }

    @Override
    public Map<String, Object> getKeysSize() {
        long dbsize= (long) this.excuteByjedis(jedis -> {
            Client client=jedis.getClient();
            client.dbSize();
            return  client.getIntegerReply();
        });
        Map<String ,Object> map=new HashMap<>();
        map.put("create_time" ,System.currentTimeMillis());
        map.put("dbSize" ,dbsize);
        return map;
    }

    @Override
    public Map<String, Object> getMemoryInfo() {
        String info = (String) this.excuteByjedis(
                j -> {
                    Client client = j.getClient();
                    client.info();
                    return client.getBulkReply();
                }
        );
        String[] strs = Objects.requireNonNull(info).split("\n");
        Map<String, Object> map = null;
        for (String s : strs) {
            String[] detail = s.split(":");
            if ("used_memory".equals(detail[0])) {
                map = new HashMap<>();
                map.put("used_memory", detail[1].substring(0, detail[1].length() - 1));
                map.put("create_time", new Date().getTime());
                break;
            }
        }
        return map;
    }

    @Override
    public Set<String> getKeys(String pattern) {
        return (Set<String>) this.excuteByjedis(jedis -> jedis.keys(pattern));
    }

    @Override
    public String get(String key) {
        return (String) this.excuteByjedis(jedis -> jedis.get(key));
    }

    @Override
    public String set(String key, String value) {
        return (String) this.excuteByjedis(jedis -> jedis.set(key,value));
    }

    @Override
    public Long del(String... keys) {
        return (Long) this.excuteByjedis(jedis -> jedis.del(keys));
    }

    @Override
    public Boolean exists(String key) {
        return (Boolean) excuteByjedis(jedis -> jedis.exists(key));
    }

    @Override
    public Long pttl(String key) {
        return (Long) excuteByjedis(jedis -> jedis.pttl(key));
    }

    @Override
    public Long expire(String key, int second) {
        return (Long)excuteByjedis(jedis -> jedis.expire(key,second));
    }
}
