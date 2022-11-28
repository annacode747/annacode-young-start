package cn.annacode.org.common.tool.tool;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;


@Data
@Component
public class Sys {
    /**
     *获取当前项目路径的父类
     * @return
     * @throws IOException
     */
    public static String GetPathParent() throws IOException {
        File file2 = new File(System.getProperty("user.dir"));
        String Path = "";
        try {
            Path = file2.getParent();
        }catch (Exception e){

        }
        return Path;
    }

    /**
     * 创建新文件
     * @param file
     * @return
     * @throws IOException
     */
    public static File NewFile(String file) throws IOException {
        File directory = new File("");
        String courseFile = directory.getCanonicalPath();
        if(file.contains("/") || file.contains("\\")){

        }else {
            file = courseFile+"/"+file;
        }
        File newfile =new File(file);
        System.out.println(file);
        if (!newfile.exists()) {
            //创建文件
            newfile.createNewFile();
        }
        return newfile;
    }
    /**
     * 拷贝文件
     * @param srcFile
     * @param copyfile
     * @throws IOException
     */
    public static void FileCopy(String srcFile , String copyfile) throws IOException {
        File directory = new File("");
        String courseFile = directory.getCanonicalPath();
        InputStream inputStream = new FileInputStream(courseFile+"/"+srcFile); //把文件内容以流的形式读取

        OutputStream outputStream = new FileOutputStream(NewFile(courseFile+"/"+copyfile));  //把内容以流的形式写到文件
        byte[] bytes = new byte[1024];
        int length;
        while ((length = inputStream.read(bytes))>0){
            outputStream.write(bytes,0,length);
        }
        inputStream.close();
        outputStream.close();
//        BufferedReader bufferedReader = new BufferedReader(new FileReader("copyfile")); //读取文件内容
//        String string;
//        while ((string=bufferedReader.readLine()) != null){
//            System.out.println(string);
//        }

//        bufferedReader.close();
    }
    /**
     * 获取当前项目路径
     * @return
     * @throws IOException
     */
    public static String GetPath() throws IOException {
        return System.getProperty("user.dir");
    }

    /**
     * 获取当前项目的PID
     * @return
     */
    public static String GetPID() {

        String name = ManagementFactory.getRuntimeMXBean().getName();
        System.out.println(name);
        String pid = name.split("@")[0];
        return pid;
    }

    /**
     * 清空文件内容
     * @param fileName
     * @throws IOException
     */
    public static void clearInfoForFile(String fileName) throws IOException {
        File directory = new File("");
        String courseFile = directory.getCanonicalPath();
        File file = new File(courseFile+"/"+fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Value("${jieba.path}")
    public static String jieba = "/JieBaWord";

//    @Value("${jieba.path}")
//    public void getJieBaPath(String name) {
//        this.jieba = name;
//    }
    public static String log = "out.log";
//    private String Path = this.GetPath();

    /**
     * 将数据进行 MD5 加密，并以16进制字符串格式输出
     * @param data
     * @return
     */
    public static String md5(String data) {
        try {
            byte[] md5 = md5(data.getBytes("utf-8"));
            return toHexString(md5);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 将字节数组进行 MD5 加密
     * @param data
     * @return
     */
    public static byte[] md5(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            return md.digest(data);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }

    /**
     * 将加密后的字节数组，转换成16进制的字符串
     * @param md5
     * @return
     */
    private static String toHexString(byte[] md5) {
        StringBuilder sb = new StringBuilder();
        System.out.println("md5.length: " + md5.length);
        for (byte b : md5) {
            sb.append(Integer.toHexString(b & 0xff));
        }
        return sb.toString();
    }

    /**
     * 获取当前时间 字符串 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String TimeString() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //创建sdf时间对象
        String Time = sdf.format(date);
        return Time;
    }

    public static String GetLogTime() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");  //创建sdf时间对象
        String Time = sdf.format(date);
        return Time;
    }

    /**
     * 將10進制轉換為16進制
     * @param numb
     * @return
     */
    public static String encodeHEX(Integer numb){
        String hex= Integer.toHexString(numb);
        return hex;

    }

    /**
     * 將16進制字符串轉換為10進制數字
     * @param hexs
     * @return
     */
    public static int decodeHEX(String hexs){
        BigInteger bigint=new BigInteger(hexs, 16);
        int numb=bigint.intValue();
        return numb;
    }
}
