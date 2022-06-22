package com.itheima.reggie.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * @author
 * @version 1.0.0
 * Create by 2022/5/8 22:46
 * description
 */
// @Service
public class SendMailServiceImpl{


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
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from+"(是吖生呀)");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(context);

        javaMailSender.send(message);
    }
}
