package com.yyh.dao;

import com.yyh.bean.Person;
import com.yyh.bean.PersonDto;

import java.util.List;
import java.util.Map;

public interface PersonDao {
    //全查
    List<Person> selectAll();

    //根据性别查询
    List<Person> selectPersonBySex(int sex);

    //查询总条数
    long selectCount();

    //查询总条数+多个参数第一种方式 2个参数都是person类中的属性,
    //所以直接可以把person当做参数
    long selectCountByParam01(Person person);

    //查性别和生日 ,当查出的数据不确定是唯一的一条的时候,返回值一定要用list
    //当多表联查的时候,请求的参数一定要为map或者是自己写的实体类
    //应用场景,多表联查的多参数查询
    List<Person> selectCountByParam02(Map map);

    //查询分值最高
    List<Person> selectCountByZi();

    //男女生平均分 分组查询
    List<PersonDto> selectAvgScore();

    //男女生平均值大于200的???
    List<PersonDto> selectAvgScoreParam(int score);

    //男女生平均值大于200的???map做返回值
    List<Map> selectAvgScoreParam02(int score);

    //查询孙姓 第一种方式(不建议)
    List<Person> selectPersonByLike(String name);

    //查询孙姓 第二种方式
    List<Person> selectPersonByLike02(String name);

    //查询孙姓 第三种方式
    List<Person> selectPersonByLike03(String name);

    //增加的方法
    int insertPerson(Person person);

    //删除的方法
    int deletePersonBuId(Integer id);//注意:之后讲解动态sql,那么我们dao层接口只有一个基础类型,
    //int,String不好的,不方便执行动态sql,对以后扩展不便
    //以后自己写代码参数一定是一个实体类或者Map,或者Dto

    //动态sql
    List<Person> dongTaiSelect(Person person);//动态sql如果参数不是实体类,集合是个空参,那么没有任何意义
    //长成返回值是list<实体类>参数也是同样的实体类,那么这是典型的动态sql语句

    //动态修改
    int dongTaiUpdate();

    //批量删除
    void piLiangDel(Map map);

    //一对多 方法写在一方
    //把这个改为动态,按id,name都可以查询!!!
    //两张表 城市表  区表  写出一对多的动态sql
    List<Person> selectOrdesByPersonId(Integer id);

    //1 VS 多 VS 多 学校--班级--学生 省--市--县区  适用于下拉框
    List<Person> selectDetailByPersonId(Integer id);
    //List<Person>   selectPersonAndOrders(Person person);

    //三表连查 适用于数据表格 参数就是map!!!双map***返回值和参数均为map,俗称万能查
    //适用于动态sql的查询
    List<Map> selectDetailByParam(Map map);

    //多对多查询
    List<Person> selectRoleByPname(String name);

}
