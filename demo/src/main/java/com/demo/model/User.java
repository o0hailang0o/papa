package com.demo.model;

import java.io.Serializable;

/**
 * Created by 33852 on 2017/4/7.
 */
public class User implements Serializable{

    private Long id;
    private String name;
    private Integer age;
    private Integer sex;

    public User(){};

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public void test(){
        String s=null;
        int i= s.length();
    }
}
