package com.itheima.reggie.utils;

import java.util.UUID;

/**
 * @author upwind
 * @version 1.0.0
 * Create by 2022/6/7 21:54
 * description
 */
public class UUIDUtil {

	public static String generateImgFileName(String fileName) {

		String uuid = UUID.randomUUID().toString();
		fileName = uuid + fileName.substring(fileName.lastIndexOf("."));

		return fileName;
	}

	public static String generateFileName(String fileName) {
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		fileName = uuid + "-"+ fileName.substring(0,fileName.lastIndexOf(".")) + fileName.substring(fileName.lastIndexOf("."));

		return fileName;
	}

	// public static void main(String[] args) {
	// 	String s = generateFileName("kkksd.sssssss.png");
	// 	System.out.println(s);
	//
	// 	String[] split = s.split("-");
	//
	// 	System.out.println(split[0] + "------" + split[1]);
	// }
}


