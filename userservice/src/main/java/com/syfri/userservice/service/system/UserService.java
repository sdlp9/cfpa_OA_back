package com.syfri.userservice.service.system;

import com.syfri.baseapi.service.BaseService;
import com.syfri.userservice.model.system.AccountVO;
import com.syfri.userservice.model.system.RoleVO;
import com.syfri.userservice.model.system.UserVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface UserService  extends BaseService<UserVO>{

	/*--查询：获取用户，如果userVO为null，则获取所有用户.--*/
	List<UserVO> doFindUserRoles(UserVO userVO);

	/*--新增：增加用户并为用户赋予角色.--*/
	UserVO doInsertUserRoles(UserVO userVO);

	/*--修改：修改用户并修改用户角色.--*/
	UserVO doUpdateUserRoles(UserVO userVO);

	/*--删除：删除用户.--*/
	int doDeleteUserRoles(List<UserVO> list);
}