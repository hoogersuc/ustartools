package com.dpnet.tools.ctrip.craw;

import com.dpnet.tools.qq.domain.model.Seed;
import com.dpnet.tools.qq.domain.model.Switch;
import com.dpnet.tools.qq.domain.model.UrlHistory;
import com.dpnet.tools.qq.domain.repository.SeedMapper;
import com.dpnet.tools.qq.domain.repository.SwitchMapper;
import com.dpnet.tools.qq.domain.repository.UrlHistoryMapper;
import com.dpnet.utils.HttpHelper;
import com.dpnet.utils.RequestResult;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hooger on 2018/4/7.
 */
@RestController
@EnableAutoConfiguration
@Log4j2
@MapperScan(basePackages="com.dpnet.tools.qq.domain.repository")
@RequestMapping("entrance")
public class EntranceTicketExtractor {


    @Autowired
    private SeedMapper seedMapper;
    @Autowired
    private SwitchMapper switchMapper;
    @Autowired
    private UrlHistoryMapper urlHistoryMapper;

    public static LinkedBlockingDeque<String> urlList = new LinkedBlockingDeque<String>();


    public static int cnum=0;

    public static int maxnum;

    public static String fileName = "lvyou_baidu_url.txt";

    public static String minNoFileName = "lvyou_baidu_no.properties";

    public static Properties prop;

    public static OutputStream noFileOutput;

    public static int crawNum = 0;

    public static ThreadPoolExecutor pool =  new ThreadPoolExecutor(100, 200, 10, TimeUnit.SECONDS, new LinkedBlockingDeque(),new ThreadPoolExecutor.AbortPolicy());








    public void addUrl(String url) throws Exception{

        UrlHistory uh = new UrlHistory();
        uh.setUrl(url);
        urlHistoryMapper.insert(uh);


    }

    public boolean isContain(String url) throws Exception{

        UrlHistory uh = new UrlHistory();
        uh.setUrl(url);
        int num = urlHistoryMapper.selectCount(uh);

            if(num <= 0){
                return false;
            }else{
                return true;
            }

    }







    @RequestMapping("craw")
    @ResponseBody
    public String deal() throws Exception{
        int i = 0;
        int flag = 1;
        List<String> hrefList;
        do{
//            ConnectionDB dbconn = ConnectionDB.getTravelDB();
//            String sql = "select url from seed limit "+i+", 300";
//            List<Map<String,Object>> resultList = dbconn.excuteQuery(sql);
            List<Seed> resultList = seedMapper.selectByRowBounds(new Seed(), new RowBounds(i,300));
            hrefList = new ArrayList<String>();
            for(Seed record : resultList){
                String url = String.valueOf(record.getUrl());
                hrefList.add(url);
            }

            for(String h : hrefList){
                final String href = h;

                if(pool.getQueue().size() < 100){
                    pool.execute(new Runnable(){

                        @Override
                        public void run() {
                            try {
                                deal(href);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    });

                }else{
                    try {
                        deal(href);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
            i = 300;
            if(hrefList.size()<=0){
                i--;
            }
//            sql = "select flag from switch";
//            resultList = dbconn.excuteQuery(sql);
            Switch swch = new Switch();
            swch.setId(1);
            swch = switchMapper.selectOne(swch);
            flag = swch.getFlag();
        }while(1 == flag);
        return "ok";
    }

    public void deal(String url)throws Exception{

        url = urltrans(url);
        if(url != null){
            log.info("URL=>"+url+"开始检查重复...");
            if(isContain(url)){
//                ConnectionDB dbconn = ConnectionDB.getTravelDB();
//                String sql = "delete from seed where url=?";
//                dbconn.executeUpdate(sql, url);
                Seed sd = new Seed();
                sd.setUrl(url);
                seedMapper.delete(sd);
                log.info("URL=>"+url+" 重复...");
                return;
            }
            if(isContextPage(url)){
                contentExtract(url);
            }
                linkExtract(url);

        }

    }

    public  void linkExtract(String url)throws Exception{

//		if(cnum>maxnum){
//			LogUtil.info("URL=>"+cnum+" 超出最大条数");
//			return;
//		}

//		LogUtil.info("URL=>"+url+"开始下载...");
        if(!isCraw(url)){
            Seed sd = new Seed();
            sd.setUrl(url);
            seedMapper.delete(sd);
            addUrl(url);

            log.info("URL=>"+url+"不合法,取消下载...");
            return;
        }
        String sb = download(url).toString("UTF-8");

        log.info("URL=>"+url+"提取链接...");
        List<String> hrefList = new ArrayList<String>();
        String regex = "href *= *\"([^\"]+)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sb.toString());

        boolean result = matcher.find();

        while(result){
            String href = matcher.group(1);
            hrefList.add(href);
            result = matcher.find();
        }

        for(String href : hrefList){
            href = urltrans(href);
            if(href != null && href.trim().length()>0 && !isContain(href)){
                log.info("add seed =["+href+"]");
                //dbconn.executeUpdate(sql, href);
            }
        }
        Seed sd = new Seed();
        sd.setUrl(url);
        seedMapper.delete(sd);
        addUrl(url);

//		for(String h : hrefList){
//			final String href = h;
//
//			if(pool.getQueue().size() < 10){
//			pool.execute(new Runnable(){
//
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					try {
//						BaiduExtractor.deal(href);
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//
//			});
//
//			}else{
//				try {
//				deal(href);
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//
//		}
    }




    private static ByteArrayOutputStream download(String url) throws Exception {
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        InputStream in = null;
        try{

//			System.getProperties().put(   "proxySet",   "true"   );
//
//			  // 设置http访问要使用的代理服务器的地址
//
//			System.getProperties().setProperty("http.proxyHost", "192.168.60.7");
//
//			   // 设置http访问要使用的代理服务器的端口
//
//			System.getProperties().setProperty("http.proxyPort", "80");



            URLConnection conn =  new URL(url).openConnection();
            UUID uuid = UUID.randomUUID();

            conn.setRequestProperty("Cookie", "sessionid="+uuid.toString()+"; csrftoken=dAkxT6n6DcsjWDdQiFDEWDMSZpxv5uA0; Hm_lvt_4424053ee9f86dc524876ab488113772=1490771605,1490844116,1491468561,1491546870; Hm_lpvt_4424053ee9f86dc524876ab488113772=1491549215");
            in = conn.getInputStream();

            byte[]bs = new byte[1024];
            int len = -1;

            while((len=in.read(bs))!=-1){
                byteout.write(bs, 0, len);
            }

        }catch (Exception e) {
            log.error("", e);
            throw new Exception("下载页面出错,URL=>["+url+"]");
        }finally{
            try{
                if(in!=null)
                    in.close();
            }catch (Exception e) {
                log.error("", e);
            }
        }
        return byteout;
    }

    public  void contentExtract(String url)throws Exception{
        cnum++;
        log.info("URL=>"+url+"内容页下载...");


        String reg = "http://piao.ctrip.com/dest/(.*?).html";
        List<List<String>> codeList = crawReg(url, reg, 1);
        String spotPathCode = codeList.get(0).get(0);

        RequestResult rt =  HttpHelper.doGet(url);
        String content = rt.getResult();


        reg = "<div id=\"main-nav\">.*?<a [^>]*>(.*?)</a>.*?<a .*?href=\"http://piao.ctrip.com/dest/([^/]+)/s-tickets/\"[^>]*>(.*?)</a>.*?<a .*?href=\"http://piao.ctrip.com/dest/([^/]+)/s-tickets/\"[^>]*>(.*?)</a>.*?<h1 [^>]*>(.*?)</h1>.*?</div>.*?" +
                "<h2 class=\"media-title\">(.*?)</h2>.*?" +
                "<span class=\"media-grade\" style=\"\"><strong>(.*?)</strong>景区</span>.*?" +
                "<em>景点地址</em>.*?<span>.*?(\\S+?)。.*?</span>.*?" +
                "<span class=\"j-limit\">.*?(\\S+?).*?</span>.*?" +
                "<div class=\"jmp pop-content\">.*?(\\S+?).*?</div>.*?" +
                "<div class=\"grade\" id=\"J-grade\" data-value=\"(\\S+?)\" style=\";\">";



        List<List<String>> crawList = crawReg(content, reg, 12);

        String typePathname = crawList.get(0).get(0);
        String provincePathCode = crawList.get(0).get(1);
        String provincePathName = crawList.get(0).get(2);
        String cityPathCode = crawList.get(0).get(3);
        String cityPathName = crawList.get(0).get(4);
        String spotPathName = crawList.get(0).get(5);

        String spotName = crawList.get(0).get(6);

        String grade = crawList.get(0).get(7);

        String addr = crawList.get(0).get(8);

        String opentime = crawList.get(0).get(9);

        String promise = crawList.get(0).get(10);

        String jgrade = crawList.get(0).get(11);




        reg = "<tr class=\"ticket-info.*?data-id=\"(\\S+?)\".*?>(.*?)</tr>";

        crawList = crawReg(content, reg, 1);

        for(List<String> rtcraw : crawList){
            String dataId = rtcraw.get(0);
            String rtStr = rtcraw.get(1);
            


        }


        String jsonurl = "http://piao.ctrip.com/Thingstodo-Booking-ShoppingWebSite/api/TicketStatute?resourceID=14950165";


        Seed sd = new Seed();
        sd.setUrl(url);
        seedMapper.delete(sd);
        addUrl(url);


    }





    public static List<List<String>> crawReg(String content, String reg , int groups){
        Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);
        List<List<String>> valueList = new ArrayList<List<String>>();
        while(matcher.find()){
            List<String> values = new ArrayList<String>();
            for(int i=0; i < groups ;i++){
                values.add(matcher.group(i+1));
            }
            valueList.add(values);
        }
        log.info(String.format("解析出内容=[%s],表达式=[%s]", valueList, reg));
        return valueList;

    }




    public static String urltrans(String url){
        url = url.trim();
        if(url.toLowerCase().startsWith("https://")||url.toLowerCase().startsWith("https://")||url.startsWith("/")){
            if(!url.toLowerCase().startsWith("https://") && !url.toLowerCase().startsWith("http://")){
                url = "http://piao.ctrip.com/"+url;
            }
            return url;
        }
        return null;
    }
    public static boolean isContextPage(String url){
//		String contentPrefix = "http://www.vipysdd.com/common/nodeDetail.html";
//		if(url.startsWith(contentPrefix)){
//			return true;
//		}

        String reg = "http://piao.ctrip.com/dest/([^/]+).html";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(url);
        if(matcher.find()){
            log.info(url);
            return true;
        }
        return false;
    }
    public static boolean isCraw(String url){
        return url.toLowerCase().startsWith("http://piao.ctrip.com/");
    }





    public static void main(String[] args) throws Exception{

        isContextPage("https://lvyou.baidu.com/faguo/jingdian/");
    }



}
