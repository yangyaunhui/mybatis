package com.yyh.dao;

import com.yyh.bean.Human;
import com.yyh.bean.HumanExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HumanDAO {
//所有---单表:增--数据
//删---根据id,条件删除(动态sql)
//改--根据主键,条件修改(动态sql)
//查---按主键id查对象,查总条数(动态),查所有数据(动态)
    long countByExample(HumanExample example);//用example类查询总条数,动态的sql去查询总条数
    //当Example类为null的时候,执行sql语句如下
    //select count(*) from human
    //当example类不为null的时候,执行sql语句如下
    //select count(*) from human where gender = 2 example参数如何传递
    int deleteByExample(HumanExample example);//按条数删除

    int deleteByPrimaryKey(Integer id);//按主键id删除

    int insert(Human record);//当human对象所有属性都在可以用,就是一个普通的对象(少用)

    int insertSelective(Human record);//尽量用它!!!(优先使用)

    List<Human> selectByExample(HumanExample example);//动态查询

    Human selectByPrimaryKey(Integer id);//按主键id

    //动态批量修改
    int updateByExampleSelective(@Param("record") Human record, @Param("example") HumanExample example);

    //动态批量修改(不用)
    int updateByExample(@Param("record") Human record, @Param("example") HumanExample example);

    //按主键id修改一个对象,一条数据
    int updateByPrimaryKeySelective(Human record);

    //千万不用!!!
    int updateByPrimaryKey(Human record);
}