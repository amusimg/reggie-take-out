package com.itheima.reggie.controller;

import com.itheima.reggie.common.Result;
import com.itheima.reggie.utils.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author upwind
 * @version 1.0.0
 * Create by 2022/6/7 21:24
 * description
 */
@RestController
@RequestMapping("common")
@Slf4j
public class CommonController {

	// @Value("${reggie.base-path}")
	private String basePath;

	@PostMapping("/upload")
	public Result<String> upload(HttpServletRequest req,MultipartFile file) {
		// file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
		log.info("新上传文件: {}",file.toString());

		String path1 = req.getServletContext().getRealPath("/");

		String path2 = CommonController.class.getResource("/static/").getPath() + "images/";
		String path3 = "";
		try {
			path3 = new File("src/main/resources").getCanonicalPath()+"\\static\\images\\";
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.info("文件上传路径为\n{},\n{}\n{}",path1,path2,path3);
		// 原始文件名
		String originalFilename = file.getOriginalFilename();//abc.jpg
		String fileName = UUIDUtil.generateImgFileName(originalFilename);

		// 创建一个目录对象
		File dir = new File(path3);
		// 判断当前目录是否存在
		if(!dir.exists()){
			// 目录不存在，需要创建
			dir.mkdirs();
		}

		try {
			// 将临时文件转存到指定位置
			file.transferTo(new File(path3 + fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Result.success(fileName);
	}

	/**
	 * 文件下载
	 */
	@GetMapping("/download")
	public void download(String name, HttpServletRequest req,HttpServletResponse response){
		String path1 = req.getServletContext().getRealPath("/");
		String path2 = CommonController.class.getResource("/static/").getPath() + "images/";
		String path3 = "";
		try {
			path3 = new File("src/main/resources").getCanonicalPath()+"\\static\\images\\";
			//输入流，通过输入流读取文件内容
			FileInputStream fileInputStream = new FileInputStream(new File(path3 + name));

			//输出流，通过输出流将文件写回浏览器
			ServletOutputStream outputStream = response.getOutputStream();

			response.setContentType("image/jpeg");
			int len = 0;
			byte[] bytes = new byte[1024]; // 把文件写到bytes数组里
			while ((len = fileInputStream.read(bytes)) != -1){
				outputStream.write(bytes,0,len);
				outputStream.flush();
			}

			// 关闭资源
			outputStream.close();
			fileInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
