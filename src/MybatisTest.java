import com.yyh.bean.*;
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
        //SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(is);
        sqlSession=sqlSessionFactory.openSession();
        //sqlSession = sqlSessionFactory.openSession();
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
    public void test19() {
        //select * from human ******全查***不带参数
        HumanExample example = new HumanExample();
        List<Human> humans = sqlSession.selectList("com.yyh.dao.HumanDAO.selectByExample", example);
        for (Human human : humans) {
            System.out.println("human = " + human);
        }
        sqlSession.close();
    }


    @Test
    public void test19_01() {
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
    public void test19_02() {
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
    public void test19_03() {
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
    public void test19_04() {
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
    public void test19_05() {
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
    public void test19_06() {
        //select * from human where score > 555 and gender = 1 and address like "%郑州%"
        HumanExample example = new HumanExample();
        HumanExample.Criteria criteria = example.createCriteria();
        criteria.andScoreGreaterThan(555.0);
        criteria.andGenderEqualTo(1);
        criteria.andAddressLike("%" + "西京" + "%");
        List<Human> humans = sqlSession.selectList("com.yyh.dao.HumanDAO.selectByExample", example);
        for (Human human : humans) {
            System.out.println("human = " + human);
        }
        sqlSession.close();
    }

    //工具的增加
    @Test
    public void test20() {
        Human human = new Human();
        human.setGender(1);
        human.setName("秦时明月");
        human.setAddress("杭州");

        //Preparing: insert into human
        //(id, `name`, gender, birthday, address, score )
        //values (?, ?, ?, ?, ?, ? ) 当数据库中有的字段有默认值,
        //或者不能为null的时候,该增加的方法就是出错,所有用长的insertSelective
        //int insert = sqlSession.insert("com.yyh.dao.HumanDAO.insertSelective", human);
        int insert1 = sqlSession.insert("com.yyh.dao.HumanDAO.insert", human);
        System.out.println("insert1 = " + insert1);
        sqlSession.commit();
        sqlSession.close();

    }

    //工具的删除
    @Test
    public void test21() {
        int delete = sqlSession.delete("com.yyh.dao.HumanDAO.deleteByPrimaryKey", 9);
        //delete from human where id = ?
        System.out.println("delete = " + delete);
        sqlSession.commit();
        sqlSession.close();
    }

    //条件删除
    //1.删除女生
    //2.删除分数小于500的
    //3.删除名字带有云的
    //4.删除女生并且分数小于300的
    //5.删除女生并且分数小于300的名字中带云的
    @Test
    public void test22() {
        HumanExample humanExample = new HumanExample();
        HumanExample.Criteria criteria = humanExample.createCriteria();
        //criteria.andGenderEqualTo(1);
        //criteria.andScoreLessThan(20.0);
        //criteria.andNameLike("%"+"灰"+"%");

        int delete = sqlSession.delete("com.yyh.dao.HumanDAO.deleteByExample", humanExample);
        System.out.println("delete = " + delete);
        sqlSession.commit();
        sqlSession.close();
    }

    //按主键id修改一个对象,一条数据
    @Test
    public void test23() {
        Human human = new Human();
        human.setId(5);
        human.setName("灰太狼");
        int update = sqlSession.update("com.yyh.dao.HumanDAO.updateByPrimaryKeySelective", human);
        System.out.println("update = " + update);
        sqlSession.commit();
        sqlSession.close();

    }

    //批量的动态修改---当分数超过100全部改为100
    //mybatis测试不了
    @Test
    public void test24() {
        Human human = new Human();
        human.setScore(666.0);
        HumanExample humanExample = new HumanExample();
        HumanExample.Criteria criteria = humanExample.createCriteria();
        criteria.andScoreGreaterThan(666.0);

        //sqlSession.update("com.yyh.dao.HumanDAO.updateByExampleSelective", humanExample);
    }

    //按主键id查询
    @Test
    public void test25() {
        Human o = sqlSession.selectOne("com.yyh.dao.HumanDAO.selectByPrimaryKey", 5);
        System.out.println("o = " + o);
        sqlSession.commit();
        sqlSession.close();
    }

    //动态查询
    //1.查询分数大于555的人
    //2.查询分数大于555的人并且生日大于2020-11-04
    @Test
    public void test26() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        HumanExample example = new HumanExample();
        HumanExample.Criteria criteria = example.createCriteria();
        criteria.andScoreGreaterThan(555.0);
        simpleDateFormat.parse("2021-11-04");
        criteria.andGenderEqualTo(1);
        List<Human> humans = sqlSession.selectList("com.yyh.dao.HumanDAO.selectByExample", example);
        for (Human human : humans) {
            System.out.println("human = " + human);
        }
        sqlSession.commit();
        sqlSession.close();
    }

    //1对多
    @Test
    public void test27(){
        List<Person> personList = sqlSession.selectList("com.yyh.dao.PersonDao.selectOrdesByPersonId", 1);
        for (Person person : personList) {
            System.out.println("person = " + person);
        }
        sqlSession.commit();
        sqlSession.close();
    }

    //1对多对多

    @Test
    public void test28(){
        List<Person> personList = sqlSession.selectList("com.yyh.dao.PersonDao.selectDetailByPersonId", 7);
        System.out.println("personList = " + personList.size());
        for (Person person : personList) {
            System.out.println("person = " + person);
            //person = Person{id=7, name='木易', gender=1, birthday=Wed Sep 08 00:00:00 CST 2021,
            //address='杭州', score=666, ordersList=[
            //Orders {ordersId=6, personId=7, totalPrice=10000.0, addr='远方',
            //orderDetails=[OrderDetail [Hash = -419046120, detailId=1, orderId=6,
            //itemName=iphone手机, price=5.0, count=10, serialVersionUID=1],
            //OrderDetail [Hash = -1816967641, detailId=2, orderId=6,
            //itemName=小米手机机, price=10.0, count=5, serialVersionUID=1]]},
            //Orders{ordersId=7, personId=7, totalPrice=1222222.0, addr='院方',
            //orderDetails=[OrderDetail [Hash = 340570813, detailId=3, orderId=7,
            //itemName=汤臣一品, price=1000.0, count=2, serialVersionUID=1],
            //OrderDetail [Hash = -1561780567, detailId=4, orderId=7, itemName=宝马x23,
            //price=500.0, count=3, serialVersionUID=1]]}]}
        }
        sqlSession.close();
    }

    //一表多查
    @Test
    public void test29(){
        Map map = new HashMap();
        map.put("id",7);
        List<Map> personList = sqlSession.selectList("com.yyh.dao.PersonDao.selectDetailByParam", map);
        System.out.println("personList = " + personList.size());
        for (Map map1 : personList) {
            System.out.println("map1 = " + map1);
            //map1 = {birthday=2021-09-08, address=杭州, orders_id=6, gender=1, total_price=10000.0, count=10, item_name=iphone手机, detail_id=1, score=666, price=5.0, name=木易, id=7, addr=远方, order_id=6, person_id=7}
            //map1 = {birthday=2021-09-08, address=杭州, orders_id=6, gender=1, total_price=10000.0, count=5, item_name=小米手机机, detail_id=2, score=666, price=10.0, name=木易, id=7, addr=远方, order_id=6, person_id=7}
            //map1 = {birthday=2021-09-08, address=杭州, orders_id=7, gender=1, total_price=1222222.0, count=2, item_name=汤臣一品, detail_id=3, score=666, price=1000.0, name=木易, id=7, addr=院方, order_id=7, person_id=7}
            //map1 = {birthday=2021-09-08, address=杭州, orders_id=7, gender=1, total_price=1222222.0, count=3, item_name=宝马x23, detail_id=4, score=666, price=500.0, name=木易, id=7, addr=院方, order_id=7, person_id=7}
        }
        sqlSession.close();
    }

    //多对一反向 注意:实体类中多写1方的实体类
    @Test
    public void test30(){
        Object o = sqlSession.selectOne("com.yyh.dao.OrdersDAO.selectPersonByOrderId", 3);
        System.out.println("o = " + o);
        sqlSession.close();
    }
    //一对一*******可以看作是简单的多对一
    //多对多*******可以看作是带中间表的一对多,它是由两个一对多组成
    @Test
    public void test31(){
        List<Person> personList = sqlSession.selectList("com.yyh.dao.PersonDao.selectRoleByPname", "木易1");
        for (Person person : personList) {
            System.out.println("person = " + person);
        }
        sqlSession.close();
    }



}