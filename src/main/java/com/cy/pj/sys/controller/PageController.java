package com.cy.pj.sys.controller;

import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cy.pj.common.pojo.SysUserMenu;
import com.cy.pj.common.utils.ShiroUtils;
import com.cy.pj.sys.pojo.SysUser;
import com.cy.pj.sys.service.SysMenuService;
import com.cy.pj.sys.service.SysUserService;

/**
 * 	基于此Controller处理所有页面请求
 */
@RequestMapping("/")
@Controller
public class PageController {
	
	@RequestMapping("doLoginUI")
	public String doLoginUI() {
		return "login";
	}
	//http://localhost/log/log_list
	//http://localhost/menu/menu_list
	//rest风格的url,{}代表是变量表达式
	
	@RequestMapping("{module}/{moduleUI}")
	public String doModuleUI(@PathVariable String moduleUI) {
		return "sys/"+moduleUI;
	}
	
//	@RequestMapping("menu/menu_list")
//	public String doMenuUI() {
//		return "sys/menu_list";
//	}
//
//	@RequestMapping("log/log_list")
//	public String doLogUI() {
//		return "sys/log_list";
//	}
	

	@RequestMapping("doPageUI")
	public String doPageUI() {
		return "common/page";
	}

	@Autowired
	private SysMenuService sysMenuService;
	@RequestMapping("doIndexUI")
	 public String doIndexUI(Model model) {
		//从shiro框架中的seesion对象中的取出登陆用户的信息
		//将用户存储到model中
		SysUser user=ShiroUtils.getUser();
		model.addAttribute("user",user);
		model.addAttribute("username", user.getUsername());
		//获取用户由访问权限
		List<SysUserMenu> userMenus=sysMenuService.findUserMenusByUserId(user.getId());
		model.addAttribute("userMenus",userMenus);
		return "starter";
	 }

	
	
	
}
