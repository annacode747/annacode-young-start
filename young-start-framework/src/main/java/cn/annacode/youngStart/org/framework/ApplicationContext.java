package cn.annacode.youngStart.org.framework;

import cn.annacode.youngStart.org.framework.annotation.Component;
import cn.annacode.youngStart.org.framework.annotation.ComponentScan;
import cn.annacode.youngStart.org.framework.annotation.Lazy;
import cn.annacode.youngStart.org.framework.annotation.Scope;
import cn.annacode.youngStart.org.framework.config.BeanDefinition;
import cn.annacode.youngStart.org.framework.enumerate.ScopeType;
import cn.annacode.youngStart.org.framework.exception.NullBeanException;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ApplicationContext {

    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

    // 单例池
    private Map<String, Object> singletonObject = new HashMap<>();

    /**
     * 配置容器
     * @param configClass 配置类
     */
    public ApplicationContext(Class configClass) {
        // 扫描
        scan(configClass);
        // 非懒加载的单例bean
        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (!beanDefinition.isLazy() && ScopeType.SINGLETON.getType().equals(beanDefinition.getScope())){
                // createBean 创建Bean
                Object bean = createBean(beanName,beanDefinition);
                // 存放单例池
                singletonObject.put(beanName,bean);
            }
        }
        System.out.println(beanDefinitionMap);
        System.out.println(singletonObject);
    }

    public Object createBean(String beanName, BeanDefinition beanDefinition){
        Class type = beanDefinition.getType();
        Object instance = null;
        try {
            instance = type.getDeclaredConstructor().newInstance();
        }catch (InstantiationException e){
            System.err.println("cannot instantiate ===> " + beanName);
            e.printStackTrace();
        }catch (IllegalAccessException e){
            System.err.println("cannot access ===> " + beanName);
            e.printStackTrace();
        }
        catch (NoSuchMethodException | InvocationTargetException e){
            e.printStackTrace();
        }
        return instance;
    }

    private void scan(Class configClass) {
        defaultMainScan();
        componentScan(configClass);
    }

    /**
     * 配置文件扫描
     * @param configClass 配置类
     */
    private void componentScan(Class configClass) {
        if (configClass.isAnnotationPresent(ComponentScan.class)){
            ComponentScan annotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            String[] pathList = annotation.value();
            for (String path: pathList) {
                URL_Scan(path);
            }
        }
    }

    /**
     * 默认扫描main目录下的包
     */
    private void defaultMainScan() {
        StackTraceElement[] stackTrace = (new RuntimeException()).getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            if ("main".equals(stackTraceElement.getMethodName())) {
                String mainName = stackTraceElement.getFileName();
                assert mainName != null;
                String packageName = stackTraceElement.getClassName().substring(0, stackTraceElement.getClassName().indexOf(mainName.replace(".java", "")));
                URL_Scan(packageName);
            }
        }
    }

    /**
     * URL扫描
     * @param path 扫描
     */
    private void URL_Scan(String path) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL resource = classLoader.getResource(path.replace(".","/"));
        scanFolder(classLoader, resource);
    }

    /**
     * 递归文件夹下的bean
     * @param classLoader classLoader
     * @param resource URL
     */
    private void scanFolder(ClassLoader classLoader, URL resource) {
        File file = new File(resource.getFile());
        if (file.isDirectory()){
            for (File FileList: Objects.requireNonNull(file.listFiles())){
                try {
                    String absolutePath = FileList.getAbsolutePath();
                    if (!absolutePath.contains(".class")){
                        // 当前是文件夹
                        absolutePath = absolutePath.substring(absolutePath.indexOf("cn"));
                        if (absolutePath.contains("\\"))
                            absolutePath = absolutePath.replace("\\","/");
                        resource = classLoader.getResource(absolutePath);
                        scanFolder(classLoader,resource);
                        continue;
                    }
                    absolutePath = absolutePath.substring(absolutePath.indexOf("cn"),absolutePath.indexOf(".class"));
                    if (absolutePath.contains("\\"))
                        absolutePath = absolutePath.replace("\\","/");
                    absolutePath = absolutePath.replace("/",".");
                    Class<?> clazz = classLoader.loadClass(absolutePath);
                    if (clazz.isAnnotationPresent(Component.class)){
                        String beanName = Introspector.decapitalize(clazz.getSimpleName());
                        BeanDefinition beanDefinition = new BeanDefinition();
                        beanDefinition.setType(clazz);
                        beanDefinition.setLazy(clazz.isAnnotationPresent(Lazy.class));
                        if (clazz.isAnnotationPresent(Scope.class)){
                            // 判断是否是单例bean
                            beanDefinition.setScope(clazz.getAnnotation(Scope.class).value().getType());
                        }else {
                            beanDefinition.setScope(ScopeType.SINGLETON.getType());
                        }
                        // 扫描bean
                        beanDefinitionMap.put(beanName,beanDefinition);
                    }
                }catch (StringIndexOutOfBoundsException e){
                    System.err.println("文件昵称报错 ===> "+e);
                }catch (ClassNotFoundException e){
                    // 类加载异常 一般不用操作
//                            System.err.println("类加载异常 ===> "+e.getMessage());
                }
            }
        }
    }


    /**
     * 获取bean容器
     * @param BeanName BeanName
     * @return Object
     */
    public Object getBean(String BeanName){
        if (!beanDefinitionMap.containsKey(BeanName)){
            try {
                throw new NullBeanException(BeanName);
            }catch (Exception ignored){}
        }
        BeanDefinition beanDefinition = beanDefinitionMap.get(BeanName);
        if (beanDefinition.getScope().equals(ScopeType.SINGLETON.getType())){
            // 单例
            Object o = singletonObject.get(BeanName);
            if (Objects.isNull(o)){
                Object bean = createBean(BeanName,beanDefinition);
                singletonObject.put(BeanName,bean);
                return bean;
            }
            return o;
        }else if (beanDefinition.getScope().equals(ScopeType.PROTOTYPE.getType())){
            // 多列
            return createBean(BeanName,beanDefinition);
        }
        return null;
    }
}
