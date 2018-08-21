package cc.mrbird.common.service;

import java.util.List;

public interface IService<T> {

    List<T> selectAll();
    List<T> selectByExample(Object object);
    T selectByKey(Object key);

    int save(Object entity);

    int delete(Object key);

    int batchDelete(List<String> list , String property ,Class clazz);

    int updateAll(T entity);

    int updateNotNull(T entity);



}
