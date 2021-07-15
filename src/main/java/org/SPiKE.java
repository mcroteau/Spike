package org;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

public class SPiKE {

    private Loader loader;

    Logger logger = Logger.getLogger("SPiKE");

    protected Boolean shutdown = false;

    List<Object> startupEvents = new ArrayList<>();

    public static void main(String[] args)  {
        SPiKE spike = new SPiKE();
        spike.loadDependencies(0);
        spike.dispatchStartup();
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

                BufferedReader br = new BufferedReader(new InputStreamReader(input));
                StringBuilder requestBuilder = new StringBuilder();
                String line;
                while (!(line = br.readLine()).equals("")) {
                    requestBuilder.append(line + "\r\n");
                    System.out.println(line);
                }
                String request = requestBuilder.toString();
                String[] requestsLines = request.split("\r\n");
                String[] requestLine = requestsLines[0].split(" ");
                String method = requestLine[0];
                String path = requestLine[1];
                String version = requestLine[2];
                String host = requestsLines[1].split(" ")[1];

                List<String> headers = new ArrayList<>();
                for (int h = 2; h < requestsLines.length; h++) {
                    String header = requestsLines[h];
                    headers.add(header);
                }

                HttpContext context = new HttpContext();
                HttpRequest httpRequest = new HttpRequest(context);
                httpRequest.setRequestURI(path);

                HttpServletRequest req = new HttpServletRequestWrapper(httpRequest);
                HttpServletResponse resp = new HttpServletResponseWrapper(new HttpResponse());

                SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss z");
                Date date = new Date();
                String dateFormat = sdf.format(date);

                if(path.startsWith("/z")){
                    System.out.println("within context");
                    String route = path.replaceFirst("/z", "");
                    System.out.println("route: " + route);
                    for(Map.Entry<String, Object> entry : routes.entrySet()){
                        System.out.println(entry.getKey());
                    }

                    Object servlet = routes.get(route);

                    System.out.println(servlet);

                    Object[] parameters = {req, resp};
                    Method[] methods = servlet.getClass().getDeclaredMethods();
                    for(Method m : methods){
                        System.out.println(m.getName() + " ::: ");
                        Type[] types = m.getParameterTypes();
                        for(Type type: types){
                            System.out.print(" : "+ type.getTypeName());
                        }
                        System.out.println("");
                    }


                    Method action = servlet.getClass().getDeclaredMethod("doGet", HttpServletRequest.class, HttpServletResponse.class);
                    action.setAccessible(true);

                    Object html = action.invoke(servlet, parameters);

                    output.write("HTTP/1.1 200 OK\r\n".getBytes());
                    output.write(("Date: " + dateFormat + "\r\n").getBytes());
                    output.write(("Content-Length: " + html.toString().length() + "\r\n").getBytes());
                    output.write("Content-Type: text/html\r\n".getBytes());
                    output.write("\r\n".getBytes());
                    output.write(html.toString().getBytes());
                    output.write("\r\n\r\n".getBytes());

                }else{
                    output.write("HTTP/1.1 404 Not Found\r\n".getBytes());
                    output.write(("Date: " + dateFormat + "\r\n").getBytes());
                    output.write(("Content-Length: " + "404".length() + "\r\n").getBytes());
                    output.write("Content-Type: text/html\r\n".getBytes());
                    output.write("\r\n".getBytes());
                    output.write("404".getBytes());
                    output.write("\r\n\r\n".getBytes());
                }

                output.flush();
                output.close();
                input.close();
                socket.close();

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


            }catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    Map<String, Object> routes = new HashMap<>();
    Map<String, Object> servlets = new HashMap<>();

    protected void parseWebXmls() {
        try {

            //java.net.FactoryURLClassLoader@3551a94
            //jdk.internal.loader.ClassLoaders$AppClassLoader@277050dc

            String webXmlDir = getProjectPath().concat(File.separator)
                    .concat("WEB-INF").concat(File.separator).concat("web.xml");
            File webXml = new File(webXmlDir);


            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(webXml);
            doc.getDocumentElement().normalize();

            NodeList servletList = doc.getElementsByTagName("servlet");
            System.out.println(servletList.getLength());
//every last of my fucking girlfriends were raped in here... and some outside

            for (int z = 0; z < servletList.getLength(); z++) {
                Node servlet = servletList.item(z);
                if (servlet.getNodeType() == Node.ELEMENT_NODE) {

                    Element servletEl = (Element) servlet;
                    String servletElName = servletEl.getElementsByTagName("servlet-name").item(0).getTextContent();
                    String servletClass = servletEl.getElementsByTagName("servlet-class").item(0).getTextContent();

                    NodeList servletMappingList = doc.getElementsByTagName("servlet-mapping");

                    for (int n = 0; n < servletMappingList.getLength(); n++) {
                        Node servletMapping = servletMappingList.item(n);

                        System.out.println(servletMapping.getNodeType() + "=" + Node.ELEMENT_NODE);
                        if (servletMapping.getNodeType() == Node.ELEMENT_NODE) {

                            Element mappingEl = (Element) servletMapping;
                            String servletName = mappingEl.getElementsByTagName("servlet-name").item(0).getTextContent();
                            String servletRoute = mappingEl.getElementsByTagName("url-pattern").item(0).getTextContent();

                            if (servletName.equals(servletElName)) {
                                Class clacc = loader.loadClass(servletClass);

                                Constructor[] constructors = clacc.getConstructors();
                                for (Constructor constructor : constructors) {
                                    constructor.setAccessible(true);
                                    if (constructor.getParameterCount() == 0) {
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
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }


    Map<String, Boolean> names = new HashMap<>();
    protected void loadDependencies(int q) {

        String projectPath = getProjectPath()
                .concat(File.separator)
                .concat("WEB-INF")
                .concat(File.separator)
                .concat("lib");

        File dependencyDir = new File(projectPath);
        File[] jars = dependencyDir.listFiles();
        URL[] jarUrls = new URL[jars.length];

        List<String> classNames = new ArrayList<>();

        System.out.println("invoked ::: " + q);
        for(int z = q; z < jars.length; z++){

            try {
                File jar = jars[z];
                JarFile jarFile = new JarFile(jar.getAbsolutePath());
                Enumeration<JarEntry> e = jarFile.entries();

                URL[] urls = { new URL("jar:file:" + jar.getAbsolutePath()+"!/") };
                jarUrls[z] = jar.toURI().toURL();

//                while (e.hasMoreElements()) {
//                    JarEntry je = e.nextElement();
//                    if (je.isDirectory() || !je.getName().endsWith(".class")) {
//                        continue;
//                    }
//                    // -6 because of .class
//                    String className = je.getName().substring(0, je.getName().length() - 6);
//                    className = className.replace('/', '.');
//                    Class c = cl.loadClass(className);
//                }
            }catch(Exception ex){

            }


//            try {
//
//                File jar = jars[z];
//
//                if(jar.getName().endsWith(".jar")) {
//
//                    System.out.println("dep ::: " + jar.getName());
//
//                    URL urls [] = {jar.toURI().toURL()};
//                    Loader cl = new Loader (urls);
//
//                    jarUrls[z] = jar.toURI().toURL();
//
//                    ZipInputStream zip = new ZipInputStream(new FileInputStream(jar.getAbsolutePath()));
//
//                    for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
//                        if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
//                            String className = entry.getName().replace('/', '.');
//                            String name = className.substring(0, className.length() - ".class".length());// including ".class"
//
//                            classNames.add(name);
//
////                            try{
////                                if(!names.containsKey(name) &&
////                                        !name.equals("module-info")) {
////                                    System.out.println(name);
////                                    cl.loadClass(name);
////                                    names.put(name, true);
////                                }
////                            }catch(NoClassDefFoundError ex){
////                                z++;
////                                if(z == jars.length){
////                                    z = 0;
////                                }else{
////                                    z++;
////                                }
////                                ex.printStackTrace();
////                                loadDependencies(z);
////                            }
//                        }
//                    }
//                }
//
//            }catch(Exception ex){ }

        }


        for(String name : classNames){
            names.put(name, true);
        }


        for(Map.Entry<String, Boolean> entry: names.entrySet()){
            if(!names.containsKey(entry.getKey())){
                System.out.println("!!!" + entry.getKey());
            }
        }

        loader = new Loader(jarUrls);
        Thread.currentThread().setContextClassLoader(loader);
        System.out.println(loader);


//        URLClassLoader loader = (URLClassLoader) this.getClass().getClassLoader();
//        ClassLoader ucl = URLClassLoader.newInstance(
//                jarUrls,
//                getClass().getClassLoader()
//        );
//        for(File jar : jars) {
//            try {
//                URL targetUrl = jar.toURI().toURL();
//
//                Method add = ucl.getClass().getDeclaredMethod("addURL", new Class[] { URL.class });
//                add.setAccessible(true);
//                add.invoke(ucl, targetUrl);
//
//            }catch (Exception ex){
//                System.out.println("jar :: " + jar.getName());
//            }
//        }

        List<String> classNamesFinal = new ArrayList<>();
        for(String name : classNames){
            for(String check : classNames){
                if(name.endsWith(check) &&
                        !name.equals(check)){
                    System.out.println("match : versions: " + name);
                }else if(!classNamesFinal.contains(name)) {
                    classNamesFinal.add(name);
                }
            }
        }
        System.out.println("load classes....");
    }

    protected String getProjectPath(){
        return new File("")
                .getAbsolutePath()
                .concat(File.separator)
                .concat("webapps")
                .concat(File.separator)
                .concat("z");
    }


    protected void dispatchStartup(){

        String projectPath = getProjectPath()
                .concat(File.separator)
                .concat("WEB-INF")
                .concat(File.separator)
                .concat("classes");

        File folder = new File(projectPath);
        File[] folderFiles = folder.listFiles();
        traverseDispatch(folderFiles);

        try {
            ServletContext context = new HttpContext();
            ServletContextEvent sce = new ServletContextEvent(context);

            for (Object obj : startupEvents) {
                Method invoke = obj.getClass().getMethod("contextInitialized", ServletContextEvent.class);
                invoke.invoke(obj, sce);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }

    }

    protected void traverseDispatch(File[] folderFiles){
        for(File file : folderFiles){
            if(file.isDirectory()){
                File[] folder = file.listFiles();
                traverseDispatch(folder);
                continue;
            }

            try {
                if (file.getName().endsWith(".class")) {
                    ///Users/mcroteau/Development/Projects/SPiKE/webapps/z/WEB-INF/classes
                    ///Users/mcroteau/Development/Projects/SPiKE/webapps/z/WEB-INF/classes
                    URL url = getUrlPath(file);

//                    url = new URL("/Users/mcroteau/Development/Projects/SPiKE/z/WEB-INF/classes/io/support/StartupListener.class");
//                    url = new URL("file:\\Users\\mcroteau\\Development\\Projects\\SPiKE\\z\\WEB-INF\\classes\\io\\support\\StartupListener.class");

                    System.out.println(url);
//                    URLClassLoader urlClassLoader = URLClassLoader.newInstance(new URL[] { url });

                    loader.addFile(url);

                    Class cls = loader.loadClass(getClassName(file));
                    if(cls.isAnnotationPresent(WebListener.class)){
                        Object obj = cls.getConstructor().newInstance();
                        startupEvents.add(obj);
                    }
                }
            }catch(ClassNotFoundException ex){
                ex.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

        }
    }

    protected String getClassName(File file){
        String separator = System.getProperty("file.separator");
        String regex = "classes\\" + separator;
        String[] pathParts = file.getPath().split(regex);
        String name = pathParts[1]
                .replace("\\", ".")
                .replace("/",".")
                .replace(".class", "");
        return name;
//        int idx = name.lastIndexOf(".");
//        return name.substring(idx).replace(".", "");
    }

    protected URL getUrlPath(File file) throws MalformedURLException {
        String baseUrl = file.getAbsolutePath();
        String[] urlParts = baseUrl.split("classes");
        return new URL("file:" + urlParts[0] + "classes" + File.separator);

//        int idx = file.getAbsolutePath().lastIndexOf(File.separator);
//        return new URL( "file:" + file.getAbsolutePath().substring(0, idx).concat(File.separator));
    }

}