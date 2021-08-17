package com.demo.batch;

import com.demo.dao.NuoWeiInfoMapper;
import com.demo.dao.RuiDianInfoMapper;
import com.demo.model.NuoWeiInfo;
import com.demo.model.RuiDianInfo;
import io.webfolder.cdp.Launcher;
import io.webfolder.cdp.session.Session;
import io.webfolder.cdp.session.SessionFactory;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

@Component
public class RuiChaoHistory implements ApplicationRunner {

    String season = "瑞典2020赛季";

    HashMap<Integer, String> map =
            new HashMap<>() {
                {
                    //put(2021, "sp_lid=58&sp_mid=1008147");
                    put(2020, "mid=132&s_id=6911");
                    put(2019, "mid=132&s_id=5559");
                    put(2018, "mid=132&s_id=4294");
                    put(2017, "mid=132&s_id=3197");
                    put(2016, "mid=132&s_id=2042");
                    put(2015, "mid=132&s_id=172");
                    put(2014, "mid=132&s_id=124");
                    put(2013, "mid=132&s_id=100");
                    put(2012, "mid=132&s_id=70");
                    put(2011, "mid=132&s_id=48");
                    put(2010, "mid=132&s_id=26");
                    put(2009, "mid=132&s_id=5");
                }
            };

    @Autowired
    private RuiDianInfoMapper ruiDianInfoMapper;


    public  void getData(Integer year)throws Exception {
        season =  "瑞典超级联赛"+year+"赛季";
        Launcher launcher = new Launcher();
        SessionFactory factory = launcher.launch();
        Session session = factory.create();
        String id = map.get(year);
        session.navigate("https://info.sporttery.cn/football/history/history_data.php?"+id).waitDocumentReady()
                .enableConsoleLog()
                .enableDetailLog()
                .enableNetworkLog();
        //2018年以后有附加赛
        if(year<=2018) {
            session.click("[round_type='table']").wait(1000);
        }
        session.click(".u_btn_left").wait(1000);
        for(int i=1;i<=23;i++){
            session.click(".box table [id='"+i+"'] a").wait(1000);
            String content = session.getContent();
            Document doc = Jsoup.parse(content);
            Elements matchInfo= doc.select("#match_info table");
            Elements trs = matchInfo.select("tr");
            handlerData(trs);
        }
        session.click(".u_btn_right").wait(1000);
        for(int i=24;i<=30;i++){
            session.click(".box table [id='"+i+"'] a").wait(1000);
            String content = session.getContent();
            Document doc = Jsoup.parse(content);
            Elements matchInfo= doc.select("#match_info table");
            Elements trs = matchInfo.select("tr");
            handlerData(trs);
        }
        launcher.kill();
    }

    public void handlerData(Elements trs)throws Exception{
        int index = 0;
        for(Element tr : trs) {
            if(index++==0){
                continue;
            }
            RuiDianInfo ruiDianInfo = new RuiDianInfo();
            ruiDianInfo.setSeason(season);
            String date = tr.select("td").eq(0).html();
            Date matchTime = getMatchTime(date);
            ruiDianInfo.setMatchTime(matchTime);

            String rounds = tr.select("td").eq(1).html();
            ruiDianInfo.setRounds(rounds);

            String host = tr.select("td").eq(2).select("a").html();
            ruiDianInfo.setHost(host);

            String half = tr.select("td").eq(3).select("a").html();
            if(StringUtils.isEmpty(half)){
                half = tr.select("td").eq(3).html();
            }
            if (StringUtils.isEmpty(half)) {
                continue;
            }
            String halfHostScoreStr = half.split(":")[0];
            String halfGuestScoreStr = half.split(":")[1];
            ruiDianInfo.setHalfHostScore(Integer.parseInt(halfHostScoreStr));
            ruiDianInfo.setHalfGuestScore(Integer.parseInt(halfGuestScoreStr));

            String all = tr.select("td").eq(4).select("a").html();
            if(StringUtils.isEmpty(all)){
                all = tr.select("td").eq(4).html();
            }
            if (StringUtils.isEmpty(all)) {
                continue;
            }

            String allHostScoreStr = all.split(":")[0];
            String allGuestScoreStr = all.split(":")[1];
            ruiDianInfo.setHostScore(Integer.parseInt(allHostScoreStr));
            ruiDianInfo.setGuestScore(Integer.parseInt(allGuestScoreStr));

            String guest = tr.select("td").eq(5).select("a").html();
            ruiDianInfo.setGuest(guest);

            String winPrice = tr.select("td").eq(6).select("span").eq(0).html();
            ruiDianInfo.setWinPrice(handlerBigDecimal(winPrice));
            String evenPrice = tr.select("td").eq(6).select("span").eq(1).html();
            ruiDianInfo.setEvenPrice(handlerBigDecimal(evenPrice));
            String lostPrice = tr.select("td").eq(6).select("span").eq(2).html();
            ruiDianInfo.setLostPrice(handlerBigDecimal(lostPrice));
            System.out.println(ruiDianInfo);
            ruiDianInfoMapper.insert(ruiDianInfo);
        }
    }


    private BigDecimal handlerBigDecimal(String price){
        return isNumber(price)?new BigDecimal(price):null;
    }

    private Boolean isNumber(String num){
        try{
            Double.parseDouble(num);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    private Date getMatchTime(String date)throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date = date.replace("&nbsp;","");
        return sdf.parse(date);
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        for(int i=2020;i>=2009;i--){
            getData(i);
        }
    }

}
