//package com.dpnet.tools.ctrip.craw;
//
//import java.io.*;
//import java.net.URL;
//import java.net.URLConnection;
//import java.util.*;
//import java.util.concurrent.LinkedBlockingDeque;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
///**
// * Created by hooger on 2018/4/7.
// */
//public class EntranceTicketExtractor {
//
//
//    public static LinkedBlockingDeque<String> urlList = new LinkedBlockingDeque<String>();
//
//
//    public static int cnum=0;
//
//    public static int maxnum;
//
//    public static String fileName = "lvyou_baidu_url.txt";
//
//    public static String minNoFileName = "lvyou_baidu_no.properties";
//
//    public static Properties prop;
//
//    public static OutputStream noFileOutput;
//
//    public static int crawNum = 0;
//
//    public static ThreadPoolExecutor pool =  new ThreadPoolExecutor(100, 200, 10, TimeUnit.SECONDS, new LinkedBlockingDeque(),new ThreadPoolExecutor.AbortPolicy());
//
//
//
//
//
//
//
//
//    public static void addUrl(String url) throws Exception{
//        ConnectionDB dbconn = ConnectionDB.getTravelDB();
//        String sql = "insert into url_history (url) values(?)";
//        dbconn.executeUpdate(sql, url);
//    }
//
//    public static boolean isContain(String url) throws Exception{
//        ConnectionDB dbconn = ConnectionDB.getTravelDB();
//        String sql = "select count(*) as num from url_history where url=?";
//        List<Map<String,Object>> rs = dbconn.excuteQuery(sql,url);
//        if(rs != null && !rs.isEmpty()){
//            Long num = (Long) rs.get(0).get("num");
//            if(num.intValue() <= 0){
//                return false;
//            }else{
//                return true;
//            }
//        }
//        return false;
//    }
//
//
//
//
//
//
//
//    public static void deal() throws Exception{
//        int i = 0;
//        int flag = 1;
//        List<String> hrefList;
//        do{
//            ConnectionDB dbconn = ConnectionDB.getTravelDB();
//            String sql = "select url from seed limit "+i+", 300";
//            List<Map<String,Object>> resultList = dbconn.excuteQuery(sql);
//            hrefList = new ArrayList<String>();
//            for(Map<String,Object> record : resultList){
//                String url = String.valueOf(record.get("url"));
//                hrefList.add(url);
//            }
//
//            for(String h : hrefList){
//                final String href = h;
//
//                if(pool.getQueue().size() < 100){
//                    pool.execute(new Runnable(){
//
//                        @Override
//                        public void run() {
//                            // TODO Auto-generated method stub
//                            try {
//                                BaiduExtractor.deal(href);
//                            } catch (Exception e) {
//                                // TODO Auto-generated catch block
//                                e.printStackTrace();
//                            }
//                        }
//
//                    });
//
//                }else{
//                    try {
//                        deal(href);
//                    } catch (Exception e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//            i = 300;
//            if(hrefList.size()<=0){
//                i--;
//            }
//            sql = "select flag from switch";
//            resultList = dbconn.excuteQuery(sql);
//            flag = (Integer)resultList.get(0).get("flag");
//        }while(1 == flag);
//    }
//
//    public static void deal(String url)throws Exception{
//
//        url = urltrans(url);
//        if(url != null){
//            LogUtil.info("URL=>"+url+"开始检查重复...");
//            if(isContain(url)){
//                ConnectionDB dbconn = ConnectionDB.getTravelDB();
//                String sql = "delete from seed where url=?";
//                dbconn.executeUpdate(sql, url);
//                LogUtil.info("URL=>"+url+" 重复...");
//                return;
//            }
//            if(isContextPage(url)){
//                contentExtract(url);
//            }else{
//                linkExtract(url);
//            }
//        }
//
//    }
//
//    public static void linkExtract(String url)throws Exception{
//
////		if(cnum>maxnum){
////			LogUtil.info("URL=>"+cnum+" 超出最大条数");
////			return;
////		}
//
////		LogUtil.info("URL=>"+url+"开始下载...");
//        if(!isCraw(url)){
//            ConnectionDB dbconn = ConnectionDB.getTravelDB();
//            String sql = "delete from seed where url=?";
//            dbconn.executeUpdate(sql, url);
//            addUrl(url);
//
//            LogUtil.info("URL=>"+url+"不合法,取消下载...");
//            return;
//        }
//        String sb = download(url).toString("UTF-8");
//
//        LogUtil.info("URL=>"+url+"提取链接...");
//        List<String> hrefList = new ArrayList<String>();
//        String regex = "href *= *\"([^\"]+)\"";
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(sb.toString());
//
//        boolean result = matcher.find();
//
//        while(result){
//            String href = matcher.group(1);
//            hrefList.add(href);
//            result = matcher.find();
//        }
//        ConnectionDB dbconn = ConnectionDB.getTravelDB();
//        String sql = "insert into seed (url) values(?)";
//        for(String href : hrefList){
//            href = urltrans(href);
//            if(href != null && href.trim().length()>0 && !isContain(href)){
//                LogUtil.info("add seed =["+href+"]");
//                //dbconn.executeUpdate(sql, href);
//            }
//        }
//        sql = "delete from seed where url=?";
//        dbconn.executeUpdate(sql, url);
//        addUrl(url);
//
////		for(String h : hrefList){
////			final String href = h;
////
////			if(pool.getQueue().size() < 10){
////			pool.execute(new Runnable(){
////
////				@Override
////				public void run() {
////					// TODO Auto-generated method stub
////					try {
////						BaiduExtractor.deal(href);
////					} catch (Exception e) {
////						// TODO Auto-generated catch block
////						e.printStackTrace();
////					}
////				}
////
////			});
////
////			}else{
////				try {
////				deal(href);
////				} catch (Exception e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				}
////			}
////
////		}
//    }
//
//
//
//
//    private static ByteArrayOutputStream download(String url) throws Exception {
//        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
//        InputStream in = null;
//        try{
//
////			System.getProperties().put(   "proxySet",   "true"   );
////
////			  // 设置http访问要使用的代理服务器的地址
////
////			System.getProperties().setProperty("http.proxyHost", "192.168.60.7");
////
////			   // 设置http访问要使用的代理服务器的端口
////
////			System.getProperties().setProperty("http.proxyPort", "80");
//
//
//
//            URLConnection conn =  new URL(url).openConnection();
//            UUID uuid = UUID.randomUUID();
//
//            conn.setRequestProperty("Cookie", "sessionid="+uuid.toString()+"; csrftoken=dAkxT6n6DcsjWDdQiFDEWDMSZpxv5uA0; Hm_lvt_4424053ee9f86dc524876ab488113772=1490771605,1490844116,1491468561,1491546870; Hm_lpvt_4424053ee9f86dc524876ab488113772=1491549215");
//            in = conn.getInputStream();
//
//            byte[]bs = new byte[1024];
//            int len = -1;
//
//            while((len=in.read(bs))!=-1){
//                byteout.write(bs, 0, len);
//            }
//
//        }catch (Exception e) {
//            LogUtil.error("", e);
//            throw new Exception("下载页面出错,URL=>["+url+"]");
//        }finally{
//            try{
//                if(in!=null)
//                    in.close();
//            }catch (Exception e) {
//                LogUtil.error("", e);
//            }
//        }
//        return byteout;
//    }
//
//    public static void contentExtract(String url)throws Exception{
//        cnum++;
//        LogUtil.info("URL=>"+url+"内容页下载...");
//        String reg = "https://lvyou.baidu.com/(\\S+)/jingdian";
//        List<List<String>> addrList = crawReg(url, reg, 1);
//        DBManager dbManager = new DBManager();
//        String addrCode = addrList.get(0).get(0);
//        dbManager.createAddr(addrCode);
//
//        ConnectionDB dbconn = ConnectionDB.getTravelDB();
//        String sql = "delete from seed where url=?";
//        dbconn.executeUpdate(sql, url);
//        addUrl(url);
//
//
//    }
//
//
//
//
//
//    public static List<List<String>> crawReg(String content, String reg , int groups){
//        Pattern pattern = Pattern.compile(reg);
//        Matcher matcher = pattern.matcher(content);
//        List<List<String>> valueList = new ArrayList<List<String>>();
//        while(matcher.find()){
//            List<String> values = new ArrayList<String>();
//            for(int i=0; i < groups ;i++){
//                values.add(matcher.group(i+1));
//            }
//            valueList.add(values);
//        }
//        LogUtil.info(String.format("解析出内容=[%s],表达式=[%s]", valueList, reg));
//        return valueList;
//
//    }
//
//
//
//
//    public static String urltrans(String url){
//        url = url.trim();
//        if(url.toLowerCase().startsWith("https://")||url.toLowerCase().startsWith("https://")||url.startsWith("/")){
//            if(!url.toLowerCase().startsWith("https://") && !url.toLowerCase().startsWith("http://")){
//                url = "https://lvyou.baidu.com"+url;
//            }
//            return url;
//        }
//        return null;
//    }
//    public static boolean isContextPage(String url){
////		String contentPrefix = "http://www.vipysdd.com/common/nodeDetail.html";
////		if(url.startsWith(contentPrefix)){
////			return true;
////		}
//
//        String reg = "https://lvyou.baidu.com/(\\S+)/jingdian";
//        Pattern pattern = Pattern.compile(reg);
//        Matcher matcher = pattern.matcher(url);
//        if(matcher.find()){
//            LogUtil.info(url);
//            return true;
//        }
//        return false;
//    }
//    public static boolean isCraw(String url){
//        return url.toLowerCase().startsWith("https://lvyou.baidu.com/");
//    }
//
//
//
//
//
//    public static void main(String[] args) throws Exception{
//
//        isContextPage("https://lvyou.baidu.com/faguo/jingdian/");
//    }
//
//
//
//}
