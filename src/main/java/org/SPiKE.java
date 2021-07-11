package org;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Logger;

public class SPiKE {

    Logger logger = Logger.getLogger("SPiKE");

    protected Boolean shutdown = false;

    public static void main(String[] args) {
        SPiKE spike = new SPiKE();
        spike.await();
    }

    public void await() {
        ServerSocket serverSocket = null;
        int port = 8080;
        try {
            serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
            logger.info("server running...");
        }catch (IOException e) {
            e.printStackTrace(); System.exit(1);
        }

        while (!shutdown) {
            Socket socket = null;
            InputStream input = null;
            OutputStream output = null;
            try {

                socket = serverSocket.accept();
                input = socket.getInputStream();
                output = socket.getOutputStream();

//                Scanner scanner = new Scanner(input, "UTF-8");
//                while(scanner.hasNext()){
//                    String next = scanner.next();
//                    System.out.println(next);
////                    if (next.startsWith("JSESSIONID"))
////                        break;
//                }

                /**
                 * HTTP/1.1 200 OK
                 * Date: Mon, 27 Jul 2009 12:28:53 GMT
                 * Server: Apache/2.2.14 (Win32)
                 * Last-Modified: Wed, 22 Jul 2009 19:15:56 GMT
                 * Content-Length: 88
                 * Content-Type: text/html
                 * Connection: Closed
                 */

                SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss z");
                Date date = new Date();
                String dateFormat = sdf.format(date);

                output.write("HTTP/1.1 200 OK\r\n".getBytes());
                output.write(("Date: " + dateFormat + "\r\n").getBytes());
                output.write(("Content-Length: " + "fetch".length() + "\r\n").getBytes());
                output.write("Content-Type: text/html\r\n".getBytes());
                output.write("\r\n".getBytes());
                output.write("fetch".getBytes());
                output.write("\r\n\r\n".getBytes());
                System.out.println("HTTP/1.1 200 OK");
                System.out.println(("Date: " + dateFormat));
                System.out.println(("Content-Length: " + "fetch".length()));
                System.out.println("Content-Type: text/html");
                System.out.println("fetch");


//                output.write("HTTP/1.1 200 OK\r\n".getBytes());
//                output.write(("ContentType: text/html\r\n").getBytes());
//                output.write("\r\n".getBytes());
//                output.write("<b>It works!</b>".getBytes());
//                output.write("\r\n\r\n".getBytes());


                output.flush();
                output.close();
                input.close();
                socket.close();

            }catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
}