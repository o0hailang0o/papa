package com.demo.batch;

import io.webfolder.cdp.Launcher;
import io.webfolder.cdp.session.Session;
import io.webfolder.cdp.session.SessionFactory;

public class OuzhouBei {

    public static Session createSession(){
        Launcher launcher = new Launcher();
        SessionFactory factory = launcher.launch();
        Session session = factory.create();
        return session;
    }


    public static void getData()throws Exception{
        Session session = createSession();
        session.navigate("https://www.sporttery.cn/jc/jsq/zqhhgg/");
        Thread.sleep(2*1000);
        String content = session.getContent();
        Thread.sleep(10*1000);
        System.out.println(content);
    }

    public static void main(String[] args)throws Exception{
        OuzhouBei.getData();
    }
}
