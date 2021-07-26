package com.demo.batch;

import com.demo.dao.NuoWeiInfoMapper;
import com.demo.model.NuoWeiInfo;
import io.webfolder.cdp.Launcher;
import io.webfolder.cdp.session.Session;
import io.webfolder.cdp.session.SessionFactory;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Component
public class NuoChaoHistory {

    @Autowired
    private NuoWeiInfoMapper nuoWeiInfoMapper;

    public  void getData()throws Exception {
        Launcher launcher = new Launcher();
        SessionFactory factory = launcher.launch();
        Session session = factory.create();
        session.navigate("https://info.sporttery.cn/football/history/history_data.php?mid=133&s_id=6865").waitDocumentReady()
                .enableConsoleLog()
                .enableDetailLog()
                .enableNetworkLog();
        for(int i=1;i<=23;i++){
            session.click(".u_btn_left").wait(1000)
                    .click(".box table [id='"+i+"'] a").wait(1000);
            String content = session.getContent();
            Document doc = Jsoup.parse(content);
            Elements matchInfo= doc.select("#match_info table");
            Elements trs = matchInfo.select("tr");
            int index = 0;
            for(Element tr : trs){
                if(index++==0){
                    continue;
                }
                NuoWeiInfo nuoWeiInfo = new NuoWeiInfo();
                String date = tr.select("td").eq(0).html();
                Date matchTime = getMatchTime(date);
                nuoWeiInfo.setMatchTime(matchTime);

                String rounds = tr.select("td").eq(1).html();
                nuoWeiInfo.setRounds(rounds);

                String host = tr.select("td").eq(2).select("a").html();
                nuoWeiInfo.setHost(host);

                String half = tr.select("td").eq(3).select("a").html();
                if(StringUtils.isEmpty(half)){
                    continue;
                }
                String halfHostScoreStr = half.split(":")[0];
                String halfGuestScoreStr = half.split(":")[1];
                nuoWeiInfo.setHalfHostScore(Integer.parseInt(halfHostScoreStr));
                nuoWeiInfo.setHalfGuestScore(Integer.parseInt(halfGuestScoreStr));

                String all = tr.select("td").eq(4).select("a").html();
                String allHostScoreStr = all.split(":")[0];
                String allGuestScoreStr = all.split(":")[1];
                nuoWeiInfo.setHostScore(Integer.parseInt(allHostScoreStr));
                nuoWeiInfo.setGuestScore(Integer.parseInt(allGuestScoreStr));

                String guest = tr.select("td").eq(5).select("a").html();
                nuoWeiInfo.setGuest(guest);

                String winPrice = tr.select("td").eq(6).select("span").eq(0).html();
                nuoWeiInfo.setWinPrice(new BigDecimal(winPrice));
                String evenPrice = tr.select("td").eq(6).select("span").eq(1).html();
                nuoWeiInfo.setEvenPrice(new BigDecimal(evenPrice));
                String lostPrice = tr.select("td").eq(6).select("span").eq(2).html();
                nuoWeiInfo.setLostPrice(new BigDecimal(lostPrice));
                System.out.println(nuoWeiInfo);
            }
        }

    }

    private Date getMatchTime(String date)throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date = date.replace("&nbsp;","");
        return sdf.parse(date);
    }

    public static void main(String[] args)throws Exception{
        NuoChaoHistory  nuoChaoHistory = new NuoChaoHistory();
        nuoChaoHistory.getData();
    }
}
