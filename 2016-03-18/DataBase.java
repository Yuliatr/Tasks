import java.sql.*;

//Задача: создание таблицы

public class DataBase {
    public static void main(String[] args) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost/postgres", "postgres", "Nshsgshs");
        Statement statement = connection.createStatement();
//      statement.executeQuery("create table message(id serial primary key, name text, message text)");
        statement.executeQuery("INSERT INTO message( name, message) VALUES ('name_1', 'message_1')");

/* Вывод содержимого таблицы
        boolean execute = statement.execute("select * from message");
        if (execute) {
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                System.out.println(resultSet.getString(2) + ": " + resultSet.getString(3));
            }
        }
*/
    }
}
