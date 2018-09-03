package cc.mrbird.common.util;

import cc.mrbird.common.domain.Tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeUtils {

    public static <T> Tree<T> build(List<Tree<T>> nodes) {
        if (nodes ==null) return  null ;
        List<Tree<T>> topNodes= new ArrayList<>();
        //topNodes 存储的根目录
        nodes.forEach(children ->{
            String pid =children.getParentId();
            if (pid==null || "0".equals(pid)){
                topNodes.add(children);
                return;
            }
            //目录列表
            nodes.forEach(node->{
                String id=node.getId();
                if (id==null || id.equals(pid)){
                    node.getChildren().add(children);
                    children.setHasParent(true);
                    node.setChildren(true);
                    return;

                }
            });
        });

        Tree tree =new Tree();
        tree.setId("0");
        tree.setParentId("");
        tree.setHasParent(false);
        tree.setChildren(true);
        tree.setChecked(true);
        tree.setChildren(topNodes);
        tree.setText("根目录");
        Map<String ,Object> map =new HashMap<>();
        map.put("opend",true);
        tree.setState(map);
        return tree;
    }

    public static  <T> List<Tree<T>> buildList(List<Tree<T>> nodes ,String idParam){

        if (nodes ==null) return  null ;
        List<Tree<T>> topNodes =new ArrayList<>();
        nodes.forEach(children ->{
            String pid=children.getParentId();
            if (pid ==null || idParam.equals(pid)){
                topNodes.add(children);
                return;
            }

            nodes.forEach(parent->{
                String  id =parent.getId();
                if (id==null && id.equals(pid)){
                    parent.getChildren().add(children);
                    children.setHasParent(true);
                    parent.setChildren(true);

                }

            });
        });
        return  topNodes;
    }

}
