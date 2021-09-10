import com.yyh.bean.Human;
import com.yyh.bean.HumanExample;
import com.yyh.bean.Person;
import com.yyh.bean.PersonDto;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MybatisTest {
    private SqlSession sqlSession;//讲一下mybatis的执行流程

    @Before//在@Test注解前,执行的方法,提取重复的代码
    public void before() throws Exception {
        //加载并读取xml
        String path = "SqlMapConfig.xml";
        //import org.apache.ibatis.io.Resources;
        InputStream is = Resources.getResourceAsStream(path);
        //sql连接的工厂类
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(is);
        sqlSession = sqlSessionFactory.openSession();
        System.out.println("sqlSession = " + sqlSession);//sqlSession = org.apache.ibatis.session.defaults.DefaultSqlSession@5ce81285
    }

    //全查
    @Test
    public void test01() {
        List<Person> personList = sqlSession.selectList("com.yyh.dao.PersonDao.selectAll");
        for (Person person : personList) {
            System.out.println("person = " + person);
        }
        sqlSession.close();
    }

    //单查
    @Test
    public void test02() {
        List<Person> personList = sqlSession.selectList("com.yyh.dao.PersonDao.selectPersonBySex", 2);
        for (Person person : personList) {
            System.out.println("person = " + person);
        }
        sqlSession.close();
    }

    //查询总条数
    @Test
    public void test03() {
        long o = sqlSession.selectOne("com.yyh.dao.PersonDao.selectCount");
        System.out.println("o = " + o);
        sqlSession.close();
    }

    //带参查询 第一种方式:实体类传参---多见于单表查询
    @Test
    public void test04() {
        Person person = new Person();
        person.setScore(100);
        person.setGender(2);
        long o = sqlSession.selectOne("com.yyh.dao.PersonDao.selectCountByParam01", person);
        System.out.println("o = " + o);
        sqlSession.close();
    }

    //带参查询 第二种方式:map传参---多见于多表查询
    @Test
    public void test05() throws ParseException {
        String date = "2020-10-14";
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Date birthday = sf.parse(date);
        Map map = new HashMap();
        map.put("gender", 2);//key一定要和#{gender}一致
        map.put("birthday", birthday);//key一定要和#{birthday}一致
        List<Person> lists = sqlSession.selectList("com.yyh.dao.PersonDao.selectCountByParam02", map);
        for (Person list : lists) {
            System.out.println("list = " + list);
        }
        sqlSession.close();
    }

    //子查询
    @Test
    public void test06() {
        List<Person> lists = sqlSession.selectList("com.yyh.dao.PersonDao.selectCountByZi");
        for (Person list : lists) {
            System.out.println("list = " + list);
        }
        sqlSession.close();
    }

    //分组查询
    @Test
    public void test07() {
        List<PersonDto> personDtos = sqlSession.selectList("com.yyh.dao.PersonDao.selectAvgScore");
        for (PersonDto personDto : personDtos) {
            System.out.println("personDto = " + personDto);
        }
        sqlSession.close();
    }

    //分组查询+参数
    @Test
    public void test08() {
        List<PersonDto> personDtos = sqlSession.selectList("com.yyh.dao.PersonDao.selectAvgScoreParam", 200);
        for (PersonDto personDto : personDtos) {
            System.out.println("personDto = " + personDto);
        }
        sqlSession.close();
    }

    //分组查询+map
    @Test
    public void test09() {
        List<Map> maps = sqlSession.selectList("com.yyh.dao.PersonDao.selectAvgScoreParam02", 200);
        for (Map map : maps) {
            System.out.println("map = " + map);
        }
        sqlSession.close();
    }

    //查询孙姓 不要用拼接的方式写$
    @Test
    public void test10() {
        Map map = new HashMap();
        map.put("name", "孙");
        List<Person> personList = sqlSession.selectList("com.yyh.dao.PersonDao.selectPersonByLike", map);

        //There is no getter for property named 'name'
        //因为$是拼接的,没有getter这个概念 #相当于问号,有getter概念
        //List<Person> personList = sqlSession.selectList("com.yyh.dao.PersonDao.selectPersonByLike","孙");
        for (Person person : personList) {
            System.out.println("person = " + person);
        }
        sqlSession.close();
    }

    //查询孙姓 可以用这个
    @Test
    public void test11() {
        List<Person> personList = sqlSession.selectList("com.yyh.dao.PersonDao.selectPersonByLike02", "孙");
        for (Person person : personList) {
            System.out.println("person = " + person);
        }
        sqlSession.close();
    }

    //查询孙姓 可以用这个
    @Test
    public void test12() {
        List<Person> personList = sqlSession.selectList("com.yyh.dao.PersonDao.selectPersonByLike03", "孙");
        for (Person person : personList) {
            System.out.println("person = " + person);
        }
        sqlSession.close();
    }

    //以上是单表的所有查询!!!看好这些例子,一会模仿去公司写
    //增加 insert into ...
    @Test
    public void test13() {
        Person person = new Person();
        person.setName("木易");
        person.setGender(1);
        person.setBirthday(new Date());
        person.setAddress("杭州");
        person.setScore(666);
        int insert = sqlSession.insert("com.yyh.dao.PersonDao.insertPerson", person);
        System.out.println("insert = " + insert);
        sqlSession.commit();
        sqlSession.close();
    }

    //删除
    @Test
    public void test14() {
        int i = sqlSession.delete("com.yyh.dao.PersonDao.deletePersonBuId", 17);
        System.out.println("i = " + i);
        sqlSession.commit();
        sqlSession.close();
    }

    //动态sql 重点,难点
    //动态sql就是让达到1条xml中的语句可以实现N种查询
    //那么要实现多种查询 就有一个硬性的条件!!!参数要多---1.放弃单个的属性(int String) 改用实体类 2.参数改用map
    //今天所学的推翻昨天所学的 那么就需要总结昨天所学的
    //第一类:特征1:返回值---正常表的结果集 对应的是person实体类
    //特征2:都是 select * from person开头的
    //1.1 select * from person  if如果 where后面没参数那么就是全查
    //1.2 select * from person where gender = 2 if如果 where后面参数是gender,那么就是单查gender
    //1.3 select * from person gender=#{gender} and birthday>=#{birthday}
    //1.4 select * from person where name like "%"#{name}"%"
    //1-4可以合N为1,只需要把where后面的参数做个if判断

    //第二类:特征1:返回值---一个数,单行单列,非person实体类,是一个数据类型
    //特征2:都是 select count(*) from person 开头的
    //2.1 select count(*) from person
    //2.2 select count(*) from person where gender = 2 and score > 100

    //综上所述,以上的sql可以进行动态判断,形成一个sql!!!这就叫动态sql...


    //动态查询
    @Test
    public void test15() {
        Person person = new Person();
        //null就是全查
        //person.setId(16); //select * from person p WHERE p.id=?
        person.setScore(200);
        person.setGender(2);
        List<Person> personList = sqlSession.selectList("com.yyh.dao.PersonDao.dongTaiSelect", person);
        for (Person person1 : personList) {
            System.out.println("person1 = " + person1);
        }
        sqlSession.commit();
        sqlSession.close();
    }

    //动态修改 有选择性修改多个字段,可以修改女生分数,日期...
    @Test
    public void test16() {
        Person person = new Person();
        person.setId(16);
        person.setAddress("四川");
        person.setBirthday(new Date());
        int update = sqlSession.update("com.yyh.dao.PersonDao.dongTaiUpdate", person);
        System.out.println("update = " + update);
        sqlSession.commit();
        sqlSession.close();
    }

    //批量删除 delete xxx in (1,2,3,4)
    //构造ids
    @Test
    public void test17() {
        List<Integer> idList = new ArrayList<>();
        idList.add(1);
        idList.add(2);
        idList.add(3);
        Map map = new HashMap();
        map.put("ids", idList);
        int delete = sqlSession.delete("com.yyh.dao.PersonDao.piLiangDel", map);
        System.out.println("delete = " + delete);
        sqlSession.commit();
        sqlSession.close();
    }

    //这是重点,逆向生成
    //没有写一行代码,但是动态的查询总条数已经完成了
    @Test
    public void test18() {
        //select count(*) from human
        //select count(*) from human where gender = 2 and address="西京"
        HumanExample example = new HumanExample();
        HumanExample.Criteria criteria = example.createCriteria();//用例子类实现查询规则、标准
        //criteria.andGenderEqualTo(2);//select count(*) from human
        //criteria.andAddressEqualTo("西京"); //select count(*) from human where gender = 2 and address="%西京%"

        //criteria.andAddressLike("%"+"西京"+"%");

        //select * from human where addrsss="北京" or score="555"
        //因为criteria查询标准里没有or,有in

        //example.or().andAddressEqualTo("北京");//or不需要criteria类
        //example.or().andScoreEqualTo(888.0);//select count(*) from human where (address = ?) or ( score = ?)

        //select * from human where id = 3 or id = 4 or id = 5
        //example.or().andIdBetween(3,5);
        //select * from human where id in (1,4,5)
        List<Integer> ids = new ArrayList<>();
            ids.add(1);
            ids.add(4);
            ids.add(5);
         criteria.andIdIn(ids);

        //当example的criteria不用赋值的时候,则是Preparing: select count(*) from human
        long o = sqlSession.selectOne("com.yyh.dao.HumanDAO.countByExample", example);
        System.out.println("o = " + o);
        sqlSession.close();

    }

    //单表的所有
//查询:
//select * from human ******全查
//select * from human where gender = 2
//select * from human where gender = 1
//select * from human where id = 1
//select * from human where score > 80
//select * from human where score < 80
//select * from human where score > 80 and gender = 1
//select * from human where score > 80 and gender = 1 and address like "%郑州%"
//以上的所有sql语句写成一个
//List<Human> selectByExample(HumanExample example);
    @Test
    public void test19(){
        //select * from human ******全查***不带参数
        HumanExample example = new HumanExample();
        List<Human> humans = sqlSession.selectList("com.yyh.dao.HumanDAO.selectByExample", example);
        for (Human human : humans) {
            System.out.println("human = " + human);
        }
        sqlSession.close();
    }


    @Test
    public void test19_01(){
        //select * from human where gender = 1
        HumanExample example = new HumanExample();
        HumanExample.Criteria criteria = example.createCriteria();
        criteria.andGenderEqualTo(1);
        List<Human> humans = sqlSession.selectList("com.yyh.dao.HumanDAO.selectByExample", example);
        for (Human human : humans) {
            System.out.println("human = " + human);
        }
        sqlSession.close();
    }

    @Test
    public void test19_02(){
        //select * from human where id = 1
        HumanExample example = new HumanExample();
        HumanExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(1);
        List<Human> humans = sqlSession.selectList("com.yyh.dao.HumanDAO.selectByExample", example);
        for (Human human : humans) {
            System.out.println("human = " + human);
        }
        sqlSession.close();
    }

    @Test
    public void test19_03(){
        //select * from human where score > 555
        HumanExample example = new HumanExample();
        HumanExample.Criteria criteria = example.createCriteria();
        criteria.andScoreGreaterThan(555.0);
        List<Human> humans = sqlSession.selectList("com.yyh.dao.HumanDAO.selectByExample", example);
        for (Human human : humans) {
            System.out.println("human = " + human);
        }
        sqlSession.close();
    }

    @Test
    public void test19_04(){
        //select * from human where score < 555
        HumanExample example = new HumanExample();
        HumanExample.Criteria criteria = example.createCriteria();
        criteria.andScoreLessThan(555.0);
        List<Human> humans = sqlSession.selectList("com.yyh.dao.HumanDAO.selectByExample", example);
        for (Human human : humans) {
            System.out.println("human = " + human);
        }
        sqlSession.close();
    }

    @Test
    public void test19_05(){
        //select * from human where score > 555 and gender = 1
        HumanExample example = new HumanExample();
        HumanExample.Criteria criteria = example.createCriteria();
        criteria.andScoreGreaterThan(555.0);
        criteria.andGenderEqualTo(1);
        List<Human> humans = sqlSession.selectList("com.yyh.dao.HumanDAO.selectByExample", example);
        for (Human human : humans) {
            System.out.println("human = " + human);
        }
        sqlSession.close();
    }

    @Test
    public void test19_06(){
        //select * from human where score > 555 and gender = 1 and address like "%郑州%"
        HumanExample example = new HumanExample();
        HumanExample.Criteria criteria = example.createCriteria();
        criteria.andScoreGreaterThan(555.0);
        criteria.andGenderEqualTo(1);
        criteria.andAddressLike("%"+"西京"+"%");
        List<Human> humans = sqlSession.selectList("com.yyh.dao.HumanDAO.selectByExample", example);
        for (Human human : humans) {
            System.out.println("human = " + human);
        }
        sqlSession.close();
    }

}