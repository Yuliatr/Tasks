import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;


public class Utility {
    public static void main(String[] args) throws SQLException {

        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost/postgres", "postgres", "Nshsgshs");
        Statement statement = connection.createStatement();
        //statement.executeQuery("create table util(id serial primary key, name text, message text)");
        //statement.executeQuery("INSERT INTO util( name, message) VALUES ('name_1', 'message1')");

//Вывод всего содержимого таблицы
/*        boolean execute = statement.execute("select * from util");
        if (execute) {
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                System.out.println(resultSet.getString(2) + ": " + resultSet.getString(3));
            }
        }
*/
//чтение запроса из консоли типа: "id=2" или "name=name_1" + вывод результата вида: id | name | message
        BufferedReader brConsole = new BufferedReader(new InputStreamReader(System.in));

        try {
            String request = brConsole.readLine();

            if (request.startsWith("id=") || request.startsWith("name=")) {
                if (request.startsWith("id=")) {
                    String id = request.substring("id=".length());

                    boolean execute1 = statement.execute("select * from util WHERE id = " + id);
                    if (execute1) {
                        ResultSet resultSet = statement.getResultSet();
                        while (resultSet.next()) {
                            System.out.println(resultSet.getString(1) + " | " + resultSet.getString(2) + " | " + resultSet.getString(3));
                        }
                    }
                }

                if (request.startsWith("name=")) {
                    String name = request.substring("name=".length());
                    boolean execute1 = statement.execute("select * from util WHERE name LIKE " + "'" + name + "'");
                    if (execute1) {
                        ResultSet resultSet = statement.getResultSet();
                        while (resultSet.next()) {
                            System.out.println(resultSet.getString(1) + " | " + resultSet.getString(2) + " | " + resultSet.getString(3));
                        }
                    }
                }
            } else System.out.println("Please, type any id or name!");
        } catch (IOException e) {
            System.out.println("Oppppps");
        }
    }
}