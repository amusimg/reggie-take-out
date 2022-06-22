package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.Result;
import com.itheima.reggie.domain.User;
import com.itheima.reggie.domain.UserMail;
import com.itheima.reggie.service.UserMailService;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.EmailUtils;
import com.itheima.reggie.utils.SMSUtils;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author upwind
 * @version 1.0.0
 * Create by 2022/6/12 23:07
 * description
 */
@RestController
@RequestMapping("user")
@Slf4j
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private UserMailService userMailService;

	@Autowired
	private EmailUtils emailUtils;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	/**
	 * 发送手机短信验证码
	 * @param user
	 * @return
	 */
	@PostMapping("/sendMsg")
	public Result<String> sendMsg(@RequestBody User user, HttpSession session){
		//获取手机号
		String phone = user.getPhone();

		if(StringUtils.isNotEmpty(phone)){
			//生成随机的4位验证码
			String code = ValidateCodeUtils.generateValidateCode(4).toString();
			log.info("code={},phone={}",code,phone);

			//调用阿里云提供的短信服务API完成发送短信
			// SMSUtils.sendMessage("瑞吉外卖","",phone,code);

			//需要将生成的验证码保存到Session
			session.setAttribute(phone,code);
			return Result.success("手机验证码短信发送成功");
		}

		return Result.error("短信发送失败");
	}

	/**
	 * 移动端用户登录
	 * @param map
	 * @param session
	 * @return
	 */
	@PostMapping("/login")
	public Result<User> login(@RequestBody Map map, HttpSession session){
		log.info("数据封装{}",map.toString());
		//获取手机号
		String phone = map.get("phone").toString();

		//获取验证码
		String code = map.get("code").toString();

		//从Session中获取保存的验证码
		Object codeInSession = session.getAttribute(phone);

		//进行验证码的比对（页面提交的验证码和Session中保存的验证码比对）
		if(codeInSession != null && codeInSession.equals(code)){
			//如果能够比对成功，说明登录成功

			LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
			queryWrapper.eq(User::getPhone,phone);

			User user = userService.getOne(queryWrapper);
			if(user == null){
				//判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
				user = new User();
				user.setPhone(phone);
				user.setStatus(1);
				userService.save(user);
			}
			session.setAttribute("user",user.getId());
			return Result.success(user);
		}
		return Result.error("验证码已失效或错误");
	}

	@PostMapping("/logout")
	public Result<String> logout(HttpServletRequest req) {
		// req.getSession().removeAttribute("user");
		req.getSession().invalidate();
		return Result.success("已退出登录");
	}







	/**
	 * 发送邮箱验证码
	 */
	// @PostMapping("/sendMsg")
	// public Result<String> sendMail(@RequestBody UserMail userMail, HttpSession session){
	//
	// 	log.info("邮件发送",userMail);
	// 	//获取手机号
	// 	String email = userMail.getEmail();
	//
	// 	if(StringUtils.isNotEmpty(email)){
	// 		//生成随机的4位验证码
	// 		String code = ValidateCodeUtils.generateValidateCode(4).toString();
	// 		log.info("code={},email={}",code,email);
	//
	// 		//调用邮箱API完成发送邮件
	// 		emailUtils.sendMailCode(email,code);
	//
	// 		//需要将生成的验证码保存到Session
	// 		session.setAttribute(email,code);
	// 		session.setMaxInactiveInterval(60);
	// 		return Result.success("验证码已成功发送到您的邮箱");
	// 	}
	// 	return Result.error("验证码发送失败");
	// }
	//
	//
	// @PostMapping("/login")
	// public Result<UserMail> loginMail(@RequestBody Map map, HttpSession session){
	//
	// 	log.info("邮件数据封装{}",map.toString());
	// 	//获取手机号
	// 	String email = map.get("email").toString();
	//
	// 	//获取验证码
	// 	String code = map.get("code").toString();
	//
	// 	//从Session中获取保存的验证码
	// 	Object codeInSession = session.getAttribute(email);
	//
	// 	//进行验证码的比对（页面提交的验证码和Session中保存的验证码比对）
	// 	if(codeInSession != null && codeInSession.equals(code)){
	// 		//如果能够比对成功，说明登录成功
	//
	// 		LambdaQueryWrapper<UserMail> queryWrapper = new LambdaQueryWrapper<>();
	// 		queryWrapper.eq(UserMail::getEmail,email);
	//
	// 		UserMail userMail = userMailService.getOne(queryWrapper);
	// 		if(userMail == null){
	// 			//判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
	// 			userMail = new UserMail();;
	// 			userMail.setEmail(email);
	// 			userMail.setStatus(1);
	// 			userMailService.save(userMail);
	// 		}
	// 		session.setAttribute("user",userMail.getId());
	// 		return Result.success(userMail);
	// 	}
	// 	return Result.error("验证码已失效或错误");
	// }
}
