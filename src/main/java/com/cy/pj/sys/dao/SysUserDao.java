package com.cy.pj.sys.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.cy.pj.sys.pojo.SysUser;
import com.cy.pj.sys.pojo.SysUserDept;

import java.util.*;
@Mapper
public interface SysUserDao {
	
	int updatePassword(String password,String salt,Integer id);
	
	
	/**
	 * 	基于用户名查找数据库中的用户对象
	 * @param username
	 * @return
	 */
	  @Select("select * from sys_users where username=#{username}")
	  SysUser findUserByUserName(String username);
	
	  /**
	   * 	基于用户id查询用户和部分信息
	   * @param id
	   * @return
	   */
	  SysUserDept findObjectById(Integer id);
	  /**
	   * 	更新用户自身信息
	   * @param entity
	   * @return
	   */
	  int updateObject(SysUser entity);
	  /**
	   * 	将用户自身信息写入到数据库
	   * @param entity
	   * @return
	   */
	  int insertObject(SysUser entity);
	  /**
	   * 	修改用户状态
	   * @param id
	   * @param state
	   * @param modifiedUser
	   * @return
	   */
	  @Update("update sys_users set valid=#{valid},modifiedUser=#{modifiedUser},modifiedTime=now() where id=#{id}")
	  int validById(Integer id,Integer valid,String modifiedUser);

	  /**
	   * 	基于用户名进行用户信息的模糊查询，获取总记录数
	   * @param username
	   * @return
	   */
	  long getRowCount(String username);
	  
	  /**
	   * 	基于用户名进行用户信息的模糊查询，获取当前页记录，并将
	       * 数据封装到pojo对象SysUserDept。
	   * @param username 查询条件
	   * @param startIndex 起始位置
	   * @param pageSize 页面大小
	   * @return 当前页查询到的记录
	   */
	  List<SysUserDept> findPageObjects(String username,Long startIndex,Integer pageSize);
}
