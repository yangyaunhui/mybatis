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

}
