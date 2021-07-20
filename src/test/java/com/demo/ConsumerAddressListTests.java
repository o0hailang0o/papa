package com.demo;

import io.webfolder.cdp.Launcher;
import io.webfolder.cdp.session.Session;
import io.webfolder.cdp.session.SessionFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class ConsumerAddressListTests {

   /*// @Autowired
    private AddressListService addressListService;

    //@Autowired
    private AddressWebService addressWebService;

   // @Test
    public void test(){
        Session session = null;
        for(int a=1;a<1500;a++){
            try{
                List<AddressList> addressListList =  addressListService.getAddressList();
                for(int i=0;i<addressListList.size();i++) {
                    AddressList addressList = addressListList.get(i);
                    String href = addressList.getHref();
                    session = getSession();
                    session.navigate("http://app1.sfda.gov.cn/datasearchcnda/face3/" + href);
                    Thread.sleep(5 * 1000);
                    String content = session.getContent();
                    Document doc = Jsoup.parse(content, "http://app1.sfda.gov.cn/");
                    Elements dom = doc.select(".listmain tr").eq(1).select("td").eq(1);
                    //许可证号
                    String certNo = dom.html();
                    Elements dom1 = doc.select(".listmain tr").eq(2).select("td").eq(1);
                    //企业名称
                    String addressName = dom1.html();

                    Elements dom2 = doc.select(".listmain tr").eq(3).select("td").eq(1);
                    //企业地址
                    String address = dom2.html();

                    Elements dom3 = doc.select(".listmain tr").eq(4).select("td").eq(1);
                    String warehouseAddress = dom3.html();

                    Elements dom4 = doc.select(".listmain tr").eq(5).select("td").eq(1);
                    //法人代表
                    String legalPerson = dom4.html();

                    Elements dom5 = doc.select(".listmain tr").eq(6).select("td").eq(1);
                    //企业负责人
                    String companyPrincipal = dom5.html();

                    Elements dom6 = doc.select(".listmain tr").eq(7).select("td").eq(1);
                    //质量负责人
                    String qualityPrincipal = dom6.html();

                    Elements dom7 = doc.select(".listmain tr").eq(8).select("td").eq(1);
                    //经营方式
                    String operationMode = dom7.html();

                    Elements dom8 = doc.select(".listmain tr").eq(9).select("td").eq(1);
                    //经营范围
                    String businessScope = dom8.html();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Elements dom9 = doc.select(".listmain tr").eq(10).select("td").eq(1);
                    //发证日期
                    Date issueCertTime = null;
                    if(!dom9.html().equals("")){
                        issueCertTime = sdf.parse(dom9.html());
                    }

                    Elements dom10 = doc.select(".listmain tr").eq(11).select("td").eq(1);
                    //证书有效期
                    Date certPeriodTime = null;
                    if(!dom10.html().equals("")) {
                        certPeriodTime = sdf.parse(dom10.html());
                    }

                    Elements dom11 = doc.select(".listmain tr").eq(12).select("td").eq(1);
                    //发证机构
                    String certIssueOrg = dom11.html();

                    AddressWeb addressWeb = new AddressWeb();
                    addressWeb.setAddressId(addressList.getId());
                    addressWeb.setCertNo(certNo);
                    addressWeb.setAddressName(addressName);
                    addressWeb.setAddress(address);
                    addressWeb.setWarehouseAddress(warehouseAddress);
                    addressWeb.setLegalPerson(legalPerson);
                    addressWeb.setCompanyPrincipal(companyPrincipal);
                    addressWeb.setQualityPrincipal(qualityPrincipal);
                    addressWeb.setOperationMode(operationMode);
                    addressWeb.setBusinessScope(businessScope);
                    addressWeb.setIssueCertTime(issueCertTime);
                    addressWeb.setCertPeriodTime(certPeriodTime);
                    addressWeb.setCertIssueOrg(certIssueOrg);
                    addressWebService.addAddressWeb(addressWeb);
                    addressListService.updateStatus(addressList.getId());
                  //  session.stop();
                   // session.close();
                }
            }catch(Exception e){
                e.printStackTrace();
                continue;
            }finally {
//                session.stop();
  //              session.close();
                continue;
            }
        }

    }

    public static Session createSession(){
        Launcher launcher = new Launcher();
        SessionFactory factory = launcher.launch();
        Session session = factory.create();
        return session;
    }

    private static Session session=null;
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

    public static void main(String[] args)throws Exception{
        Session session = createSession();
        session.navigate("http://app1.sfda.gov.cn/datasearchcnda/face3/content.jsp?tableId=41&tableName=TABLE41&tableView=%E8%8D%AF%E5%93%81%E7%BB%8F%E8%90%A5%E4%BC%81%E4%B8%9A&Id=450838");
        Thread.sleep(5 * 1000);
        String content = session.getContent();
        Document doc = Jsoup.parse(content, "http://app1.sfda.gov.cn/");
        Elements dom = doc.select(".listmain tr").eq(1).select("td").eq(1);
        //许可证号
        String certNo = dom.html();
        Elements dom1 = doc.select(".listmain tr").eq(2).select("td").eq(1);
        //企业名称
        String addressName = dom1.html();

        Elements dom2  = doc.select(".listmain tr").eq(3).select("td").eq(1);
        //企业地址
        String address = dom2.html();

        Elements dom3 = doc.select(".listmain tr").eq(4).select("td").eq(1);
        String warehouseAddress = dom3.html();

        Elements dom4 = doc.select(".listmain tr").eq(5).select("td").eq(1);
        //法人代表
        String legalPerson = dom4.html();

        Elements dom5 = doc.select(".listmain tr").eq(6).select("td").eq(1);
        //企业负责人
        String companyPrincipal = dom5.html();

        Elements dom6 = doc.select(".listmain tr").eq(7).select("td").eq(1);
        //质量负责人
        String qualityPrincipal = dom6.html();

        Elements dom7 = doc.select(".listmain tr").eq(8).select("td").eq(1);
        //经营方式
        String operationMode = dom7.html();

        Elements dom8 = doc.select(".listmain tr").eq(9).select("td").eq(1);
        //经营范围
        String businessScope = dom8.html();

        SimpleDateFormat sdf = new SimpleDateFormat();
        Elements dom9 = doc.select(".listmain tr").eq(10).select("td").eq(1);
        //发证日期
        Date issueCertTime = sdf.parse(dom9.html());

        Elements dom10 = doc.select(".listmain tr").eq(11).select("td").eq(1);
        //证书有效期
        Date certPeriodTime = sdf.parse(dom10.html());

        Elements dom11 = doc.select(".listmain tr").eq(12).select("td").eq(1);
        //发证机构
        Date certIssueOrg = sdf.parse(dom11.html());



        System.out.println(companyPrincipal);
        session.stop();
        session.close();
    }*/
}
