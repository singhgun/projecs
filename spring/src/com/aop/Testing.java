package com.aop;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class Testing {

	public static void main(String[] args){
		//Note: config files should be in the classpath and should be loaded with ClassPathApplicationXmlContext
		ApplicationContext context = new FileSystemXmlApplicationContext("com//aop//aop.xml");
		Student student = (Student) context.getBean("student");
		System.out.println("Testing: main ");
		student.read();
		student.write("aop testing");
		student.status(false);
	}
}
