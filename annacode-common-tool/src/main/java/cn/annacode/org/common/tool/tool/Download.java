package cn.annacode.org.common.tool.tool;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;


public class Download {
    public static File Img(String s) throws IOException {
        Request request = new Request.Builder().get().url(s).build();
        InputStream inputStream = new OkHttpClient().newCall(request).execute().body().byteStream();

        String id = UUID.randomUUID().toString().substring(0, 10);
        String pat = new Sys().GetPathParent()+"/img/"+id+".jpg";
        File file = new File(pat);
        FileUtils.copyToFile(inputStream, file);
        return file;
    }

    public static InputStream ImgInputStream(String s) throws IOException {
        Request request = new Request.Builder().get().url(s).build();
        return new OkHttpClient().newCall(request).execute().body().byteStream();
    }

}
