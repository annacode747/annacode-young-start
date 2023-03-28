package cn.annacode.org;

import cn.annacode.org.common.ClassUtils;
import cn.annacode.org.common.Pair;

import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Tomcat {

    private String apps = "plugins";

    private String classes = "classes";

    public String getApps() {
        return apps;
    }

    public String getClasses() {
        return classes;
    }

    public void start() {
        try {
            // 利用线程池
            ExecutorService executorService = Executors.newFixedThreadPool(200);


            // Socket 连接 TCP
            ServerSocket serverSocket = new ServerSocket(8080);
            while (true) {
                Socket socket = serverSocket.accept();

                executorService.execute(new SocketProcessor(socket));
            }

        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public static void main(String[] args) {
        Tomcat tomcat = new Tomcat();
        tomcat.deployApps();
        tomcat.start();
    }

    private void deployApps() {
        File appsFile = new File(System.getProperty("user.dir"), apps);
//        for (String app : ClassUtils.getSubFolderNames(appsFile)) {
//            deployApp(appsFile,app);
//        }
        for (Pair<File, String> directories : ClassUtils.getDirectoriesWithClassess(appsFile, classes)) {
            deployApp(directories.getKey(), directories.getValue());
        }
    }

    private void deployApp(File apps, String app) {
        File classDirectory = new File(apps, getClasses());
        // 文件夹所有的文件
        List<File> files = ClassUtils.getAllFilePath(classDirectory);
        for (File clazz : files) {
            // loadClass
            String name = ClassUtils.getClassName(clazz, classDirectory);        // 获取className
            try {
                AppClassLoader appClassLoader = new AppClassLoader(new URL[]{classDirectory.toURL()});
                Class<?> servletClass = appClassLoader.loadClass(name);

                System.out.println(servletClass);
                if (HttpServlet.class.isAssignableFrom(servletClass)) {
                    System.out.println(servletClass);
                }
            } catch (ClassNotFoundException e) {
                System.err.println("找不到类，八成不是类:" + e);
                throw new RuntimeException(e);
            } catch (MalformedURLException e) {
                System.err.println("找不到路径:" + e);
                throw new RuntimeException(e);
            }
        }
    }


}
