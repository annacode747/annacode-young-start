package cn.annacode.org.common.tool.tool;


import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;

public class lame {
//    public static ByteArrayInputStream encodeToMp3(MultipartFile file) throws IOException, UnsupportedAudioFileException {
//        // Stream流的方式
//        InputStream in = file.getInputStream();
//        return encodeToMp3(in);
//    }
//    public static ByteArrayInputStream encodeToMp3(InputStream in) throws IOException, UnsupportedAudioFileException {
//        InputStream bufferedIn = new BufferedInputStream(in);
//        // import javax.sound.sampled.AudioSystem;
//        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
//        // import net.sourceforge.lame.mp3.Lame;
//        // 官方样例
//        LameEncoder encoder = new LameEncoder(audioInputStream.getFormat(), 256, MPEGMode.MONO, Lame.QUALITY_LOW, true);
////        LameEncoder encoder = new LameEncoder(audioInputStream.getFormat(), 256, MPEGMode.STEREO, Lame.QUALITY_HIGHEST, false);
//        ByteArrayOutputStream mp3 = new ByteArrayOutputStream();
//        byte[] inputBuffer = new byte[encoder.getPCMBufferSize()];
//        byte[] outputBuffer = new byte[encoder.getPCMBufferSize()];
//        int bytesRead;
//        int bytesWritten;
//        while(0 < (bytesRead = audioInputStream.read(inputBuffer))) {
//            bytesWritten = encoder.encodeBuffer(inputBuffer, 0, bytesRead, outputBuffer);
//            mp3.write(outputBuffer, 0, bytesWritten);
//        }
//        encoder.close();
//        return new ByteArrayInputStream(mp3.toByteArray());
//    }


//    public InputStream fileToInputStream(String path) throws FileNotFoundException {
//        File file = new File(path);
//        InputStream is = new FileInputStream(path);
//        FileOutputStream fos = new FileOutputStream(databaseFilename);
//        byte[] buffer = new byte[8192];
//        int count = 0;
//        while ((count = is.read(buffer)) > 0) {
//            fos.write(buffer, 0, count);
//        }
//        fos.close();
//        is.close();
//    }
    public static boolean save2File(String fname, byte[] msg){
        OutputStream fos = null;
        try{
            File file = new File(fname);
            File parent = file.getParentFile();
            boolean bool;
            if ((!parent.exists()) &&
                    (!parent.mkdirs())) {
                return false;
            }
            fos = new FileOutputStream(file);
            fos.write(msg);
            fos.flush();
            return true;
        }catch (FileNotFoundException e){
            return false;
        }catch (IOException e){
            File parent;
            return false;
        }
        finally{
            if (fos != null) {
                try{
                    fos.close();
                }catch (IOException e) {}
            }
        }
    }
    //将Byte数组转换成文件
    public static void getFileByBytes(byte[] bytes, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {// 判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(filePath + "\\" + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
//    @Test
//    public void test(){
//        this.encodeToMp3()
//    }



}
