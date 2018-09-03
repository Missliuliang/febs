package cc.mrbird.common.service.impl;


import cc.mrbird.common.service.IService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;
import cc.mrbird.common.service.IService;

import java.util.List;

@Service
@Transactional(propagation = Propagation.SUPPORTS ,isolation = Isolation.DEFAULT ,readOnly = true ,rollbackFor = Exception.class)
public abstract class BaseServiceImpl<T> implements IService<T> {


    @Autowired
    Mapper mapper;

    public Mapper getMapper(){
        return mapper;
    }

    @Override
    public List<T> selectAll() {
        List list = mapper.selectAll();
        return list;
    }

    @Override
    public T selectByKey(Object key) {
        Object o = mapper.selectByPrimaryKey(key);
        return (T) o;
    }

    @Override
    public List<T> selectByExample(Object object) {
        return mapper.selectByExample(object);
    }



    @Override
    @Transactional
    public int save(Object entity) {
        return mapper.insert(entity);
    }

    @Override
    @Transactional
    public int delete(Object key) {
        return mapper.delete(key);
    }

    @Override
    @Transactional
    public int batchDelete(List<String> list, String property, Class clazz) {
        Example example=new Example(clazz);
        example.createCriteria().andIn(property ,list);
        return  mapper.deleteByExample(example);
    }

    @Override
    public int updateAll(T entity) {
        return mapper.updateByPrimaryKey(entity);
    }

    @Override
    public int updateNotNull(T entity) {
        return mapper.updateByPrimaryKeySelective(entity);
    }
}
