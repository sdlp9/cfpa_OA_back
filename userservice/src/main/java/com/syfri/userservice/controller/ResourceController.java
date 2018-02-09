package com.syfri.userservice.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.syfri.baseapi.model.ResultVO;
import com.syfri.baseapi.utils.EConstants;
import com.syfri.userservice.model.RoleVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.syfri.userservice.model.ResourceVO;
import com.syfri.userservice.service.ResourceService;
import com.syfri.baseapi.controller.BaseController;

import java.util.List;

@Api(value = "资源管理",tags = "资源管理API",description = "资源管理")
@Controller
@RequestMapping("resource")
public class ResourceController  extends BaseController<ResourceVO>{

	@Autowired
	private ResourceService resourceService;

	@Autowired
	private Environment environment;

	@Override
	public ResourceService getBaseService() {
		return this.resourceService;
	}

	@ModelAttribute
	public void Model(Model model){
		if (environment.containsProperty("server.context-path")) {
			model.addAttribute("contextPath", environment.getProperty("server.context-path"));
		}else{
			model.addAttribute("contextPath", "/");
		}
	}

	@GetMapping("")
	public String user(Model model){
		return "system/resource_list";
	}

	/**
	 * 根据资源获取权限信息
	 */
	@ApiOperation(value="根据资源查询资源及其权限信息",notes="列表信息")
	@ApiImplicitParam(name="vo",value="资源对象")
	@RequiresPermissions("resource:list")
	@PostMapping("/findByVO")
	public @ResponseBody ResultVO findByVO(@RequestBody ResourceVO resourceVO){
		ResultVO resultVO = ResultVO.build();
		try{
			resultVO.setResult(resourceService.doFindResourcePermissions(resourceVO));
		}catch(Exception e){
			logger.error("{}",e.getMessage());
			resultVO.setCode(EConstants.CODE.FAILURE);
		}
		return resultVO;
	}

	/**
	 * 新增资源，同时新增其权限信息
	 */
	@ApiOperation(value="根据资源新增资源及其权限信息",notes="新增")
	@ApiImplicitParam(name="vo",value="资源对象")
	@RequiresPermissions("resource:add")
	@PostMapping("/insertByVO")
	public @ResponseBody ResultVO insertByVO(@RequestBody ResourceVO resourceVO){
		ResultVO resultVO = ResultVO.build();
		try{
			resultVO.setResult(resourceService.doInsertResourcePermissions(resourceVO));
		}catch(Exception e){
			logger.error("{}",e.getMessage());
			resultVO.setCode(EConstants.CODE.FAILURE);
		}
		return resultVO;
	}

	/**
	 * 修改资源，同时修改其权限信息
	 */
	@ApiOperation(value="根据资源修改资源及其权限信息",notes="修改")
	@ApiImplicitParam(name="vo",value="资源对象")
	@RequiresPermissions("resource:update")
	@PostMapping("/updateByVO")
	public @ResponseBody ResultVO updateByVO(@RequestBody ResourceVO resourceVO){
		ResultVO resultVO = ResultVO.build();
		try{
			resultVO.setResult(resourceService.doUpdateResourcePermissions(resourceVO));
		}catch(Exception e){
			logger.error("{}",e.getMessage());
			resultVO.setCode(EConstants.CODE.FAILURE);
		}
		return resultVO;
	}

	/**
	 * 删除资源，同时删除其权限信息
	 */
	@ApiOperation(value="根据主键删除资源资源及其权限信息",notes="删除")
	@ApiImplicitParam(name="id",value="资源主键")
	@RequiresPermissions("resource:delete")
	@PostMapping("/deleteByIds")
	public @ResponseBody ResultVO deleteByIds(@RequestBody String id){
		JSONObject jsonObject = JSON.parseObject(id);
		JSONArray ids = jsonObject.getJSONArray("ids");
		ResultVO resultVO = ResultVO.build();
		try{
			for(int i=0;i<ids.size();i++){
				String resourceid = (String)ids.get(i);
				resourceService.doDeleteResourcePermissions(resourceid);
			}
			resultVO.setMsg("删除成功");
		}catch(Exception e){
			logger.error("{}",e.getMessage());
			resultVO.setCode(EConstants.CODE.FAILURE);
		}
		return resultVO;
	}

	/**
	 * 根据角色获取资源树
	 */
	@ApiOperation(value="根据角色获取资源树",notes="列表信息")
	@ApiImplicitParam(name="vo",value="资源对象")
	@RequiresPermissions("resource:list")
	@GetMapping("/getResourceTree")
	public @ResponseBody ResultVO getResourceTree(List<RoleVO> roleList){
		ResultVO resultVO = ResultVO.build();
		try{
			resultVO.setResult(resourceService.getMenuTree(roleList));
		}catch(Exception e){
			logger.error("{}",e.getMessage());
			resultVO.setCode(EConstants.CODE.FAILURE);
		}
		return resultVO;
	}

	@ApiOperation(value="根据角色ID查询其资源",notes="查询")
	@ApiImplicitParam(name="id",value="角色ID")
	@GetMapping("/getResource/{roleid}")
	public @ResponseBody ResultVO getResource(@PathVariable String roleid){
		ResultVO resultVO = ResultVO.build();
		try{
			resultVO.setResult(resourceService.doFindResourceTree(roleid));
		}catch(Exception e){
			logger.error("{}",e.getMessage());
			resultVO.setCode(EConstants.CODE.FAILURE);
		}
		return resultVO;
	}

	@ApiOperation(value="获取所有资源",notes="查询")
	@GetMapping("/getAll")
	public @ResponseBody ResultVO getAll(){
		ResultVO resultVO = ResultVO.build();
		try{
			resultVO.setResult(resourceService.doFindResourceTree(""));
		}catch(Exception e){
			logger.error("{}",e.getMessage());
			resultVO.setCode(EConstants.CODE.FAILURE);
		}
		return resultVO;
	}

	@ApiOperation(value="根据用户ID查询角色子节点",notes="查询")
	@ApiImplicitParam(name="id",value="用户ID")
	@GetMapping("/getChildren/{roleid}")
	public @ResponseBody ResultVO getChildrenRole(@PathVariable String roleid){
		ResultVO resultVO = ResultVO.build();
		try{
			resultVO.setResult(resourceService.doFindChildren(roleid));
		}catch(Exception e){
			logger.error("{}",e.getMessage());
			resultVO.setCode(EConstants.CODE.FAILURE);
		}
		return resultVO;
	}

	@ApiOperation(value="根据资源名称查询资源数量",notes="查询")
	@ApiImplicitParam(name="resourcename",value="资源名")
	@GetMapping("/getNum/{resourcename}")
	public @ResponseBody ResultVO getNum(@PathVariable String resourcename){
		ResultVO resultVO = ResultVO.build();
		try{
			ResourceVO resourceVO = new ResourceVO();
			resourceVO.setResourcename(resourcename);
			if(resourceService.doFindByVO(resourceVO) == null){
				resultVO.setResult(0);
			}else{
				resultVO.setResult(1);
			}
		}catch(Exception e){
			logger.error("{}",e.getMessage());
			resultVO.setCode(EConstants.CODE.FAILURE);
		}
		return resultVO;
	}

}
