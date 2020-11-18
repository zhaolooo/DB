package com.cy.pj.sys.pojo;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class SysUserDept implements Serializable{
	private static final long serialVersionUID = 3776304095517793129L;
	private Integer id;
	private String username;
	private String password;//md5
	private String salt;
	private String email;
	private String mobile;
	private Integer valid=1;
	/**基于此对象存储部门信息*/
	private SysDept sysDept; 
	private Date createdTime;
	private Date modifiedTime;
	private String createdUser;
	private String modifiedUser;
}
