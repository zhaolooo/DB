package com.cy.pj.common.web;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;

import com.cy.pj.common.exception.ServiceException;

/**
 * 	在spring框架中所有实现了HabdlerInterceptor的对象都是spring mvc拦截器对象
 * @author tarena
 *
 */
public class TimeAccessInterceptor implements HandlerInterceptor{

	/**
	 * 	此方法在@Controller描述对象方法执行之前执行
	 * 	@return true 表示请求放行、false表示请求结束
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		System.out.println("preHandler()");
		//获取当前的时间  JDK8的新特性
		LocalDateTime localDateTime = LocalDateTime.now();
		//获取当前对应的小时
		int hour = localDateTime.getHour();
		if(hour<=9 || hour>=20) {
			throw new ServiceException("请在9:00~20:00之间访问");
		}
		 return true;
		
		
		//		JDK8之前的时间API
		//获取java中的日历对象
//		Calendar c=Calendar.getInstance();
//		c.set(Calendar.HOUR_OF_DAY, 6);
//		c.set(Calendar.MINUTE, 0);
//		c.set(Calendar.SECOND, 0);
//		long start=c.getTimeInMillis();
//		c.set(Calendar.HOUR_OF_DAY,23);
//		long end=c.getTimeInMillis();
//		long cTime=System.currentTimeMillis();
//		if(cTime<start||cTime>end)
//		throw new ServiceException("不在访问时间之内");
 		
	}

}
