//Задача: чат с сохранением сообщений: сообщения участников сохраняются в БД и загружаются пользователям при подключении к чату
//+Пользователи видят, от кого пришло сообщение

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;

public class ChatDBWithHistory {
    private static Integer portUp = null;
    private static Integer portDown = null;
    private static BufferedReader brUp;
    private static BufferedReader brDown;
    private static BufferedWriter bwUp;
    private static BufferedWriter bwDown;
    private static BufferedReader brConsole = new BufferedReader(new InputStreamReader(System.in));
    private static Socket socketUp = null;
    private static Socket socketDown = null;
    private static String userName;

    private static Connection connection;
    private static Statement statement;

    public static void main(String[] args) throws SQLException {

//----------Чтение аргументов вида: "ChatUser userName portUp(если есть) portDown"--------
        if (args.length == 4) {
            portUp = Integer.parseInt(args[2]);
            portDown = Integer.parseInt(args[3]);
            System.out.println("My Up port: " + portUp);
            System.out.println("My Down port: " + portDown);
        } else {
            portDown = Integer.parseInt(args[2]);
            System.out.println("My Down port: " + portDown);
        }
        System.out.println();
        userName = args[1];


//------------------Поключение к базе данных (таблица chat уже создана)---------------
        connection = DriverManager.getConnection("jdbc:postgresql://localhost/postgres", "postgres", "Nshsgshs");  //("jdbc:postgresql://АДРЕС/НАЗВАНИЕ БД", "ИМЯ ПОЛЬЗОВАТЕЛЯ", "ПАРОЛЬ");
//      statement.executeQuery("create table chat(id serial primary key, name text, message text)");   //<----Создание таблицы  для чата
        statement = connection.createStatement();   //statement - отправка запроса

        //Вывод сообщений при подключении к чату
        boolean execute = statement.execute("select * from chat");
        if (execute) {
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                System.out.println(resultSet.getString(2) + ": " + resultSet.getString(3));
            }
        }

        try {
//-------------------------Подключение Верхнего сокета--------------------------------
            if (portUp != null) {
                //Подключение к верхнему Сокету
                socketUp = new Socket("localhost", portUp);
                brUp = new BufferedReader(new InputStreamReader(socketUp.getInputStream()));
                bwUp = new BufferedWriter(new OutputStreamWriter(socketUp.getOutputStream()));
                //System.out.println("<--Connected with UpSocket");
            }

//---------------------Поток прослушивания Верхнего сокета----------------------------
            if (socketUp != null) {
                Thread threadForReadingUp = new Thread() {
                    public void run() {
                        try {
                            while (true) {
                                //Чтение, запись в консоль и отправка вниз
                                String upMess = brUp.readLine();
                                System.out.println(upMess);
                                if (socketDown != null) {
                                    writeMessage(bwDown, upMess);
                                }
                            }
                        } catch (IOException e) {
                            System.out.println("Problem with UpSocket " + e);
                        }
                    }
                };
                threadForReadingUp.start();
            }

//---------------Запись своих сообщений в  консоль и отправка по сторонам-----------------
            Thread threadPrintMyMessage = new Thread() {
                public void run() {
                    try {
                        while (true) {
                            String myMess = brConsole.readLine();
                            writeMyMessage(userName + ": " + myMess);
                            try {
                                statement.executeQuery("INSERT INTO chat(name, message) VALUES ( '" + userName + "', '"+ myMess + "')"); //Добавление в столбцы name и message соотв. значений
                            } catch (SQLException ignored) {
                                //  System.out.println("Problem with DB");
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            threadPrintMyMessage.start();

//-------------------------Подключение Нижнего сокета--------------------------------
            if (portDown != null) {
                ServerSocket ss = new ServerSocket(portDown);
                socketDown = ss.accept();
                brDown = new BufferedReader(new InputStreamReader(socketDown.getInputStream()));
                bwDown = new BufferedWriter(new OutputStreamWriter(socketDown.getOutputStream()));
                //System.out.println("<--Connected with DownSocket");
            }

//---------------------Поток прослушивания Нижнего сокета----------------------------
            if (socketDown != null) {
                Thread threadForReadingDown = new Thread() {
                    public void run() {
                        try {
                            while (true) {
                                //Чтение, запись в консоль и отправка вверх
                                String downMess = null;
                                downMess = brDown.readLine();
                                System.out.println(downMess);
                                if (socketUp != null) {
                                    writeMessage(bwUp, downMess);
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                threadForReadingDown.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void writeMessage(BufferedWriter bw, String message)throws IOException {
        bw.write(message, 0, message.length());
        bw.newLine();
        bw.flush();
    }

    public static synchronized void writeMyMessage(String myMess) {
        try {
            if (socketDown!=null) {
                writeMessage(bwDown, myMess);
            }
            if (socketUp!=null) {
                writeMessage(bwUp, myMess);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}