package com.cy.pj.sys.service.impl;
import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cy.pj.common.exception.ServiceException;
import com.cy.pj.common.pojo.PageObject;
import com.cy.pj.sys.dao.SysLogDao;
import com.cy.pj.sys.pojo.SysLog;
import com.cy.pj.sys.service.SysLogService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

@Service
public class SysLogServiceImpl implements SysLogService {
	
	@Autowired
	private SysLogDao sysLogDao;
	//将来希望此业务方法参与到其它事务中执行,传播特性设置为Propagation.REQUIRED
	//将来希望此业务方法始终运行在一个独立事务中,传播特性设置为Propagation.REQUIRES_NEW
	//将写日志操作放在一个独立的事务
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Async //此注解描述的方法会运行在spring框架提供的一个线程中
	@Override
	public void saveObject(SysLog entity) {
		String tName=Thread.currentThread().getName();
		System.out.println("SysLogService.saveObject.thread.name="+tName);
		try{Thread.sleep(2000);}catch(Exception e) {}
	    sysLogDao.insertObject(entity);
	}
	
	@RequiresPermissions("sys:log:delete")
	@Override
	public int deleteObjects(Integer... ids) {
	    if(ids==null||ids.length==0)
	    	throw new IllegalArgumentException("请输入id的值");
	    int rows=sysLogDao.deleteObjects(ids);
	    if(rows==0)
	    	throw new ServiceException("记录可能已经不存在");
		return rows;
	}
	
	@Override
	public PageObject<SysLog> findPageObjects(String username, Long pageCurrent) {
		//验证参数的有效性
		if(pageCurrent==null||pageCurrent<1)
			throw new IllegalArgumentException("当前页码值无效");//e.message="当前页码值无效"
		int  pageSize=5;//(这个值也可以从页面传递到服务端)
		//设置查询位置以及页面大小
		Page<SysLog> page=PageHelper.startPage(pageCurrent.intValue(), pageSize);
		//查询当前页数据
		List<SysLog> records=sysLogDao.findPageObjects(username);
		return new PageObject<>(page.getTotal(), records,pageSize, pageCurrent);
	}
	
}
