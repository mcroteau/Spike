package org;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class SPiKE {

    Logger logger = Logger.getLogger("SPiKE");

    protected Boolean shutdown = false;

    public static void main(String[] args) throws Exception {
        SPiKE spike = new SPiKE();
        spike.parseWebXmls();
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

                Scanner scanner = new Scanner(input, "UTF-8");
                List<String> headers = new ArrayList<>();
                while(scanner.hasNext()){
                    String next = scanner.next();
                    System.out.println(next);
                    headers.add(next);
                }

                /**
                 * HTTP/1.1 200 OK
                 * Date: Mon, 27 Jul 2009 12:28:53 GMT
                 * Server: Apache/2.2.14 (Win32)
                 * Last-Modified: Wed, 22 Jul 2009 19:15:56 GMT
                 * Content-Length: 88
                 * Content-Type: text/html
                 * Connection: Closed
                 */

//                SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss z");
//                Date date = new Date();
//                String dateFormat = sdf.format(date);
//
//                output.write("HTTP/1.1 200 OK\r\n".getBytes());
//                output.write(("Date: " + dateFormat + "\r\n").getBytes());
//                output.write(("Content-Length: " + "fetch".length() + "\r\n").getBytes());
//                output.write("Content-Type: text/html\r\n".getBytes());
//                output.write("\r\n".getBytes());
//                output.write("fetch".getBytes());
//                output.write("\r\n\r\n".getBytes());
//                System.out.println("HTTP/1.1 200 OK");
//                System.out.println(("Date: " + dateFormat));
//                System.out.println(("Content-Length: " + "fetch".length()));
//                System.out.println("Content-Type: text/html");
//                System.out.println("fetch");



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

    ClassLoader loader = ClassLoader.getSystemClassLoader();

    Map<String, Object> routes = new HashMap<>();
    Map<String, Object> servlets = new HashMap<>();

    protected void parseWebXmls() throws Exception {
        String dir = new File("").getAbsolutePath();
        String project = dir.concat(File.separator).concat("z");
        String webXmlDir = project.concat(File.separator).concat("WEB-INF").concat(File.separator).concat("web.xml");
        File webXml = new File(webXmlDir);

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(webXml);
        doc.getDocumentElement().normalize();

        NodeList servletList = doc.getElementsByTagName("servlet");
        for(int z = 0; z < servletList.getLength(); z++) {
            Node servlet = servletList.item(z);
            if (servlet.getNodeType() == Node.ELEMENT_NODE) {

                Element servletEl = (Element) servlet;
                Node servletElNameNode = servletEl.getFirstChild();
                String servletElName = servletElNameNode.getNodeValue();

                NodeList servletMappingList = servletEl.getElementsByTagName("servlet-mapping");

                for (int n = 0; n < servletMappingList.getLength(); n++) {
                    Node servletMapping = servletMappingList.item(n);
                    if (servletMapping.getNodeType() == Node.ELEMENT_NODE) {

                        Element mappingEl = (Element) servlet;
                        Node servletNameNode = mappingEl.getFirstChild();
                        Node servletRouteNode = mappingEl.getLastChild();
                        String servletName = servletNameNode.getNodeValue();
                        String servletRoute = servletRouteNode.getNodeValue();

                        if (servletName.equals(servletElName)) {
                            String servletClassKey = servletClassNode.getNodeValue();
                            Class clacc = loader.loadClass(servletClassKey);

                            Constructor[] constructors = clacc.getConstructors();
                            for(Constructor constructor : constructors){
                                constructor.setAccessible(true);
                                if(constructor.getParameterCount() == 0){
                                    Object obj = constructor.newInstance();
                                    servlets.put(servletName, obj);
                                    routes.put(servletRoute, obj);
                                }
                            }
                        }
                    }
                }
            }
        }
    }


}