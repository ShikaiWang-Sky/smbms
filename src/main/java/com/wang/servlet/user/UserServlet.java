package com.wang.servlet.user;

import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;
import com.wang.pojo.Role;
import com.wang.pojo.User;
import com.wang.service.role.RoleServiceImpl;
import com.wang.service.user.UserService;
import com.wang.service.user.UserServiceImpl;
import com.wang.util.Constants;
import com.wang.util.PageSupport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//实现Servlet服用
public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        if (method.equals("savepwd") && method != null) {
            this.updatePwd(req, resp);
        } else if (method.equals("pwdmodify") && method != null) {
            this.pwdModify(req, resp);
        }else if (method.equals("query") && method != null) {
            this.query(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    //修改密码
    public void updatePwd(HttpServletRequest req, HttpServletResponse resp) {
        //从Session里面拿ID
        Object o = req.getSession().getAttribute(Constants.USER_SESSION);
        String newpassword = req.getParameter("newpassword");

        boolean flag = false;

        if (o != null && !StringUtils.isNullOrEmpty(newpassword)) {
            UserService userService = new UserServiceImpl();
            flag = userService.updatePwd(((User) o).getId(), newpassword);
            if (flag) {
                req.setAttribute("message", "修改密码成功,请退出,使用新密码登录");
                //密码修改成功,移除当前Session
                req.getSession().removeAttribute(Constants.USER_SESSION);
            } else {
                req.setAttribute("message", "密码修改失败");
            }
        } else {
            req.setAttribute("message", "新密码有问题");
        }

        try {
            req.getRequestDispatcher("pwdmodify.jsp").forward(req,resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //验证旧密码,session中有用户的密码
    public void pwdModify(HttpServletRequest req, HttpServletResponse resp) {
        //从Session里面拿ID
        Object o = req.getSession().getAttribute(Constants.USER_SESSION);
        String oldpassword = req.getParameter("oldpassword");

        //万能的map: 结果集
        Map<String, String> resultMap = new HashMap<>();

        if (o == null) {
            //Session失效了,Session过期了
            resultMap.put("result", "sessionerror");
        } else if (StringUtils.isNullOrEmpty(oldpassword)) {
            resultMap.put("result", "error");
        } else  {
            //Session用户的密码
            String userPassword = ((User) o).getUserPassword();
            if (oldpassword.equals(userPassword)) {
                resultMap.put("result", "true");
            } else {
                resultMap.put("result", "false");
            }
        }

        try {
            //限定响应返回的格式为json
            resp.setContentType("application/json");
            PrintWriter writer = resp.getWriter();
            //JSONArray 阿里巴巴的JSON工具类转换格式
            /*
            把resultMap转为JSON格式
             */
            writer.write(JSONArray.toJSONString(resultMap));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //重点,难点
    public void query(HttpServletRequest req, HttpServletResponse resp) {

        //查询用户列表

        //从前端获取数据
        String queryUserName = req.getParameter("queryname");
        String temp = req.getParameter("queryUserRole");
        String pageIndex = req.getParameter("pageIndex");
        int queryUserRole = 0;

        //获取用户列表
        UserServiceImpl userService = new UserServiceImpl();
        List<User> userList = null;
        //第一次走这个请求,一定是第一页,页面大小是固定的
        //可以把这个写在配置文件中,方便后期修改
        int pageSize = 5;
        int currentPageNo = 1;

        if (queryUserName == null) {
            queryUserName = "";
        }
        if (temp != null && !temp.equals("")) {
            queryUserRole = Integer.parseInt(temp);     //给查询赋值
        } if (pageIndex != null) {
            currentPageNo = Integer.parseInt(pageIndex);
        }

        //获取用户的总数(分页:上一页,下一页)
        int totalCount = userService.getUserCount(queryUserName, queryUserRole);
        //总页数支持
        PageSupport pageSupport = new PageSupport();
        pageSupport.setCurrentPageNo(currentPageNo);
        pageSupport.setPageSize(pageSize);
        pageSupport.setTotalCount(totalCount);

        int totalPageCount = pageSupport.getTotalPageCount();

        //控制首页和尾页
        //如果页面要小于1了,就显示第一页的东西
        if (totalPageCount < 1) {
            currentPageNo = 1;
        } else if (currentPageNo > totalPageCount){
            //当前页面大于最后一页
            currentPageNo = totalPageCount;
        }

        //获取用户列表展示
        userList = userService.getUserList(queryUserName, queryUserRole, currentPageNo, pageSize);
        req.setAttribute("userList", userList);

        //展示角色列表
        RoleServiceImpl roleService = new RoleServiceImpl();
        List<Role> roleList = roleService.getRoleList();
        req.setAttribute("roleList", roleList);
        req.setAttribute("queryUserName", queryUserName);
        req.setAttribute("queryUserRole", queryUserRole);

        //展示分页
        req.setAttribute("totalCount", totalCount);
        req.setAttribute("currentPageNo", currentPageNo);
        req.setAttribute("totalPageCount", totalPageCount);

        //返回前端
        try {
            req.getRequestDispatcher("userlist.jsp").forward(req, resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
