package com.itheima.reggie.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * @author
 * @version 1.0.0
 * Create by 2022/5/8 22:46
 * description
 */
@Service
public class SendMailServiceImpl2{


    @Autowired
    private JavaMailSender javaMailSender;

    // 发送者
    private String from = "3133145320@qq.com";
    // 接收者
    private String to = "1149444930@qq.com";

    // 标题
    private String subject = "测试邮件";

    // 正文
    private String context = "测试邮件正文内容";


    public void sendMail() {

        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message,true);

            helper.setFrom(from+"(是吖生呀)");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(context);

            File f1 = new File("E:\\Itcast\\springboot\\code\\springboot\\server.log");
            File f2 = new File("E:\\Itcast\\springboot\\code\\springboot\\18-springboot-mail\\src\\main\\resources\\logo.png");

            helper.addAttachment("日志文件",f1);
            helper.addAttachment("黑马Logo.png",f2);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        javaMailSender.send(message);
    }
}
