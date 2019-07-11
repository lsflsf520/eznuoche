package com.ujigu.exam.test;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)//表示整合JUnit4进行测试
@ContextConfiguration(locations={
		"classpath:context/spring-dataSource.xml",
		"classpath:context/spring-dict.xml",
		"classpath:context/spring-persist.xml"})//加载spring配置文件
public class BaseJunit4Test {
	
}
