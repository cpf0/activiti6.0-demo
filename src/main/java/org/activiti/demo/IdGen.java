package org.activiti.demo;

import org.activiti.engine.impl.cfg.IdGenerator;

import java.util.UUID;

public class IdGen implements IdGenerator {

    public  static  String uuid(){

        return UUID.randomUUID().toString().replaceAll("-","");
    }
    @Override
    public String getNextId() {
        return IdGen.uuid();
    }
}
