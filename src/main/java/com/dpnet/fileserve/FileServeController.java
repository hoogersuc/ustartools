///**
// *
// */
//package com.dpnet.fileserve;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.UUID;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import lombok.Cleanup;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.alibaba.fastjson.JSONObject;
//import org.springframework.web.multipart.MultipartHttpServletRequest;
//import org.springframework.web.multipart.commons.CommonsMultipartResolver;
//
///**
// * Created by ahu on 2017年7月11日
// *
// */
//@RestController
//@EnableAutoConfiguration
//@Log4j2
//public class FileServeController {
//
//    @Value("${fileserve.path}")
//    private String filepath;
//
//
//
// 	@RequestMapping("/upload")
// 	@ResponseBody
//    public String upload( HttpServletRequest request) throws Exception {
//
//        long startTime = System.currentTimeMillis();
// 		try {
//
//
//            //将当前上下文初始化给  CommonsMutipartResolver （多部分解析器）
//            CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
//                    request.getSession().getServletContext());
//            //检查form中是否有enctype="multipart/form-data"
//            if (multipartResolver.isMultipart(request)) {
//                //将request变成多部分request
//                MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
//                //获取multiRequest 中所有的文件名
//                Iterator iter = multiRequest.getFileNames();
//
//                while (iter.hasNext()) {
//                    //一次遍历所有文件
//                    MultipartFile file = multiRequest.getFile(iter.next().toString());
//                    if (file != null) {
//                        String path = filepath;
//                        log.info("本地path:" + path);
//                        String fileName = file.getOriginalFilename();
//                        log.info("fileName=" + fileName);
//                        String newfileName = fileName;
//                        File targetFile = new File(path, newfileName);
//
//
//                        //保存
//                        file.transferTo(targetFile);
//
//
//                    }
//
//                }
//
//            }
//            return "success";
//        }catch(Exception e){
//            log.error("", e);
// 		    return "error";
//        }finally {
//            long  endTime=System.currentTimeMillis();
//            log.info(String.format("耗时：[%sms]", endTime-startTime));
//        }
//
//
//
//    }
//
//    @RequestMapping("/download/{name}")
//    public void download(@PathVariable String name, HttpServletRequest request, HttpServletResponse response)throws Exception {
//        String path = filepath;
//
//        String fileurl =  path+File.separator+name;
//
//        @Cleanup InputStream input = new FileInputStream(fileurl);
//        @Cleanup OutputStream output = response.getOutputStream();
//        byte[] buf = new byte[1024];
//        int len = -1;
//        while((len = input.read(buf)) > -1){
//            output.write(buf, 0, len);
//        }
//    }
//
//
//	public static void main(String[] args) throws Exception {
//		SpringApplication.run(FileServeController.class, args);
//	}
//
//}
