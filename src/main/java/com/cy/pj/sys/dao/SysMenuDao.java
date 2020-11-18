package com.cy.pj.sys.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.cy.pj.common.pojo.Node;
import com.cy.pj.common.pojo.SysUserMenu;
import com.cy.pj.sys.pojo.SysMenu;

import java.util.*;
@Mapper
public interface SysMenuDao {
	
	List <String> findPermissions(@Param("menuIds")List<Integer> menuIds);

	
	int updateObject(SysMenu entity);
	int insertObject(SysMenu entity);
	 /**
	  * 从菜单表查询菜单id,名称,上级菜单id
	  * @return
	  */
	 @Select("select id,name,parentId from sys_menus")
	 List<Node> findZtreeMenuNodes();
	
	/**
	 * 	基于id删除当前菜单对象
	 * @param id
	 * @return
	 */
	 @Delete("delete from sys_menus where id=#{id}")
	 int deleteObject(Integer id);
	 
	 /**
	  * 	基于id获取当前菜单对应的子菜单个数
	  * @param id 菜单id
	  * @return 子菜单的个数
	  */
	 @Select("select count(*) from sys_menus where parentId=#{id}")
	 int getChildCount(Integer id);
	 
	 /**
	  *  	基于菜单获取菜单信息
	  * @param menuIds
	  * @return
	  */
	 List<SysUserMenu> findMenusByIds(List<Integer> menuIds);
	 
	 
	 /**
	  * 	查询所有菜单信息以及这个菜单对应的上级菜单信息
	  * @return
	  */
	 List<SysMenu> findObjects();
}
