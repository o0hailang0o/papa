package com.demo.batch;

import io.webfolder.cdp.Launcher;
import io.webfolder.cdp.session.Session;
import io.webfolder.cdp.session.SessionFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class NuoChaoHistory {


    public static void getData()throws Exception {
        Launcher launcher = new Launcher();
        SessionFactory factory = launcher.launch();
        Session session = factory.create();
        session.navigate("https://info.sporttery.cn/football/history/history_data.php?mid=133&s_id=6865").waitDocumentReady()
                .enableConsoleLog()
                .enableDetailLog()
                .enableNetworkLog();
        session.click(".u_btn_left").wait(1000)
        .click(".box table [id='1'] a").wait(1000);
        String content = session.getContent();
        Document doc = Jsoup.parse(content);
        Elements matchInfo= doc.select("#match_info table");
        Elements trs = matchInfo.select("tr");
        for(Element tr : trs){
            String date = tr.select("td").eq(0).html();
            System.out.println(date);
        }
    }

    public static void main(String[] args)throws Exception{
        NuoChaoHistory.getData();
    }
}
