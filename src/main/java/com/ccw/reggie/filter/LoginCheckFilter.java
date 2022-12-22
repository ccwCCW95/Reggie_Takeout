package com.ccw.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.ccw.reggie.common.BaseContext;
import com.ccw.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已完成登录
 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    // 路径匹配器 支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 1. 获取本次请求的uri
        String requestURI = request.getRequestURI();

        log.info("拦截到URL {}", requestURI);

        // 定义不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**"
        };

        // 2. 检查本次请求是否需要处理
        boolean check = check(urls, requestURI);

        // 3. 如果不需要处理 直接放行
        if(check){
            log.info("本次请求 {} 不需要处理", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        // 4. 判断登录状态 若已登录 则直接放行
        if(request.getSession().getAttribute("employee") != null){
            log.info("用户已登录！ 用户id为 {}", request.getSession().getAttribute("employee"));

            // 获取当前登录用户id并放入线程池
            BaseContext.setCurrentId((Long)request.getSession().getAttribute("employee"));

            filterChain.doFilter(request, response);
            return;
        }

        log.info("用户未登录！");
        // 5. 若未登录则显示未登录结果 通过输出流的方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 路径匹配 检查本次url是否放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls, String requestURI){
        for (String url: urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);

            if(match){
                return true;
            }
        }

        return false;
    }
}
