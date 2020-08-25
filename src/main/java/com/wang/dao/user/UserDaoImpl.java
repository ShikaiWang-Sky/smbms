package com.wang.dao.user;

import com.mysql.jdbc.StringUtils;
import com.wang.dao.BaseDao;
import com.wang.pojo.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {

    //得到登录的用户
    @Override
    public User getLoginUser(Connection connection, String userCode, String password) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        User user = null;

        if (connection != null) {
            String sql = "select * from smbms_user where userCode = ? and userPassword = ?";
            Object[] params = {userCode, password};

           rs = BaseDao.execute(connection, sql, params, rs, pstm);

           if (rs.next()) {
               user = new User();
               user.setId(rs.getInt("id"));
               user.setUserCode(rs.getString("userCode"));
               user.setUserName(rs.getString("userName"));
               user.setUserPassword(rs.getString("userPassword"));
               user.setGender(rs.getInt("gender"));
               user.setBirthday(rs.getDate("birthday"));
               user.setPhone(rs.getString("phone"));
               user.setAddress(rs.getString("address"));
               user.setUserRole(rs.getInt("userRole"));
               user.setCreatedBy(rs.getInt("createdBy"));
               user.setCreationDate(rs.getTimestamp("creationDate"));
               user.setModifyBy(rs.getInt("modifyBy"));
               user.setModifyDate(rs.getTimestamp("modifyDate"));
           }
           BaseDao.closeResource(null, pstm, rs);

        }

        return user;

    }

    //修改当前用户的密码,返回受影响的行数
    @Override
    public int updatePwd(Connection connection, int id, String password) throws SQLException {

        PreparedStatement pstm = null;
        int execute = 0;

        if (connection != null) {
            String sql = "update smbms_user set userPassword = ? where id = ?";
            Object params[] = {password, id};
            execute = BaseDao.execute(connection, sql, params, pstm);
            BaseDao.closeResource(null, pstm, null);
        }

        return execute;

    }

    //根据用户名或者角色查询用户总数
    @Override
    public int getUserCount(Connection connection, String username, int userRole) throws SQLException {

        PreparedStatement pstm = null;
        ResultSet rs = null;
        int count = 0;

        if (connection != null) {
            StringBuffer sql = new StringBuffer();
            sql.append("select count(1) as count from smbms_user as u, smbms_role as r where u.userRole = r.id");
            //存放我们的参数
            ArrayList<Object> list = new ArrayList<>();

            if (!StringUtils.isNullOrEmpty(username)) {
                sql.append(" and u.userName like ?");
                list.add("%" + username + "%");     //index = 0
            }

            if (userRole > 0) {
                sql.append(" and u.userRole like ?");
                list.add(userRole);     //index = 1
            }

            //把list转为数组
            Object[] objects = list.toArray();

            rs = BaseDao.execute(connection, sql.toString(), objects, rs, pstm);

            if (rs.next()) {
                //从结果集中获得最终的结果
                count = rs.getInt("count");
            }

            BaseDao.closeResource(null, pstm, rs);

        }

        return count;
    }

    //通过条件查询-userlist
    @Override
    public List<User> getUserList(Connection connection, String userName, int userRole, int currentPageNo, int pageSize) throws SQLException {

        PreparedStatement pstm = null;
        ResultSet rs = null;
        ArrayList<User> userList = new ArrayList<>();

        if (connection != null) {
            StringBuffer sql = new StringBuffer();
            sql.append("select u.*, r.roleName userRoleName from smbms_user as u join smbms_role as r on u.userRole = r.id");
            //存放我们的参数
            ArrayList<Object> list = new ArrayList<>();

            if (!StringUtils.isNullOrEmpty(userName)) {
                sql.append(" and u.userName like ?");
                list.add("%" + userName + "%");     //index = 0
            }

            if (userRole > 0) {
                sql.append(" and u.userRole like ?");
                list.add(userRole);     //index = 1
            }

            //在数据库中,分页使用limit   startIndex, pageSize;   总数
            //0,5
            //6,5
            //(当前页-1)*页面大小
            //appendSQL语句不要忘了前面写空格!

            sql.append(" order by creationDate desc limit ?,?");
            currentPageNo = (currentPageNo - 1) * pageSize;
            list.add(currentPageNo);
            list.add(pageSize);

            Object[] params = list.toArray();
            rs = BaseDao.execute(connection, sql.toString(), params, rs, pstm);
            while (rs.next()) {
                User _user = new User();
                _user.setId(rs.getInt("id"));
                _user.setUserCode(rs.getString("userCode"));
                _user.setUserName(rs.getString("userName"));
                _user.setGender(rs.getInt("gender"));
                _user.setBirthday(rs.getDate("birthday"));
                _user.setPhone(rs.getString("phone"));
                _user.setUserRole(rs.getInt("userRole"));
                _user.setUserRoleName(rs.getString("userRoleName"));
                userList.add(_user);
            }
            BaseDao.closeResource(null, pstm, rs);

        }
        return userList;
    }

}
