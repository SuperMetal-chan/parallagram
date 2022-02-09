package sample.classes.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class DatabaseHandler extends Config
{
    Connection dbConnection;

    public Connection getDbConnection() throws ClassNotFoundException, SQLException
    {
        String connectionString = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName
                + "?" + "autoReconnect=true&useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&" +
                "useLegacyDatetimeCode=false&serverTimezone=UTC";


        Class.forName("com.mysql.cj.jdbc.Driver");

        dbConnection = DriverManager.getConnection(connectionString, dbUser, dbPass);

        return dbConnection;
    }

    public void SignUpUser(User user)
    {
        String insert = "INSERT INTO " + Constant.USER_TABLE + "(" + Constant.USER_NICKNAME + "," +
                Constant.USER_EMAIL + "," + Constant.USER_PHONE + "," + Constant.USER_PASSWORD + ") VALUES(?,?,?,?)";

        try
        {
            PreparedStatement prepStat = getDbConnection().prepareStatement(insert);
            prepStat.setString(1, user.getNickname());
            prepStat.setString(2, user.getEmail());
            prepStat.setString(3, user.getPhone());
            prepStat.setString(4, user.getPassword());

            prepStat.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

    }

    public ResultSet LogInUser(User user) throws SQLException, ClassNotFoundException
    {
        String select = "SELECT * FROM " + Constant.USER_TABLE + " WHERE " + Constant.USER_PASSWORD + "=? AND ("
                + Constant.USER_EMAIL + "=? OR " + Constant.USER_PHONE + "=?)";

        PreparedStatement prepStat = getDbConnection().prepareStatement(select);

        prepStat.setString(1, user.getPassword());
        prepStat.setString(2, user.getEmail());
        prepStat.setString(3, user.getEmail());
        ResultSet resultSet = prepStat.executeQuery();

        return resultSet;
    }

    public ResultSet CheckNickname(User user) throws SQLException, ClassNotFoundException
    {
        String select = "SELECT * FROM " + Constant.USER_TABLE + " WHERE " + Constant.USER_NICKNAME + "=?";

        PreparedStatement prepStat = getDbConnection().prepareStatement(select);

        prepStat.setString(1, user.getNickname());

        ResultSet resultSet = prepStat.executeQuery();

        return resultSet;
    }

    public ResultSet CheckEmail(User user) throws SQLException, ClassNotFoundException
    {
        String select = "SELECT * FROM " + Constant.USER_TABLE + " WHERE " + Constant.USER_EMAIL + "=?";

        PreparedStatement prepStat = getDbConnection().prepareStatement(select);

        prepStat.setString(1, user.getEmail());

        ResultSet resultSet = prepStat.executeQuery();

        return resultSet;
    }

    public ResultSet CheckPhone(User user) throws SQLException, ClassNotFoundException
    {
        String select = "SELECT * FROM " + Constant.USER_TABLE + " WHERE " + Constant.USER_PHONE + "=?";

        PreparedStatement prepStat = getDbConnection().prepareStatement(select);

        prepStat.setString(1, user.getPhone());

        ResultSet resultSet = prepStat.executeQuery();

        return resultSet;
    }

    public ResultSet CheckPassword(User user) throws SQLException, ClassNotFoundException
    {
        String select = "SELECT * FROM " + Constant.USER_TABLE + " WHERE " + Constant.USER_PASSWORD + "=?";

        PreparedStatement prepStat = getDbConnection().prepareStatement(select);

        prepStat.setString(1, user.getPassword());

        ResultSet resultSet = prepStat.executeQuery();

        return resultSet;
    }
}
