package com.cy.pj.sys.service.impl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.thymeleaf.util.TextUtils;

import com.cy.pj.common.annotation.RequiredLog;
import com.cy.pj.common.exception.ServiceException;
import com.cy.pj.common.pojo.PageObject;
import com.cy.pj.common.utils.AssertUtils;
import com.cy.pj.sys.dao.SysUserDao;
import com.cy.pj.sys.dao.SysUserRoleDao;
import com.cy.pj.sys.pojo.SysUser;
import com.cy.pj.sys.pojo.SysUserDept;
import com.cy.pj.sys.service.SysUserService;

/*@Transactional 描述类表示类中所有方法都要进行事务控制,
       假如方法上也有这个注解则方法上的事务注解特性优先级比较高.
   1)readOnly属性
   1.1)含义是什么?是否为只读事务(只读事务只允许查询操作)
   1.2)默认值是什么?(false)你怎么知道的?
   2)rollbackFor属性
   1.1)含义是什么?(什么异常回滚事务)
   1.2)默认值是什么?(RuntimeException与Error,但是检查异常不回滚)
   3)noRollbackFor属性
   1.1)含义是什么?什么情况下不回滚
   1.2)默认值是什么?没有默认
   
   4)timeout 属性
   4.1)含义是什么?是否支持事务超时
   4.2)默认值是什么?-1,表示不支持事务超时,我们可以给定一个时间
   
   
 */
//@Slf4j
@Transactional(readOnly = false,
               rollbackFor = Exception.class,
               timeout = 60,
               isolation = Isolation.READ_COMMITTED)
@Service
public class SysUserServiceImpl implements SysUserService {

	@Autowired
	private SysUserDao sysUserDao;
	
	@Autowired
	private SysUserRoleDao sysUserRoleDao;
	
	
	@Override
	public int updatePassword(String password, String newPassword, String cfgPassword) {
				//1.判定新密码与密码确认是否相同
				if(StringUtils.isEmpty(newPassword))
				throw new IllegalArgumentException("新密码不能为空");
				if(StringUtils.isEmpty(cfgPassword))
				throw new IllegalArgumentException("确认密码不能为空");
				if(!newPassword.equals(cfgPassword))
				throw new IllegalArgumentException("两次输入的密码不相等");
				//2.判定原密码是否正确
				if(StringUtils.isEmpty(password))
				throw new IllegalArgumentException("原密码不能为空");
				//获取登陆用户
				SysUser user=(SysUser)SecurityUtils.getSubject().getPrincipal();
				SimpleHash sh=new SimpleHash("MD5",
				password, user.getSalt(), 1);
				if(!user.getPassword().equals(sh.toHex()))
				throw new IllegalArgumentException("原密码不正确");
			 
			   String passRegex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$";
			   if(StringUtils.isEmpty(newPassword) && newPassword.matches(passRegex)) throw
			     new IllegalArgumentException("字母和数字的");
			  
				//3.对新密码进行加密
				String salt=UUID.randomUUID().toString();
				sh=new SimpleHash("MD5",newPassword,salt, 1);
				//4.将新密码加密以后的 结果更新到数据库
				int rows=sysUserDao.updatePassword(sh.toHex(), salt,user.getId());
				if(rows==0)
				throw new ServiceException("修改失败");
				return rows;

	}
	
	@Transactional(rollbackFor = IllegalArgumentException.class)//由此注解描述的方法为一个事务切入点方法,后续运行时会对它描述的描述的方法进行事务控制
	@RequiredLog(value="禁用启用")//此注解描述的方法为一个日志切入点方法
	@Override
	public int validById(Integer id, Integer valid){
		//1.参数校验
		AssertUtils.isArgValid(id==null||id<1, "参数值无效");
		AssertUtils.isArgValid(valid!=0&&valid!=1, "状态值无效");
		//2.修改用户状态
		int rows=sysUserDao.validById(id, valid,"admin");//这里的admin先假设为登录用户
		//AssertUtils.isServiceValid(rows==1, "记录可能已经不存在");
		if(rows==0)
			throw new ServiceException("记录可能已经不存在");
		return rows;
	}
	
	
	@Transactional(readOnly = true) //所有的查询方法建议readOnly=true (性能会比较高)
	@Override
	public Map<String, Object> findObjectById(Integer id) {
		//long t1=System.currentTimeMillis();//直接在类方法中添加代码会违背开闭原则
		//1.参数校验
		AssertUtils.isArgValid(id==null||id<1,"id值无效");
		//2.查询用户以及用户对应的部门信息
		SysUserDept user=sysUserDao.findObjectById(id);
		AssertUtils.isServiceValid(user==null, "记录可能已经不存在");
		//3.查询用户对应的角色信息
		List<Integer> roleIds=sysUserRoleDao.findRoleIdsByUserId(id);
		//4.封装查询结果
		Map<String,Object> map=new HashMap<>();
		map.put("user", user);
		map.put("roleIds", roleIds);
		//long t2=System.currentTimeMillis();
		//log.info("time {}",t2-t1);
		return map;
	}
	@Transactional
	@RequiredLog(value="保存用户")
	@Override
	public int saveObject(SysUser entity, Integer[] roleIds) {
		//1.参数校验
		AssertUtils.isArgValid(entity==null, "保存对象不能为空");
		AssertUtils.isArgValid(entity.getUsername()==null||"".equals(entity.getUsername()), "用户名不能为空");
		AssertUtils.isArgValid(entity.getPassword()==null||"".equals(entity.getPassword()), "密码不能为空");
		AssertUtils.isArgValid(roleIds==null||roleIds.length==0, "必须为用户分配角色");
		//2.保存用户自身信息
		//2.1 构建salt(盐)值
		String salt=UUID.randomUUID().toString();//产生一个固定长度随机字符串
		//2.2 基于相关API对密码进行加密
		//借助spring框架中自带API对密码进行加密
		//String hashedPassword=
		//DigestUtils.md5DigestAsHex((salt+entity.getPassword()).getBytes());
		//借助shiro框架中的API对密码进行加密
		SimpleHash sh=new SimpleHash("MD5",//算法名称
				          entity.getPassword(), //未加密的密码
				          salt,//加密盐 
				          1);//这里1表示加密次数
		String hashedPassword=sh.toHex();//将加密结果转换为16进制的字符串
		entity.setSalt(salt);//为什么盐值也要保存到数据库?(登录时还要使用此salt对登录密码进行加密)
		entity.setPassword(hashedPassword);
		//2.3 将用户信息写入到数据库
		int rows=sysUserDao.insertObject(entity);
		//3.保存用户与角色关系数据
		sysUserRoleDao.insertObjects(entity.getId(), roleIds);
		return rows;
	}
	
	@Override
	public int updateObject(SysUser entity, Integer[] roleIds) {
		//1.参数校验
		AssertUtils.isArgValid(entity==null, "保存对象不能为空");
		AssertUtils.isArgValid(entity.getUsername()==null||"".equals(entity.getUsername()), "用户名不能为空");
		AssertUtils.isArgValid(roleIds==null||roleIds.length==0, "必须为用户分配角色");
		//2.保存用户自身信息
		int rows=sysUserDao.updateObject(entity);
		AssertUtils.isServiceValid(rows==0, "记录可能已经不存在了");
		//3.保存用户与角色关系数据
		sysUserRoleDao.deleteObjectsByUserId(entity.getId());
		sysUserRoleDao.insertObjects(entity.getId(), roleIds);
		return rows;
	}

	@Transactional(readOnly = true)
	@RequiredLog(value="分页查询")
	@Override
	public PageObject<SysUserDept> findPageObjects(String username, Long pageCurrent) {
		String tName=Thread.currentThread().getName();
		System.out.println("SysUserService.findPageObjects.thread.name="+tName);
		//1.参数有效性校验
		if(pageCurrent==null||pageCurrent<1)
			throw new IllegalArgumentException("页码值无效");
		//2.查询总记录数并校验
		long rowCount=sysUserDao.getRowCount(username);
		if(rowCount==0)
			throw new ServiceException("没有找到对应记录");
		//3.查询当前页记录
		int pageSize=2;
		long startIndex=(pageCurrent-1)*pageSize;
		List<SysUserDept> records=
				sysUserDao.findPageObjects(username, startIndex, pageSize);
		//4.封装查询结果并返回
		return new PageObject<>(rowCount, records, pageSize, pageCurrent);
	}

}
