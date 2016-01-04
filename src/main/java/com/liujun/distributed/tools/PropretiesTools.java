package com.liujun.distributed.tools;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class PropretiesTools {

	/**
	 * 属性map信息
	 */
	private static Map<String, String> proMap = new HashMap<String, String>();

	static {
		// 加载文件
		loadFile("distributed/nettyzookeeper/nettyzookeeper.properties");
	}

	/**
	 * 实例信息
	 */
	private static final PropretiesTools proTools = new PropretiesTools();

	private PropretiesTools() {

	}

	/**
	 * 得到实例对象
	 * 
	 * @return
	 */
	public static PropretiesTools getProtoolInstance() {
		return proTools;
	}

	/**
	 * 加载文件
	 * 
	 * @param file
	 */
	private static void loadFile(String file) {
		Properties proper = new Properties();

		try {

			URL url = PropretiesTools.class.getClassLoader().getResource(file);

			proper.load(new FileInputStream(url.getPath()));

			for (Entry<Object, Object> iter : proper.entrySet()) {
				proMap.put(String.valueOf(iter.getKey()), String.valueOf(iter.getValue()));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getValue(String key) {
		return proMap.get(key);
	}

	public static void main(String[] args) {
		PropretiesTools proTools = new PropretiesTools();
		System.out.println(proTools.getValue("loader1.cfg"));
	}

}
