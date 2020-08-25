package com.wang.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

//操作数据库的公共类
//公共类中的方法为静态方法,这样就不用实例化公共类,直接用类名.方法()进行调用
public class BaseDao {

    private static String driver;
    private static String url;
    private static String username;
    private static String password;

    //静态代码块,类加载的时候就初始化了
    static {
        Properties properties = new Properties();
        //通过类加载器读取对应的资源
        InputStream is = BaseDao.class.getClassLoader().getResourceAsStream("db.properties");

        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        driver = properties.getProperty("driver");
        url = properties.getProperty("url");
        username = properties.getProperty("username");
        password = properties.getProperty("password");
    }

    //获取数据库的连接
    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    //编写查询公共类
    public static ResultSet execute(Connection connection, String sql, Object[] params, ResultSet resultSet, PreparedStatement preparedStatement) throws SQLException {
        //预编译的sql,在后面直接执行就可以了,不用传参
        preparedStatement = connection.prepareStatement(sql);

        //给预编译的sql传参数
        for (int i = 0; i < params.length; i++) {
            //setObject方法,占位符从1开始,但是我们的数组是从0开始的,因此下面写 i+1
            preparedStatement.setObject(i + 1, params[i]);
        }

        resultSet = preparedStatement.executeQuery();
        return resultSet;
    }

    //编写增删改公共方法
    public static int execute(Connection connection, String sql, Object[] params, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement = connection.prepareStatement(sql);

        //给预编译的sql传参数
        for (int i = 0; i < params.length; i++) {
            //setObject方法,占位符从1开始,但是我们的数组是从0开始的,因此下面写 i+1
            preparedStatement.setObject(i + 1, params[i]);
        }

        int updateRows = preparedStatement.executeUpdate();
        return updateRows;
    }

    //释放资源
    public static boolean closeResource(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {
        boolean flag = true;

        if (resultSet != null){
            try {
                resultSet.close();
                //GC回收
                resultSet = null;
            } catch (SQLException e) {
                e.printStackTrace();
                flag = false;
            }
        }

        if (preparedStatement != null){
            try {
                preparedStatement.close();
                //GC回收
                preparedStatement = null;
            } catch (SQLException e) {
                e.printStackTrace();
                flag = false;
            }
        }

        if (connection != null){
            try {
                connection.close();
                //GC回收
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
                flag = false;
            }
        }

        return flag;
    }

}
