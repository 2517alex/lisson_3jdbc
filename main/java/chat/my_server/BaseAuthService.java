package chat.my_server;

import java.sql.*;

/**
 * Курс 3
 * Доашнее задание н-6
 * подключение к базе данных mysql
 *
 * решение в классе MyServer
 *
 * @author Ложкин Александр
 * @version 1.0
 */
public class BaseAuthService  {

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    String uzer = "root";
    String password = "2517";

    String url = "jdbc:mysql://localhost:3306/chat";
    String dade = "?verifyServerCertificate=false&useSSL=false&requireSSL=" +
                    "false&useLegacyDatetimeCode=false&amp&serverTimezone=UTC";


    //проверка логина и пароля, возращает имя пользователя
    public String getNickByLoginPass(String login, String pass) throws SQLException {
        String str = "";
        try (Connection connection = DriverManager.getConnection((url + dade), uzer, password)) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select * from login");
            while (rs.next()) {
                if (rs.getString("login").equals(login)) {
                    if (rs.getString("pass").equals(pass)) {
                        str += rs.getString("nick");
                        break;
                    }
                }
            }
        }
        if (str.equals("")) {
            return null;
        }
        return str;
    }

    //изменения ника пользователя
    public String getNick(String newNick, String nick) throws SQLException {
        String str = "";
        try (Connection connection = DriverManager.getConnection((url + dade), uzer, password)) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select * from login");
            while (rs.next()) {
                if (rs.getString("nick").equals(nick)) {
                    stmt.executeUpdate("UPDATE login SET `nick` = " + "'" + newNick + "'" + " WHERE (`idnew_table` = " + "'" +new String(String.valueOf(rs.getInt("idnew_table")))+ "'" + ");");
                    str += newNick;
                    break;
                }
            }
        }
        if (str.equals("")) {
            return null;
        }
        return str;
    }
}
