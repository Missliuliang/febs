package cc.mrbird.system.service.impl;

import cc.mrbird.common.domain.Tree;
import cc.mrbird.common.service.impl.BaseService;
import cc.mrbird.common.util.TreeUtils;
import cc.mrbird.system.dao.DeptMapper;
import cc.mrbird.system.domain.Dept;
import cc.mrbird.system.service.DeptService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@Transactional(isolation = Isolation.DEFAULT ,propagation = Propagation.REQUIRED,readOnly = true ,rollbackFor = Exception.class)
public class DeptServiceImpl extends BaseService<Dept> implements DeptService {

    @Autowired
    DeptMapper deptMapper;

    @Override
    public Tree<Dept> getDeptTree() {
        List<Tree<Dept>> ltd=new ArrayList<>();
        List<Dept> allDept = this.findAllDept(new Dept());
        for (Dept dept:allDept
             ) {
            Tree<Dept> deptTree=new Tree<>();
            deptTree.setId(dept.getDeptId().toString());
            deptTree.setParentId(dept.getParentId().toString());
            deptTree.setText(dept.getDeptName());
            ltd.add(deptTree);
        }
        Tree<Dept> t= TreeUtils.build(ltd);
        return t;
    }

    @Override
    public List<Dept> findAllDept(Dept dept) {
        try{
            Example example=new Example(Dept.class);
            if (StringUtils.isNotBlank(dept.getDeptName())){
                example.createCriteria().andCondition("deptName=" ,dept.getDeptName());
            }
            example.setOrderByClause("dept_id");
            return this.deptMapper.selectByExample(example);

        }catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public Dept findByName(String deptName) {
        Example.Criteria criteria = new Example(Dept.class).createCriteria().andCondition("deptName=", deptName);
        List<Dept> depts = this.deptMapper.selectByExample(criteria);
        if (depts.size()>0){
            return depts.get(0);
        }
        return null;
    }

    @Override
    public Dept findById(Long deptIds) {
      return this.deptMapper.selectByPrimaryKey(deptIds);
    }

    @Override
    public void addDept(Dept dept) {
        Long parentId=dept.getParentId();
        if (parentId==null){
            dept.setParentId(0L);
            dept.setCreateTime(new Date());
            this.deptMapper.insert(dept);
        }
    }

    @Override
    public void updateDept(Dept dept) {
        this.deptMapper.updateByPrimaryKeySelective(dept);

    }

    @Override
    public void deleteDept(String deptIds) {
        List<String> list = Arrays.asList(deptIds.split(","));
        this.batchDelete(list,"deptId" ,Dept.class);
        this.deptMapper.changeToTop(list);
    }
}
