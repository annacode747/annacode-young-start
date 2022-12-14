package cn.js.fan.sso;

import cn.js.fan.security.SecurityUtil;

public class Encrypt {
    String key = "@#45^&*3";
    Config config = null;

    public Encrypt() {
        config = new Config();
        key = config.getKey();
    }

    public String encodeinfo(String nick, String pwd, String privurl) {
        byte[] bkey = key.getBytes(); //DES密钥长度为64bit
        String r = "", info = "";
        info = nick + "," + pwd + "," + privurl;
        try {
            r = SecurityUtil.encode2hex(info.getBytes(), bkey);
        } catch (Exception e) {
            System.out.println("encodeinfo:" + e.getMessage());
        }
        return r;
    }

    public String[] decodeinfo(String info) {
        byte[] bkey = key.getBytes(); //DES密钥长度为64bit
        try {
            byte[] dstr = SecurityUtil.decodehexstr(info, bkey);
            String s = new String(dstr);
            if (dstr != null)
                return s.split(",");//分割后依次为nick,pwd,privurl
        } catch (Exception e) {
            System.out.println("DES decode error:" + e.getMessage());
        }
        return null;
    }

}
