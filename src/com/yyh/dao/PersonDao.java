package com.yyh.dao;

import com.yyh.bean.Person;

import java.util.List;

public interface PersonDao {

    //全查
    List<Person> selectAll();
}
