package com.demo.service;

import com.demo.model.User;

import java.util.List;

/**
 * Created by 33852 on 2017/4/7.
 */
public interface UserService {

    List<User> listUser(User user);

    User insertUser(User user);

    User findUserByid(Long id);

    void updateUser(User user);

    void deleteUserById(Long id);
}
