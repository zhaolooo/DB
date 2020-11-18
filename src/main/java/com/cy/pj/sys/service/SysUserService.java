package com.cy.pj.sys.service;

import java.util.Map;

import com.cy.pj.common.pojo.PageObject;
import com.cy.pj.sys.pojo.SysUser;
import com.cy.pj.sys.pojo.SysUserDept;

public interface SysUserService {
	
	
	
	Map<String,Object> findObjectById(Integer id);
	
	int updateObject(SysUser entity,Integer[]roleIds);
	
    int saveObject(SysUser entity,Integer[]roleIds);
    
	int validById(Integer id,Integer valid);

	PageObject<SysUserDept> findPageObjects(String username,Long pageCurrent);
	
	int updatePassword(String password,
	           String newPassword,
	           String cfgPassword);

}
