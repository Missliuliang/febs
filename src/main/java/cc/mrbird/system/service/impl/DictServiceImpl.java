package cc.mrbird.system.service.impl;

import cc.mrbird.common.service.impl.BaseService;
import cc.mrbird.system.dao.DictMapper;
import cc.mrbird.system.domain.Dict;
import cc.mrbird.system.service.DictService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DictServiceImpl  extends BaseService<Dict> implements DictService  {


    @Autowired
    private DictMapper dictMapper;

    @Override
    public List<Dict> findAllDict(Dict dict) {
        try{
            Example example =new Example(Dict.class);
            Example.Criteria criteria = example.createCriteria();
            if (StringUtils.isNotBlank(dict.getKeyy())){
                criteria.andCondition("keyy=",Long.valueOf(dict.getKeyy()));
            }
            if (StringUtils.isNotBlank(dict.getValuee())){
                criteria.andCondition("valuee=",dict.getValuee());
            }
            if (StringUtils.isNotBlank(dict.getTableName())){
                criteria.andCondition("table_name=",dict.getTableName());
            }
            if (StringUtils.isNotBlank(dict.getFieldName())){
                criteria.andCondition("field_name=" ,dict.getFieldName());
            }
            example.setOrderByClause("dict_id");
            return selectByExample(example);


        }catch (Exception e){
            e.printStackTrace();
            return new ArrayList<Dict>();
        }
    }

    @Override
    public Dict findById(Long id) {
        return this.selectByKey(id);
    }

    @Override
    public int addDict(Dict dict) {
        return dictMapper.insert(dict);
    }

    @Override
    public int updateDict(Dict dict) {
        return this.updateNotNull(dict);
    }

    @Override
    public int deleteDict(String ids) {
        List<String> list = Arrays.asList(ids.split(","));
        return  this.batchDelete(list,"dictId",Dict.class);

    }
}
