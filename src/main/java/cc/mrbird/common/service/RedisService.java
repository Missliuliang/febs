package cc.mrbird.common.service;

import cc.mrbird.common.domain.RedisInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RedisService  {

    List<RedisInfo> getRedisInfo();

    Map<String ,Object>  getKeysSize();

    Map<String ,Object> getMemoryInfo();

    Set<String> getKeys(String pattern);

    String get(String key);

    String set(String key , String value );

    Long del(String... keys);

    Boolean exists(String key);

    Long pttl(String key);

    Long expire(String key , int second);
}
