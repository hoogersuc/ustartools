package com.dpnet.utils;

import lombok.extern.log4j.Log4j2;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.util.Calendar;
import java.util.Properties;

/**
 * Created by hooger on 2018/3/22.
 */
@Log4j2
public class MailUtil {

    public static void sendFileMessage(String smtpHost, String from,
                                   String fromUserPassword, String to, String subject,
                                   byte[] attach, String contentType, String attachName) throws Exception {
        // 第一步：配置javax.mail.Session对象
        // System.out.println("为" + smtpHost + "配置mail session对象");
        //logger.info("为" + smtpHost + "配置mail session对象");



        Properties props = new Properties();
        props.setProperty("mail.smtp.host", smtpHost);
        props.put("mail.smtp.ssl.enable","true");//使用 STARTTLS安全连接
        props.setProperty("mail.smtp.port", "465");             //google使用465或587端口
        props.setProperty("mail.smtp.auth", "true");        // 使用验证
        // 发送邮件协议名称
        props.setProperty("mail.transport.protocol", "smtp");
        // 设置邮件服务器主机名
        props.setProperty("mail.host", smtpHost);
        //props.setProperty("mail.debug", "true");
        Session mailSession = Session.getInstance(props, new MyAuthenticator(from,fromUserPassword));

        // 第二步：编写消息
        log.info("编写消息from——to:" + from + "——" + to);

        InternetAddress fromAddress = new InternetAddress(from);

        MimeMessage message = new MimeMessage(mailSession);

        message.setFrom(fromAddress);
        message.addRecipient(MimeMessage.RecipientType.TO, fromAddress);

        message.setSentDate(Calendar.getInstance().getTime());
        message.setSubject(subject);

        //整封邮件的MINE消息体
        MimeMultipart msgMultipart = new MimeMultipart("mixed");//混合的组合关系

        MimeBodyPart attch1 = new MimeBodyPart();

        //把内容，附件1，附件2加入到 MINE消息体中
        msgMultipart.addBodyPart(attch1);
        //把文件，添加到附件1中
        //数据源
        //DataSource ds1 = new ByteArrayDataSource(attach, contentType);
        DataSource ds1  = new FileDataSource(new File(attachName));
        //数据处理器
        DataHandler dh1 = new DataHandler(ds1 );
        //设置第一个附件的数据
        attch1.setDataHandler(dh1);
        //设置第一个附件的文件名
        attch1.setFileName(attachName);


        //设置邮件的MINE消息体
        message.setContent(msgMultipart);



        // 第三步：发送消息
        Transport transport = mailSession.getTransport("smtp");
        transport.connect(smtpHost,from, fromUserPassword);
        transport.send(message, message.getRecipients(MimeMessage.RecipientType.TO));
        // logger.info("message yes");
    }


    public static void sendHtmlMessage(String smtpHost, String from,
                                       String fromUserPassword, String to, String subject,
                                       String content) throws Exception {
        // 第一步：配置javax.mail.Session对象
        // System.out.println("为" + smtpHost + "配置mail session对象");
        log.info("为" + smtpHost + "配置mail session对象");



        Properties props = new Properties();
        props.setProperty("mail.smtp.host", smtpHost);
        props.put("mail.smtp.ssl.enable","true");//使用 STARTTLS安全连接
        props.setProperty("mail.smtp.port", "465");             //google使用465或587端口
        props.setProperty("mail.smtp.auth", "true");        // 使用验证
        // 发送邮件协议名称
        props.setProperty("mail.transport.protocol", "smtp");
        // 设置邮件服务器主机名
        props.setProperty("mail.host", smtpHost);
        //props.setProperty("mail.debug", "true");
        Session mailSession = Session.getInstance(props, new MyAuthenticator(from,fromUserPassword));

        // 第二步：编写消息
        log.info("编写消息from——to:" + from + "——" + to);

        InternetAddress fromAddress = new InternetAddress(from);
        InternetAddress toAddress = new InternetAddress(to);

        MimeMessage message = new MimeMessage(mailSession);

        message.setFrom(fromAddress);
        message.addRecipient(MimeMessage.RecipientType.TO, toAddress);

        message.setSentDate(Calendar.getInstance().getTime());
        message.setSubject(subject);

        //整封邮件的MINE消息体
        MimeMultipart msgMultipart = new MimeMultipart("mixed");//混合的组合关系



        // 创建一个包含HTML内容的MimeBodyPart
        BodyPart html = new MimeBodyPart();
        // 设置HTML内容
        html.setContent(content, "text/html; charset=utf-8");
        msgMultipart.addBodyPart(html);
        // 将MiniMultipart对象设置为邮件内容
        message.setContent(msgMultipart);



        // 第三步：发送消息
        //Transport transport = mailSession.getTransport("smtp");
        //transport.connect(smtpHost,from, fromUserPassword);
        //transport.send(message, message.getRecipients(MimeMessage.RecipientType.TO));
        Transport.send(message);
        log.info("message yes");
    }


    public static void main(String[] args) throws  Exception{
        String mailcontent = "<table cellpadding=\"0\" align=\"center\" cellspacing=\"0\" style=\" width:100%;table-layout:fixed;height:643px;background-image:url(https://imgsa.baidu.com/fex/pic/item/0bd162d9f2d3572c614502cc8613632762d0c37f.jpg);background-color:#857f7b \" class=\"i\" unselectable=\"on\"> <tbody> <tr> <td style=\"text-align: left;\" valign=\"top\" id=\"QQMAILSTATIONERY\"> <blockquote style=\"color: rgb(167, 104, 89); margin: 0px 0px 0px 40px; border: none; padding: 0px; line-height: 2; display: inline; font-family: 幼圆; background-color: rgba(0, 0, 0, 0); font-weight: 400; font-style: normal; font-size: large;\"> XXX您好： <br> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 诚挚邀请您加入 <a target=\"_blank\" href=\"http://dp-ustar.legendh5.com/h5/joinustar.html\"> 游大咖 </a> ，游大咖是最好的旅游咨询平台 <br> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 把问题留给专业人士，您只需负责享受； <br> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 您也可以申请成为“大咖”，您知道的正是他人所需要的，轻轻松松赚到钱； 愿意帮助他人的人，永远是最难得的最可爱的人； </blockquote> <div style=\"color: rgb(167, 104, 89); text-align: left;\"> </div> <div style=\"color: rgb(167, 104, 89);\"> <span style=\"background-color: initial;\"> <font size=\"5\" face=\"幼圆\"> <br> </font> </span> </div> <blockquote style=\"color: rgb(167, 104, 89); margin: 0px 0px 0px 40px; border: none; padding: 0px;line-height: 2;\"><div style=\"\"><span style=\"background-color: initial;\"><font face=\"幼圆\" size=\"2\"> 期盼着您的加入！<br> 祝：身体健康 万事如意 </font></span></div></blockquote><div style=\"text-align: center; color: rgb(167, 104, 89); font-size: 16px;\"><font face=\"幼圆\">&nbsp;</font><img src=\"https://imgsa.baidu.com/fex/pic/item/a50f4bfbfbedab6400fa9e06fc36afc378311e6f.jpg\" modifysize=\"50%\" diffpixels=\"8px\" style=\"color: rgb(0, 0, 0); text-align: left; font-size: 14px; width: 172px; height: 172px;\"><span style=\"font-family: 幼圆;\">&nbsp; &nbsp;</span></div><div style=\"text-align: center;\"><span style=\"font-family: 幼圆; font-size: x-large; color: rgb(255, 0, 0); background-color: rgb(255, 255, 0);\">关注微信公众号加入“游大咖”</span></div> </td> </tr> <tr> <td style=\"height:10px;\"> </td> </tr> </tbody> </table>";
        sendHtmlMessage("smtp.dpsoft.top", "ustar@dpsoft.top", "HoogerWang2018", "wangjh1111@foxmail.com","和我一起加入游大咖",mailcontent);
    }
}


class MyAuthenticator extends Authenticator {
    String userName="";
    String password="";
    public MyAuthenticator(){

    }
    public MyAuthenticator(String userName,String password){
        this.userName=userName;
        this.password=password;
    }
    protected PasswordAuthentication getPasswordAuthentication(){
        return new PasswordAuthentication(userName, password);
    }
}
