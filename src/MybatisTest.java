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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void test01(){
       List<Person> personList = sqlSession.selectList("com.yyh.dao.PersonDao.selectAll");
        for (Person person : personList) {
            System.out.println("person = " + person);
        }
        sqlSession.close();
    }

    //单查
    @Test
    public void test02(){
        List<Person> personList = sqlSession.selectList("com.yyh.dao.PersonDao.selectPersonBySex",2);
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
        long o = sqlSession.selectOne("com.yyh.dao.PersonDao.selectCountByParam01",person);
        System.out.println("o = " + o);
        sqlSession.close();
    }

    //带参查询 第二种方式:map传参---多见于多表查询
    @Test
    public void test05() throws ParseException {
        String date="2020-10-14";
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Date birthday = sf.parse(date);
        Map map = new HashMap();
        map.put("gender",2);//key一定要和#{gender}一致
        map.put("birthday",birthday);//key一定要和#{birthday}一致
        List<Person> lists = sqlSession.selectList("com.yyh.dao.PersonDao.selectCountByParam02", map);
        for (Person list : lists) {
            System.out.println("list = " + list);
        }
        sqlSession.close();
    }

    //子查询
    @Test
    public void test06(){
        List<Person> lists = sqlSession.selectList("com.yyh.dao.PersonDao.selectCountByZi");
        for (Person list : lists) {
            System.out.println("list = " + list);
        }
        sqlSession.close();
    }

    //分组查询
    @Test
    public void test07(){
        List<PersonDto> personDtos = sqlSession.selectList("com.yyh.dao.PersonDao.selectAvgScore");
        for (PersonDto personDto : personDtos) {
            System.out.println("personDto = " + personDto);
        }
        sqlSession.close();
    }

    //分组查询+参数
    @Test
    public void test08(){
        List<PersonDto> personDtos = sqlSession.selectList("com.yyh.dao.PersonDao.selectAvgScoreParam",200);
        for (PersonDto personDto : personDtos) {
            System.out.println("personDto = " + personDto);
        }
        sqlSession.close();
    }

    //分组查询+map
    @Test
    public void test09(){
        List<Map> maps = sqlSession.selectList("com.yyh.dao.PersonDao.selectAvgScoreParam02", 200);
        for (Map map : maps) {
            System.out.println("map = " + map);
        }
        sqlSession.close();
    }

    //查询孙姓 不要用拼接的方式写$
    @Test
    public void test10(){
        Map map = new HashMap();
        map.put("name","孙");
        List<Person> personList = sqlSession.selectList("com.yyh.dao.PersonDao.selectPersonByLike",map);

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
    public void test11(){
        List<Person> personList = sqlSession.selectList("com.yyh.dao.PersonDao.selectPersonByLike02", "孙");
        for (Person person : personList) {
            System.out.println("person = " + person);
        }
        sqlSession.close();
    }

    //查询孙姓 可以用这个
    @Test
    public void test12(){
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
            person.setGender(2);
            person.setBirthday(new Date());
            person.setAddress("杭州");
            person.setScore(666);
        int insert = sqlSession.insert("com.yyh.dao.PersonDao.insertPerson", person);
        System.out.println("insert = " + insert);
        sqlSession.commit();
        sqlSession.close();
    }

}