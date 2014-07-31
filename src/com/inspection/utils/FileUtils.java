package com.inspection.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import android.text.TextUtils;

public class FileUtils {

	/**
	 * 删除文件
	 * @param filePath 文件路径
	 * @return 删除文件的结果
	 */
	public static boolean deleteFile(String filePath) {
		File file = new File(filePath);
		boolean isDeleted = false;
		if (file.exists()) {
			isDeleted = file.delete();
		}
		return isDeleted;
	}

	/**
	 * 拷贝目录下的所有文件或者文件夹
	 * @param srcFilePath 源文件路径
	 * @param desFilePath 目标文件路径
	 * @return 拷贝文件的结果
	 */
	public static int copyFiles(String srcFilePath, String desFilePath) {
		File[] currentFiles;
		File root = new File(srcFilePath);
		if (!root.exists()) {
			return -1;
		}
		if (root.isFile()) {
			copyFile(root.getAbsolutePath(), desFilePath + File.separator + root.getName());
		} else {
			currentFiles = root.listFiles();
			File desFile = new File(desFilePath);
			if (!desFile.exists()) {
				desFile.mkdirs();
			}
			for (File file : currentFiles) {
				if (file.isDirectory()) {
					copyFiles(file.getAbsolutePath() + "/", desFilePath + file.getName() + "/");
				} else {
					copyFile(file.getAbsolutePath(), desFilePath + File.separator + file.getName());
				}
			}
		}		
		return 0;
	}
	
	/**
	 * 拷贝文件到另一个目录
	 * @param srcFilePath 源文件路径
	 * @param desFilePath 目标文件路径
	 * @return 拷贝文件的结果
	 */
	public static int copyFile(String srcFilePath, String desFilePath) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(srcFilePath);
			fos = new FileOutputStream(desFilePath);
			byte buffer[] = new byte[1024];
			int c;
			while ((c= fis.read(buffer)) > 0) {
				fos.write(buffer, 0, c);
			}
		} catch (FileNotFoundException e) {
			return -1;
		} catch (IOException e) {
			return -1;
		} finally {
			try {
				fis.close();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
		return 0;
	}
	
	/**
	 * 创建文件，写入指定内容
	 * @param filePath 创建文件的路径
	 * @param content 写入的文件内容
	 * @return 创建文件的结果
	 */
	public static int createFile(String filePath, String content){
		FileOutputStream fos = null;
		try {
			File file = new File(filePath);
			fos = new FileOutputStream(file);
			fos.write(content.getBytes("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			try {
				fos.flush();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}
	
	/**
	 * 判断文件是否是图片
	 * @param filePath 图片路径
	 * @return
	 */
	public static boolean isImage(String filePath) {
		String type = getFileType(filePath);
		if (!TextUtils.isEmpty(type)
				&& (type.equals("jpeg") || type.equals("jpg")
						|| type.equals("bmp") || type.equals("png"))) {
			return true;
		}
		return false;
	}
	
	/**
	 * 获取文件后缀名
	 * @param filePath 文件路径
	 * @return 文件后缀名
	 */
	public static String getFileType(String filePath) {
		String type = null;
		File file = new File(filePath);
		if (file.exists()) {
			String fileName = file.getName();
			int temp = fileName.lastIndexOf(".");
			if (temp != -1) {
				type = fileName.substring(temp + 1).toLowerCase(
						Locale.getDefault());
			}
		}
		return type;
	}
}
