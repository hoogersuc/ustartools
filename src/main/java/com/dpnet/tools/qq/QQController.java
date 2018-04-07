package com.dpnet.tools.qq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.dpnet.tools.qq.domain.model.Qqmember;
import com.dpnet.tools.qq.domain.repository.QqmemberMapper;
import com.dpnet.utils.HttpHelper;
import com.dpnet.utils.MailUtil;
import com.dpnet.utils.RequestResult;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.*;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by hooger on 2018/3/22.
 */
@RestController
@EnableAutoConfiguration
@Log4j2
@MapperScan(basePackages="com.dpnet.tools.qq.domain.repository")
@RequestMapping("qq")
public class QQController {

    @Autowired
    private QqmemberMapper qqmemberMapper;

    public static String cookies = "RK=vd0Gq8COQt; pgv_pvi=9932497920; tvfe_boss_uuid=cd8ee15fff6d20d8; pgv_pvid=9742381575; ptcz=9b4446f6edd4c198a87411ed68a3ce292bbb244069ee9a94c122da60cfe29a5d; o_cookie=374200051; pt2gguin=o0591739270; pac_uid=1_374200051; pgv_si=s3844974592; p_uin=o0591739270; uin=o0591739270; skey=@mW4nmQuil; ptisp=cnc; pt4_token=GfgnS3o5ld5B90vofZXupwX5nSU4xfFRC2Bwm7Ji0Vs_; p_skey=-rWbZ6qOoyCa6e6Gz3fQP-af3BuboR3Pg14tr-R0FvI_";

    @RequestMapping("login")
    @ResponseBody
    public String login(@RequestParam("cookie") String cookie){
        QQController.cookies = cookie;
        return "ok";
    }


    @RequestMapping("member")
    @ResponseBody
    public String crawGroupMember(@RequestParam("groupId") long groupId,@RequestParam("st") int st,@RequestParam("end") int end,@RequestParam("sort") int sort,@RequestParam("bkn") String bkn){


        String url = "https://qun.qq.com/cgi-bin/qun_mgr/search_group_members";


        Map<String, String> header = new HashMap<>();

        header.put(":authority", "qun.qq.com");
        header.put(":method", "POST");
        header.put(":path", "/cgi-bin/qun_mgr/search_group_members");
        header.put(":scheme", "https");
        header.put("accept", "application/json, text/javascript, */*; q=0.01");
        header.put("accept-encoding", "gzip, deflate, br");
        header.put("accept-language", "zh-CN,zh;q=0.9");
        //header.put("content-length", "46");
        header.put("content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        header.put("cookie", cookies);
        header.put("origin", "https://qun.qq.com");
        header.put("referer", "https://qun.qq.com/member.html");
        header.put("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        header.put("x-requested-with", "XMLHttpRequest");



        Map<String,Object> params = new HashMap<String,Object>();
        params.put("gc",groupId);
        params.put("st", st);
        params.put("end", end);
        params.put("bkn", bkn);
        RequestResult rt = HttpHelper.doPost(url, params, header);

        String jsonrst = rt.getResult();

        log.info(String.format("响应信息=[%s]", jsonrst));

        JSONObject jsonObj = JSON.parseObject(jsonrst);

        JSONArray memArray = jsonObj.getJSONArray("mems");

        for (int i=0; i<memArray.size(); i++){
            JSONObject memObj = memArray.getJSONObject(i);

            Qqmember member = new Qqmember();

            String nick = memObj.getString("nick");
            long uin = memObj.getLong("uin");
            String card = memObj.getString("card");
            int flag = memObj.getIntValue("flag");
            int g = memObj.getIntValue("g");
            long join_time = memObj.getLongValue("join_time");
            long last_speek_time = memObj.getLongValue("last_speak_time");
            JSONObject lvObj = memObj.getJSONObject("lv");
            int level = lvObj.getIntValue("level");
            int point = lvObj.getIntValue("point");
            int qage = memObj.getIntValue("qage");
            int role = memObj.getIntValue("role");
            String tags = memObj.getString("tags");

            member.setCard(card);
            member.setFlag(flag);
            member.setG(g);
            member.setGroupid(groupId);
            member.setJoin_time(join_time);
            member.setLast_speak_time(last_speek_time);
            member.setLevel(level);
            member.setNick(nick);
            member.setPoint(point);
            member.setQage(qage);
            member.setRole(role);
            member.setTags(tags);
            member.setUin(uin);

try{
    qqmemberMapper.insert(member);
}catch (Exception e){
    log.error("", e);
}

        }



        return "ok";
    }



    @RequestMapping("sendmail")
    @ResponseBody
    public String sendMail() throws Exception{


        //int ct = qqmemberMapper.selectCount(new Qqmember());
        for(int i=450; i< 500; i+=50){
            try{
                RowBounds rb = new RowBounds(i, 50);
                List list =  qqmemberMapper.selectByRowBounds(new Qqmember(), rb);

                for(int j=0; null!=list && j<list.size(); j++){
                    log.info(list.get(j));
                    log.info(list.get(j).getClass().getCanonicalName());
                    log.info(list.get(j).getClass().getClassLoader());
                    log.info(Qqmember.class.getClassLoader());



                    try{
                        Qqmember m = new Qqmember();
                        BeanUtils.copyProperties(list.get(j), m);

                        String nick = m.getNick();
                        long qq = m.getUin();

                        String mailcontent = "<table cellpadding=\"0\" align=\"center\" cellspacing=\"0\" style=\" width:100%;table-layout:fixed;height:643px;background-image:url(https://imgsa.baidu.com/fex/pic/item/0bd162d9f2d3572c614502cc8613632762d0c37f.jpg);background-color:#857f7b \" class=\"i\" unselectable=\"on\"> <tbody> <tr> <td style=\"text-align: left;\" valign=\"top\" id=\"QQMAILSTATIONERY\"> <blockquote style=\"color: rgb(167, 104, 89); margin: 0px 0px 0px 40px; border: none; padding: 0px; line-height: 2; display: inline; font-family: 幼圆; background-color: rgba(0, 0, 0, 0); font-weight: 400; font-style: normal; font-size: large;\"> "+nick+"，您好： <br> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 诚挚邀请您加入 <a target=\"_blank\" href=\"http://ustar.dpsoft.top/desc.html\"> 游大咖 </a> ，游大咖是最好的旅游咨询平台 <br> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 把问题留给专业人士，您只需负责享受； <br> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 您也可以申请成为“大咖”，您知道的正是他人所需要的，轻轻松松赚到钱； 愿意帮助他人的人，永远是最难得的最可爱的人； </blockquote> <div style=\"color: rgb(167, 104, 89); text-align: left;\"> </div> <div style=\"color: rgb(167, 104, 89);\"> <span style=\"background-color: initial;\"> <font size=\"5\" face=\"幼圆\"> <br> </font> </span> </div> <blockquote style=\"color: rgb(167, 104, 89); margin: 0px 0px 0px 40px; border: none; padding: 0px;line-height: 2;\"><div style=\"\"><span style=\"background-color: initial;\"><font face=\"幼圆\" size=\"2\"> 期盼着您的加入！<br> 祝：身体健康 万事如意 </font></span></div></blockquote><div style=\"text-align: center; color: rgb(167, 104, 89); font-size: 16px;\"><font face=\"幼圆\">&nbsp;</font><img src=\"https://imgsa.baidu.com/fex/pic/item/a50f4bfbfbedab6400fa9e06fc36afc378311e6f.jpg\" modifysize=\"50%\" diffpixels=\"8px\" style=\"color: rgb(0, 0, 0); text-align: left; font-size: 14px; width: 172px; height: 172px;\"><span style=\"font-family: 幼圆;\">&nbsp; &nbsp;</span></div><div style=\"text-align: center;\"><span style=\"font-family: 幼圆; font-size: x-large; color: rgb(255, 0, 0); background-color: rgb(255, 255, 0);\">关注微信公众号加入“游大咖”</span></div> </td> </tr> <tr> <td style=\"height:10px;\"> </td> </tr> </tbody> </table>";
                        try{
                        MailUtil.sendHtmlMessage("smtp.dpsoft.top", "ustar@dpsoft.top", "HoogerWang2018", qq+"@qq.com","和我一起加入游大咖",mailcontent);
                        }catch (Exception e){
                            log.error("", e);
                        }
                        Thread.sleep(new Random().nextInt(20)*1000);

                    }catch (Exception e){
                        log.error("", e);
                    }
                }
            }catch (Exception e){
                log.error("", e);
            }
        }


        return "ok";

    }


    public static void main(String[]args) {
        // new QQController().crawGroupMember(699716606, 0,100, 0,"2069957448");

        SpringApplication.run(QQController.class, args);
    }
}
