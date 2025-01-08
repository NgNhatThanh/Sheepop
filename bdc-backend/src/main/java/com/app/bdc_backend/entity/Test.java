package com.app.bdc_backend.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Test {

    @Id
    public int id;

    public String name;

    public int age;

    public Test(int id, String name, int age){
        this.name = name;
        this.age = age;
        this.id = id;
    }

}
