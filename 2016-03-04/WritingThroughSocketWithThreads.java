import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class WritingThroughSocketWithThreads {
    private static ServerSocket ss;
    private static Socket s;
    private static BufferedReader br;
    private static BufferedWriter bw;

    public static void main(String[] args) {

        final BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(System.in));

        try {
            s = new Socket("localhost",15151);
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));

            while (true) {
                    writeMessage("Writer: " + bufferedReader.readLine());
                    readMessage();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread threadForPrint = new Thread() {
            public void run() {
                while(true) {
                    try {
                        writeMessage(bufferedReader.readLine());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread threadForReading = new Thread() {
            public void run() {
                while (true) {
                    readMessage();
                }
            }
        };

        threadForPrint.start();
        threadForReading.start();
    }

    public static void readMessage () {
        try {
            System.out.println(br.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeMessage ( String message) {
        try {
            bw.write(message, 0, message.length());
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
