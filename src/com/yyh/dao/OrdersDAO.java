package com.yyh.dao;

import com.yyh.bean.Orders;
import com.yyh.bean.OrdersExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrdersDAO {
    long countByExample(OrdersExample example);

    int deleteByExample(OrdersExample example);

    int deleteByPrimaryKey(Integer ordersId);

    int insert(Orders record);

    int insertSelective(Orders record);

    List<Orders> selectByExample(OrdersExample example);

    Orders selectByPrimaryKey(Integer ordersId);

    int updateByExampleSelective(@Param("record") Orders record, @Param("example") OrdersExample example);

    int updateByExample(@Param("record") Orders record, @Param("example") OrdersExample example);

    int updateByPrimaryKeySelective(Orders record);

    int updateByPrimaryKey(Orders record);

    //多对一 根据订单查询人
    Orders selectPersonByOrderId(Integer ordersId);

}