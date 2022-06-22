package com.itheima.reggie.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * @author upwind
 * @version 1.0.0
 * Create by 2022/6/13 12:36
 * description
 */
@Component
public class EmailUtils {

	@Autowired
	private JavaMailSender javaMailSender;

	@Value("${spring.mail.username}")
	private String sourceMail;

	// 标题
	private String subject = "【瑞吉外卖】";

	// private String context = ;

	public void sendMailCode(String targetMail,String authCode) {
		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(sourceMail+"(是吖生呀)");
		message.setTo(targetMail);
		message.setText("【瑞吉外卖】您的验证码为"+ authCode
				+"，尊敬的客户，以上验证码3分钟内有效，请妥善保管！");
		message.setSubject(subject);
		javaMailSender.send(message);
	}
}
