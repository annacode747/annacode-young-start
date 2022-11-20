package cn.annacode.youngStart.org.framework.exception;

public class ScanException extends Exception {
    public ScanException() {
        super();
        System.err.println("扫描包异常，请在main方法类外 或者自定义配置 创建包");
    }
    public ScanException(String s){
        super();
        System.err.println("扫描包异常，请在"+s+"方法创建包");
    }
}
