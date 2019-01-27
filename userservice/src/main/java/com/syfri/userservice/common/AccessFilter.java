package com.syfri.userservice.common;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@WebFilter(filterName = "accessFilter", urlPatterns = "/*")
public class AccessFilter implements Filter {

	private static Logger LOG = LoggerFactory.getLogger(AccessFilter.class);

	@Value("${user.token.period:1800000}")
	private long validPeriod;

	private Set<String> urlPatterns = null;//配置url通配符

	public AccessFilter(){

	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse resp = (HttpServletResponse) response;
		handleCrossDomain(req, resp);
		if(req.getMethod().equals("OPTIONS")){
			return;
		}else{
			// 获得用户请求的URI
			String path = req.getRequestURI();
			LOG.info(path);

			// 登陆请求无需过滤
			boolean isAuth = false;
			Response result = Response.build();
			result.setCode("1010100001");

			// 从head中获取token，如果head中的token为null，则从request中获取请求
			String accessToken = req.getHeader(MessageCache.LOGIN_TOKEN);
			if (StringUtils.isEmpty(accessToken)) {
				accessToken = req.getParameter(MessageCache.LOGIN_TOKEN);
			}

			// 判断对应Token是否存在
			if (StringUtils.isNotEmpty(accessToken) && MessageCache.containsKey(accessToken)) {
				UserToken loginToken = MessageCache.getUserToken(accessToken);
				long lastAccessTime = loginToken.getLastAccessTime();
				long currentTime = System.currentTimeMillis();

				// 判断是否过期
				if ((currentTime-lastAccessTime) > validPeriod) {
					MessageCache.removeToken(accessToken); //若登录token超时，则模拟退出后台登录
				} else {
					loginToken.setLastAccessTime(currentTime);
					MessageCache.putToken(accessToken,loginToken);
					isAuth = true;
				}
			}

			// 登录校验通过
			if (isAuth) {
				chain.doFilter(request, response);
			} else {
				result.setMessage("登录异常！");
				resp.setContentType("text/html");
				resp.getWriter().print(JSON.toJSONString(result));
			}
		}

	}

	@Override
	public void destroy() {
		LOG.info("===============AccessFilter destroy=============");
	}


	//处理跨域
	private void handleCrossDomain(HttpServletRequest req,HttpServletResponse resp){
		resp.setContentType("text/json;charset=UTF-8");
		resp.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
		resp.setHeader("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, DELETE, OPTIONS");
		resp.setHeader("Access-Control-Max-Age", "0");
		//res.setHeader("Access-Control-Allow-Headers", "Origin, No-Cache, X-Requested-With, "
		//		+ "If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With,userId,token");
		resp.setHeader("Access-Control-Allow-Headers", "Origin, Accept, X-Requested-With, Content-Type, X-E4M-With, Authorization,"
				+ " IPS, LOGINTOKEN, PATHNAME, PATHURL, clusterManagerType");
		resp.setHeader("Access-Control-Allow-Credentials", "true");
		resp.setHeader("XDomainRequestAllowed","1");

	}
}
