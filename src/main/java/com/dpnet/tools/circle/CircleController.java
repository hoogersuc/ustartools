package com.dpnet.tools.circle;

/**
 * Created by hooger on 2018/4/5.
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dpnet.tools.qq.domain.model.Sinaweibo;
import com.dpnet.tools.qq.domain.model.StarDynamic;
import com.dpnet.tools.qq.domain.model.User;
import com.dpnet.tools.qq.domain.repository.SinaweiboMapper;
import com.dpnet.tools.qq.domain.repository.StarDynamicMapper;
import com.dpnet.tools.qq.domain.repository.UserMapper;
import com.dpnet.utils.HttpHelper;
import com.dpnet.utils.RequestResult;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

@RestController
@EnableAutoConfiguration
@Log4j2
@MapperScan(basePackages="com.dpnet.tools.qq.domain.repository")
@RequestMapping("circle")
public class CircleController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SinaweiboMapper sinaweiboMapper;

    @Autowired
    private StarDynamicMapper starDynamicMapper;

    @RequestMapping("publish")
    public String publishCircle(@RequestParam("num") int num){

        int ct = sinaweiboMapper.selectCount(new Sinaweibo());

        while(num-- > 0){
            try {
                int id = new Random().nextInt(ct);
                Sinaweibo sw = new Sinaweibo();
                sw.setId(id);
                Object to = sinaweiboMapper.selectOne(sw);

                BeanUtils.copyProperties(to, sw);

                User user = new User();
                user.setUid(sw.getUid());
                Object uobj = userMapper.selectOne(user);
                if(uobj == null){
                    user = new User();
                    user.setUid(sw.getUid());
                    user.setBalance(new BigDecimal("0"));
                    user.setGender(2);
                    user.setHead_img("http:"+sw.getHeadimg());
                    user.setMinopenid(sw.getUid());
                    user.setName(sw.getNick());
                    user.setNick_name(sw.getNick());
                    user.setOpenid(sw.getUid());
                    user.setUnionid(sw.getUid());
                    user.setStar_state(0);
                    userMapper.insert(user);
                }else{
                    BeanUtils.copyProperties(uobj, user);
                }

                StarDynamic dynamic = new StarDynamic();
                dynamic.setCreatetime(new Date());
                dynamic.setDynamic_txt(sw.getWeibotxt().replace("...展开全文c","").trim());
                dynamic.setUnionid(user.getUnionid());

                String imgspath = sw.getWeiboimg();

                String dpimgpath  = "";
                if(StringUtils.isNotEmpty(imgspath)){
                    String[] imgpathArray = imgspath.split(";");

                    for (String imgpath: imgpathArray){

                        imgpath = imgpath.replace("square","bmiddle");

                        byte[] imgbytes =  HttpHelper.download("http:"+imgpath,new HashMap<String, Object>(), new HashMap());

                        File file = new File(imgpath.substring(imgpath.lastIndexOf("/")+1));
                        file.createNewFile();
                        FileOutputStream fo = new FileOutputStream(file);
                        fo.write(imgbytes);
                        fo.close();
                        RequestResult rt = HttpHelper.doPost("https://ustar.dpcloudx.com/upload.jsp",file,new HashMap<String, Object>(), new HashMap());
                        file.delete();
                        if(StringUtils.isNotEmpty(rt.getResult())){
                            JSONObject obj = JSON.parseObject(rt.getResult());
                            if("ok".equals(obj.getString("status"))){
                                dpimgpath+=obj.getString("data")+",";
                            }
                        }
                    }
                }

                if(dpimgpath.length()>0){
                    dpimgpath = dpimgpath.substring(0,dpimgpath.length()-1);
                }


                dynamic.setDynamic_img(dpimgpath);

                int dynamicId = starDynamicMapper.insert(dynamic);

                log.info(dynamicId+":"+dynamic);
            }catch (Exception e){
                log.error("", e);
            }
        }

        return "ok";
    }


    public static void main(String[]args) {
        // new QQController().crawGroupMember(699716606, 0,100, 0,"2069957448");

        SpringApplication.run(CircleController.class, args);
    }
}
