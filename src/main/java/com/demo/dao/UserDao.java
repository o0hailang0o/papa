package com.demo.dao;

import com.demo.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by 33852 on 2017/4/7.
 */
@Mapper
public interface UserDao {

    List<User> listUser(User user);


    void insertUser(User user);

    User selectUserById(Long id);

    void updateUser(User user);

    void deleteUserById(Long id);
}
