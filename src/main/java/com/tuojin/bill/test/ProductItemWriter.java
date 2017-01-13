package com.tuojin.bill.test;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Administrator on 2017/1/13.
 */
public class ProductItemWriter implements ItemWriter<Product> {
    public static final String GET_PRODUCT="select * from product where id=?";
    public static final String INSET_PRODUCT="insert into product (id,name,description,quantity) values (?,?,?,?)";
    public static final String UPDATE_PRODUCT="update product set name=?, description=?, quantity=? where id=?";
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    public void write(List<? extends Product> list) throws Exception {
        for (Product product :
                list) {
         jdbcTemplate.query(GET_PRODUCT, new Object[]{product.getId()}, new RowMapper<Product>() {
             public Product mapRow(ResultSet resultSet, int i) throws SQLException {
                 Product p=new Product();
                 p.setId(resultSet.getInt(1));
                 p.setName(resultSet.getString(2));
                 p.setDescription(resultSet.getString(3));
                 p.setQuantity(resultSet.getInt(4));
                 return p;
             }
         });
            if (list.size() > 0) {
                jdbcTemplate.update(UPDATE_PRODUCT, product.getName(), product.getDescription(), product.getQuantity());
            }else{
                jdbcTemplate.update(INSET_PRODUCT,product.getId(),product.getName(),product.getDescription(),product.getQuantity());
            }
        }
    }
}
