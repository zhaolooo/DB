package com.cy.pj.common.pojo;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class SysUserMenu implements  Serializable{
 
	private static final long serialVersionUID = 4430852847707200409L;

	private Integer id;
	private String name;
	private String url;
	private List<SysUserMenu> Childs;
}
