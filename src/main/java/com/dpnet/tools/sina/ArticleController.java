package com.dpnet.tools.sina;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dpnet.tools.qq.domain.model.Sinaweibo;
import com.dpnet.tools.qq.domain.repository.SinaweiboMapper;
import com.sun.deploy.net.HttpUtils;
import lombok.extern.log4j.Log4j2;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hooger on 2018/3/21.
 */
@RestController
@EnableAutoConfiguration
@Log4j2
@MapperScan(basePackages="com.dpnet.tools.qq.domain.repository")
@RequestMapping("sinaweibo")
public class ArticleController {

    @Autowired
    private SinaweiboMapper sinaweiboMapper;

    public static String cookies = "SINAGLOBAL=178809319303.0882.1496503731701; _s_tentry=login.sina.com.cn; Apache=2430464773913.2344.1521714940109; ULV=1521714940208:6:4:3:2430464773913.2344.1521714940109:1521626397476; SWBSSL=usrmdinst_6; SWB=usrmdinst_21; login_sid_t=dd4f375b39844f73373cb70fbaa37424; cross_origin_proto=SSL; UOR=www.lanrentuku.com,widget.weibo.com,login.sina.com.cn; un=cgruppo@sina.com; SCF=AkZzXA2yFaXdjAtOlR5VMg8cN47ZcDYU-g8DtT-YEkF0RokzYS7OEthVtXiUgpdz-Jyhn8bWrYcbwbNBrb3dr50.; SUB=_2A253sNKfDeRhGeNO6VEX9y_IzjuIHXVUxENXrDV8PUNbmtBeLXTlkW9NTxstyh_Js8VXOREWPUyb_XonGMNg9xMo; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9WWj-9qreVV-wJsSg1SbSE6y5JpX5KzhUgL.Fo-7eoecS02XSKM2dJLoI0qLxKqLBonLBonLxK-LBKBLBKMLxK-L1KzL1KqLxKBLBonL1h5LxKML1-2L1hMLxK-L1-eL1-qt; SUHB=0la165lx8BIKVA; ALF=1553323599; SSOLoginState=1521787599; wvr=6; WBStorage=c5ff51335af29d81|undefine";


    @RequestMapping("login")
    @ResponseBody
    public String login(@RequestParam("cookie") String cookie){
        ArticleController.cookies = cookie;
        return "ok";
    }


    @RequestMapping("craw")
    @ResponseBody
    public String craw(@RequestParam("keyword") String keyword, @RequestParam("xsort") String xsort, @RequestParam("haspic") String haspic, @RequestParam("page") int page) throws Exception {


while(page-- > 0) {
    String url = "http://s.weibo.com/weibo/" + URLEncoder.encode(keyword, "UTF-8") + "&xsort=" + xsort + "&haspic=" + haspic + "&page=" + page;
    String content = new String(download(url).toByteArray(), "UTF-8");

    Pattern pattern = Pattern.compile("<script>STK \\&\\& STK.pageletM \\&\\& STK\\.pageletM\\.view\\((.*)\\)</script>");

    Matcher mat = pattern.matcher(content);
    String html = "";
    while (mat.find()) {
        String json = mat.group(1);
        JSONObject jb = JSON.parseObject(json);
        if ("pl_weibo_direct".equals(jb.getString("pid"))) {
            html = jb.getString("html");
            break;
        }
    }
    html = html.replaceAll("\\\\t", "   ").replaceAll("\\\\\"", "\"").replaceAll("\\\\/", "/").replaceAll("\\\\n", "\n");

    pattern = Pattern.compile("<div class=\"WB_cardwrap S_bg2 clearfix\" >(.*?)<div node-type=\"feed_list_repeat\"", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    int i = 0;
    mat = pattern.matcher(html);
    int count = 20;
    while (mat.find() && count-- > 0) {
        try {
            String feed = mat.group(1);

            Matcher m = Pattern.compile("<div mid=\"(.*?)\"").matcher(feed);
            String mid = "";
            if (m.find()) {
                mid = m.group(1);
            }
            String headimg = "";
            String nickname = "";
            String uid = "";
            m = Pattern.compile("<img.*?src=\"(.*?)\".*?alt=\"(.*?)\".*?width=\"50\".*?height=\"50\".*?usercard=\"id=(\\d+)\\&.*?\".*?class=\"W_face_radius\".*?>").matcher(feed);
            if (m.find()) {
                headimg = m.group(1);
                nickname = m.group(2);
                uid = m.group(3);
            }

            String commenttxt = "";
            m = Pattern.compile("<p class=\"comment_txt\"(.*?)</p>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(feed);
            if (m.find()) {
                String p = m.group(1);
                Matcher mm = Pattern.compile(">([^<]*)<").matcher(p);
                while (mm.find()) {
                    commenttxt = commenttxt + mm.group(1);
                }
            }

            String commentpic = "";

            m = Pattern.compile("<ul[^>]*?WB_media_a[^>]*?>(.*?)</ul>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(feed);
            if (m.find()) {
                String p = m.group(1);
                Matcher mm = Pattern.compile("<img.*?src=\"(.*?)\"[^>]*?>").matcher(p);
                while (mm.find()) {
                    commentpic += mm.group(1) + ";";
                }
            }


            log.info(String.format("i=[%d],uid=[%s],mid=[%s],头像=[%s],昵称=[%s],文本=[%s],图片=[%s]", ++i, uid, mid, headimg, nickname, commenttxt, commentpic));

            //Thread.sleep(new Random().nextInt(10) * 2000);
            //trans(mid, "1", "欢迎关注微信公众号\"游大咖\"http://dp-ustar.legendh5.com/h5/joinustar.html", "1");
            //comment(mid, "5023671457", "0", "0", "欢迎关注微信公众号\"游大咖\"http://dp-ustar.legendh5.com/h5/joinustar.html");


            Sinaweibo weibo = new Sinaweibo();
            weibo.setHeadimg(headimg);
            weibo.setMid(mid);
            weibo.setNick(nickname);
            weibo.setUid(uid);
            weibo.setWeiboimg(commentpic);
            weibo.setWeibotxt(commenttxt);
            sinaweiboMapper.insert(weibo);
        }catch (Exception e){
            log.error("", e);
        }
    }


    Thread.sleep(new Random().nextInt(10)*2000);

}
        return "ok";
    }

    @RequestMapping("trans")
    @ResponseBody
    public String randomtrans(@RequestParam("num") int num){

        int ct = sinaweiboMapper.selectCount(new Sinaweibo());

        while(num-- > 0){
            try {
                int id = new Random().nextInt(ct);
                Sinaweibo sw = new Sinaweibo();
                sw.setId(id);
                Object to = sinaweiboMapper.selectOne(sw);
                BeanUtils.copyProperties(to, sw);
                trans(sw.getMid(), "1", sw.getWeibotxt()+",欢迎关注微信公众号\"游大咖\"http://ustar.dpsoft.top/desc.html", "1");
            }catch (Exception e){
                log.error("", e);
            }
        }

        return "ok";
    }


    public String trans(String mid,String styleType,String reason,String is_comment_base) throws Exception {
        String url = "http://s.weibo.com/ajax/mblog/forward?__rnd="+System.currentTimeMillis();


        Properties prop = new Properties();
        prop.setProperty("Content-type", "application/x-www-form-urlencoded");
        prop.setProperty("mid", mid);
        prop.setProperty("style_type", styleType);
        prop.setProperty("reason", reason);
        prop.setProperty("is_comment_base", is_comment_base);
        String content = new String(sendPostResquest(url, prop).toByteArray(),"UTF-8");

        log.info(content);
        return null;
    }

    public String comment(String mid, String uid, String forward, String isroot, String content) throws Exception {
        String url = "http://s.weibo.com/ajax/comment/add?__rnd="+System.currentTimeMillis();
        Properties prop = new Properties();
        prop.setProperty("mid", mid);
        prop.setProperty("uid", uid);
        prop.setProperty("forward", forward);
        prop.setProperty("isroot", isroot);
        prop.setProperty("content", content);// "欢迎关注微信公众号\"游大咖\"http://dp-ustar.legendh5.com/h5/joinustar.html");
        String resp = new String(sendPostResquest(url, prop).toByteArray(),"UTF-8");

        log.info(resp);
        return null;


    }


    private static ByteArrayOutputStream download(String url) throws Exception {
        log.info(url);
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        InputStream in = null;
        try{

            URLConnection conn =  new URL(url).openConnection();
            conn.setRequestProperty("Cookie", cookies);

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



    public static ByteArrayOutputStream sendPostResquest(String url,Properties nameValue)
            throws Exception {


        InputStream in = null;
        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
        try{
        //2，通过URL对象的openConnection创建URLConnection
        HttpURLConnection con = (HttpURLConnection)new URL(url).openConnection();
            con.setRequestProperty("Cookie", cookies);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

            con.setRequestProperty("Host","s.weibo.com");
            con.setRequestProperty("Connection","keep-alive");
            //con.setRequestProperty("Content-Length","252");
            con.setRequestProperty("Origin","http://s.weibo.com");
            con.setRequestProperty("X-Requested-With","XMLHttpRequest");
            con.setRequestProperty("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.162 Safari/537.36");
            con.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            con.setRequestProperty("Accept","*/*");
            con.setRequestProperty("Referer","http://s.weibo.com/weibo/%E6%97%85%E6%B8%B8&xsort=hot&haspic=1");

        //3，post请求的参数名/值队要放在HTTP正文中
        con.setDoOutput(true);//设置是否使用URL连接进行输出

        //4.把请求参数添加到连接对象中
        OutputStream os = con.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os);
        PrintWriter pw = new PrintWriter(osw,true);

        for(Iterator it = nameValue.keySet().iterator(); it.hasNext();){
            String key = (String) it.next();
            String values = nameValue.getProperty(key);
            pw.write(key);
            pw.write("=");
            pw.write(values);
            if(it.hasNext()){
                pw.write("&");
            }
        }

        pw.close();
        //4.连接
        con.connect();


            //5.连接服务器后，就可以查询头部信息了
            Map<String,List<String>> headerMap = con.getHeaderFields();
            for(Map.Entry<String, List<String>> entry : headerMap.entrySet()){
                String key = entry.getKey();
                List<String> values = entry.getValue();
                StringBuilder sb = new StringBuilder();
                int size=values==null?0:values.size();
                for(int i=0;i<size;i++){
                    if(i>0){
                        sb.append(",");
                    }
                    sb.append(values.get(i));
                }
                System.out.println(key+":"+sb.toString());
            }
            System.out.println("--------------------------");




            //6.获取输入流用来读取数据


        in = con.getInputStream();
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


    public static void main(String[]args) throws Exception {
        //new ArticleController().craw("旅游攻略","","1", 1);

        //new ArticleController().trans("4219722318345405", "1", "欢迎关注微信公众号\"游大咖\"http://dp-ustar.legendh5.com/h5/joinustar.html", "1");
        SpringApplication.run(ArticleController.class, args);
    }

}
