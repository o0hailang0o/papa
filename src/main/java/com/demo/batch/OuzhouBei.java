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
        ArrayList<String> command = new ArrayList<String>();
        //不显示google 浏览器
      //  command.add("--headless");
        Launcher launcher = new Launcher();
        SessionFactory factory = launcher.launch("C://Users//Administrator//AppData//Local//Google//Chrome//Application//chrome.exe",command);
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
            Elements trs =  row.select("table tr");
            for(Element td : trs){

            }
        }

        System.out.println(html);
    }

    public static void main(String[] args)throws Exception{
        OuzhouBei.getData();
    }
}
