package cc.mrbird.system.service;

import cc.mrbird.common.domain.Tree;
import cc.mrbird.common.service.IService;
import cc.mrbird.system.domain.Dept;

import java.util.List;

public interface DeptService extends IService<Dept> {
    Tree<Dept> getDeptTree();
    List<Dept> findAllDept(Dept dept);
    Dept findByName(String deptName);
    Dept findById(Long deptIds);

    void addDept(Dept dept);

    void updateDept(Dept dept);

    void deleteDept(String deptIds);


}
