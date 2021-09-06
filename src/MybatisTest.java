import com.yyh.bean.Person;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;


public class MybatisTest {
    private SqlSession sqlSession;
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

    @Test
    public void test01(){
        List<Person> personList = sqlSession.selectList("com.yyh.dao.PersonDao.selectAll");
        for (Person person : personList) {
            System.out.println("person = " + person);
        }
        sqlSession.close();

    }
}