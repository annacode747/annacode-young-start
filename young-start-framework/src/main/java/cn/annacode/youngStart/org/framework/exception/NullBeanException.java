package cn.annacode.youngStart.org.framework.exception;

public class NullBeanException extends Exception{
    public NullBeanException() {
        super();
        System.err.println("空bean对象");
    }
    public NullBeanException(String s){
        super();
        System.err.println("空 "+s+" bean对象");
    }
}
