package com.wang.filter;

import com.wang.pojo.User;
import com.wang.util.Constants;

import javax.servlet.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SysFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        //过滤器,从session中获取用户
        User user = (User) request.getSession().getAttribute(Constants.USER_SESSION);

        //已经被移除或者被注销了,或者未登录
        if (user == null) {
            response.sendRedirect("/smbms/error.jsp");
        }else {
            chain.doFilter(req, resp);
        }
    }

    @Override
    public void destroy() {

    }
}
