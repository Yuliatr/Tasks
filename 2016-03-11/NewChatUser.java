package SocketTaskWithThreadsMultyClients;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class NewChatUser {
    private static Integer portUp = null;
    private static Integer portDown = null;
    private static BufferedReader brUp;
    private static BufferedReader brDown;
    private static BufferedWriter bwUp;
    private static BufferedWriter bwDown;
    private static BufferedReader brConsole = new BufferedReader(new InputStreamReader(System.in));
    private static Socket socketUp = null;
    private static Socket socketDown = null;


    public static void main(String[] args) {
//----------Чтение аргументов вида: "ChatUser portUp(если есть) portDown"--------
        if (args.length == 3) {
            portUp = Integer.parseInt(args[1]);
            portDown = Integer.parseInt(args[2]);
            System.out.println("My Up port: " + portUp);
            System.out.println("My Down port: " + portDown);
        } else {
            portDown = Integer.parseInt(args[1]);
            System.out.println("My Down port: " + portDown);
            System.out.println();
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
                                System.out.println("User: " + upMess);
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
                            writeMyMessage(myMess);
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
                                System.out.println("User: " + downMess);
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