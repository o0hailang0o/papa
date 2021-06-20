package com.demo;

import io.webfolder.cdp.Launcher;
import io.webfolder.cdp.session.Session;
import io.webfolder.cdp.session.SessionFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class DemoApplicationTests {


	private  int startPage = 29113;

	//@Test
	public void contextLoads() throws Exception{
		Session session = getSession();
		session.navigate("http://app1.sfda.gov.cn/datasearchcnda/face3/base.jsp?tableId=41&tableName=TABLE41&title=%D2%A9%C6%B7%BE%AD%D3%AA%C6%F3%D2%B5&bcId=152911863995882985662523838679");
		Thread.sleep(2*1000);
		session.setValue("#goInt",startPage);
		Thread.sleep(2*1000);
		//<input src="images/dataanniu_11.gif" type="image" width="37" height="17" onclick="javascript:goPage(30145)">
		for(int i=startPage;i>0;i--) {
			try {
				if (i == startPage) {
					session.click("input[src='images/dataanniu_11.gif']");
				} else {
					session.click("img[src='images/dataanniu_05.gif']");
				}

				Thread.sleep(5 * 1000);
				String content = session.getContent();

				Document doc = Jsoup.parse(content, "http://app1.sfda.gov.cn/");
				Elements newsHeadlines = doc.select("#content a");
				for (Element headline : newsHeadlines) {
					AddressList addressList = new AddressList();
					String html = headline.html();
					String href = headline.attr("href");
					Long id = Long.parseLong(html.substring(0,html.indexOf(".")));
					String name = html.substring(html.indexOf(".")+1,html.indexOf("("));
					String url = href.substring(href.indexOf("content"),href.lastIndexOf(",")-1);
					addressList.setId(id);
					addressList.setName(name);
					addressList.setHref(url);
					addressList.setStatus(0);
					addressListDao.addAddressList(addressList);
				}
				System.out.println(i+"*************************");
			}catch (Exception e){
				startPage++;
				e.printStackTrace();
			}

		}
	}


	private static Session session = null;

	public static Session createSession(){
		Launcher launcher = new Launcher();
		SessionFactory factory = launcher.launch();
		Session session = factory.create();
		return session;
	}

	public static synchronized Session getSession(){
		try {
			if(session == null) {
				session = createSession();
			}

		}catch (Exception e){
			session = createSession();
		}
		return session;
	}

	private void getAddressList(){
		Session session = getSession();
		session.navigate("http://app1.sfda.gov.cn/datasearchcnda/face3/base.jsp?tableId=41&tableName=TABLE41&title=%D2%A9%C6%B7%BE%AD%D3%AA%C6%F3%D2%B5&bcId=152911863995882985662523838679");
		session.click("img[src='images/dataanniu_07.gif']");
		session.setValue("#goInt",30145);
		//<input src="images/dataanniu_11.gif" type="image" width="37" height="17" onclick="javascript:goPage(30145)">
		session.click("input[src='images/dataanniu_11.gif']");
		String content = session.getContent();
		System.out.println(content);
	}


	public static void main(String[] args)throws Exception{

	}
}
