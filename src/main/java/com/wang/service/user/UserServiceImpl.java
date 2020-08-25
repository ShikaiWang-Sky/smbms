package com.wang.service.user;

import com.wang.dao.BaseDao;
import com.wang.dao.user.UserDao;
import com.wang.dao.user.UserDaoImpl;
import com.wang.pojo.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserServiceImpl implements UserService {

    //业务层都会调用dao层,因此我们要引入dao层
    private UserDao userDao;
    public  UserServiceImpl() {
        userDao = new UserDaoImpl();
    }

    @Override
    public User login(String userCode, String password) {
        Connection connection = null;
        User user = null;

        try {
            connection = BaseDao.getConnection();
            //通过业务层调用对应的具体的数据库操作
            user = userDao.getLoginUser(connection, userCode, password);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return user;
    }

    //根据用户ID修改密码
    @Override
    public boolean updatePwd(int id, String pwd) {
        Connection connection = null;
        boolean flag = false;

        //修改密码
        try {
            connection = BaseDao.getConnection();
            if (userDao.updatePwd(connection, id, pwd) > 0) {
                flag = true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return flag;
    }

    //查询记录数
    @Override
    public int getUserCount(String username, int userRole) {

        Connection connection = null;
        int userCount = 0;

        try {
            connection = BaseDao.getConnection();
            userCount = userDao.getUserCount(connection, username, userRole);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }

        return userCount;
    }

    //根据条件查询用户列表
    @Override
    public List<User> getUserList(String queryUserName, int queryUserRole, int currentPageNo, int pageSize) {
        Connection connection = null;
        List<User> userList = null;

        try {
            connection = BaseDao.getConnection();
            userList = userDao.getUserList(connection, queryUserName, queryUserRole, currentPageNo, pageSize);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }

        return userList;

    }
}
