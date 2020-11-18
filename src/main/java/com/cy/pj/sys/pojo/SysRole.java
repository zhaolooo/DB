package com.cy.pj.sys.pojo;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
/**
 * 	基于此对象封装角色信息
 * @author pc
 */
@Data
public class SysRole implements Serializable{
	private static final long serialVersionUID = -8557710039441592130L;
	 private Integer id;
	 private String name;
	 private String note;
	 private Date createdTime;
	 private Date modifiedTime;
	 private String createdUser;
	 private String modifiedUser;
}
