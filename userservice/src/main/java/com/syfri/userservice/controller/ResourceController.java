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
	public String getResource(Model model, @RequestParam(value="index") String index){
		model.addAttribute("index", index);
		return "system/resource_list";
	}

	/**
	 * 根据资源获取权限信息
	 */
	@ApiOperation(value="根据资源查询资源及其权限信息",notes="列表信息")
	@ApiImplicitParam(name="vo",value="资源对象")
	@RequiresPermissions("system/resource_list:list")
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
	@RequiresPermissions("system/resource_list:add")
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
	@RequiresPermissions("system/resource_list:update")
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
	@RequiresPermissions("system/resource_list:delete")
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
	@RequiresPermissions("system/resource_list:list")
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
	@PostMapping("/getNum")
	public @ResponseBody ResultVO getNum(@RequestBody String resourcename){
		ResultVO resultVO = ResultVO.build();
		try{
			JSONObject jsonObject = JSON.parseObject(resourcename);
			ResourceVO resourceVO = new ResourceVO();
			resourceVO.setResourcename(jsonObject.getString("resourcename"));
			if(resourceService.doSearchListByVO(resourceVO).size() == 0){
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

	@ApiOperation(value="获取所有第一父类资源",notes="查询")
	@GetMapping("/getParentResource")
	public @ResponseBody ResultVO getFirstResource(){
		ResultVO resultVO = ResultVO.build();
		try{
			resultVO.setResult(resourceService.doSearchListByVO(new ResourceVO(null,"-1")));
		}catch(Exception e){
			logger.error("{}",e.getMessage());
			resultVO.setCode(EConstants.CODE.FAILURE);
		}
		return resultVO;
	}
	@ApiOperation(value="获取所有第二父类资源",notes="查询")
	@ApiImplicitParam(name="parentid",value="父节点ID")
	@GetMapping("/getParentResource/{parentid}")
	public @ResponseBody ResultVO getSecondResource(@PathVariable String parentid){
		ResultVO resultVO = ResultVO.build();
		try{
			resultVO.setResult(resourceService.doSearchListByVO(new ResourceVO(null, parentid)));
		}catch(Exception e){
			logger.error("{}",e.getMessage());
			resultVO.setCode(EConstants.CODE.FAILURE);
		}
		return resultVO;
	}

	@ApiOperation(value="根据父节点取其父节点",notes="查询")
	@ApiImplicitParam(name="parentid",value="父节点ID")
	@GetMapping("/getParentid/{parentid}")
	public @ResponseBody ResultVO getParentid(@PathVariable String parentid){
		ResultVO resultVO = ResultVO.build();
		try{
			String tempParentid = resourceService.doFindByVO(new ResourceVO(parentid)).getParentid();
			if("-1".equals(tempParentid)){
				resultVO.setResult("-1111");
			}else{
				resultVO.setResult(tempParentid);
			}
//			resultVO.setResult(resourceService.doFindByVO(new ResourceVO(parentid)));
		}catch(Exception e){
			logger.error("{}",e.getMessage());
			resultVO.setCode(EConstants.CODE.FAILURE);
		}
		return resultVO;
	}

	/**
	 * 删除一条资源，同时删除其权限信息
	 * add by yushch
	 */
	@ApiOperation(value="根据主键删除资源资源及其权限信息",notes="删除")
	@ApiImplicitParam(name="id",value="资源主键")
	@GetMapping("/deleteOneById/{resourceid}")
	public @ResponseBody ResultVO deleteOneById(@PathVariable String resourceid){
		ResultVO resultVO = ResultVO.build();
		try{
			ResourceVO resourceVO = new ResourceVO();
			resourceVO.setParentid(resourceid);
			List<ResourceVO> list = resourceService.doSearchListByVO(resourceVO);
			if(list.size()>0){
				for (int i=0;i<list.size();i++){
					String id = list.get(i).getResourceid();
					resourceService.doDeleteResourcePermissions(id);
				}
			}
			resourceService.doDeleteResourcePermissions(resourceid);
			resultVO.setMsg("删除成功");
		}catch(Exception e){
			logger.error("{}",e.getMessage());
			resultVO.setCode(EConstants.CODE.FAILURE);
		}
		return resultVO;
	}
}
