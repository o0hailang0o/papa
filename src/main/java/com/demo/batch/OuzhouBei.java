package com.demo.batch;

import io.webfolder.cdp.Launcher;
import io.webfolder.cdp.session.Session;
import io.webfolder.cdp.session.SessionFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class OuzhouBei {

    public static Session createSession(){
        Launcher launcher = new Launcher();
        SessionFactory factory = launcher.launch();
        Session session = factory.create();
        return session;
    }


    public static void getData()throws Exception{
        Session session = createSession();
        session.navigate("https://www.sporttery.cn/jc/jsq/zqhhgg");
        Thread.sleep(5*1000);
        String content = session.getContent();
        Document doc = Jsoup.parse(content);
        Elements rows= doc.select(".leftPan .portlet");
        for(Element row : rows){
            Elements mCodeCls =  row.select("table .mCodeCls");
            Elements dateCls = row.select("table .dateCls");
            Elements vsCls = row.select("table .vsCls");
          //  Elements
            System.out.println(mCodeCls.html());
        }

    }

    public static void main(String[] args)throws Exception{
        OuzhouBei.getData();
    }
}
