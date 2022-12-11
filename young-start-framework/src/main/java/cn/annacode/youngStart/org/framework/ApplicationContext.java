package cn.annacode.youngStart.org.framework;

import cn.annacode.youngStart.org.framework.annotation.*;
import cn.annacode.youngStart.org.framework.config.BeanDefinition;
import cn.annacode.youngStart.org.framework.enumerate.ScopeType;
import cn.annacode.youngStart.org.framework.exception.NullBeanException;
import cn.annacode.youngStart.org.framework.exception.ScanException;
import cn.annacode.youngStart.org.framework.interfaces.ApplicationContextAware;
import cn.annacode.youngStart.org.framework.interfaces.BeanNameAware;
import cn.annacode.youngStart.org.framework.interfaces.BeanPostProcessor;
import cn.annacode.youngStart.org.framework.interfaces.InitializingBean;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

public class ApplicationContext {

    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

    // 单例池
    private Map<String, Object> singletonObject = new HashMap<>();

    private ArrayList<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

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
                if (singletonObject.containsKey(beanName)){
                    // createBean 创建Bean
                    Object bean = createBean(beanName,beanDefinition);
                    // 存放单例池
                    singletonObject.put(beanName,bean);
                }
            }
        }
        System.out.println("beanDefinitionMap ===> "+beanDefinitionMap);
        System.out.println("singletonObject ===> "+singletonObject);
        System.out.println("beanPostProcessorList === > "+beanPostProcessorList);
    }

    public Object createBean(String beanName, BeanDefinition beanDefinition){
        Class type = beanDefinition.getType();
        Object instance;
        try {
            instance = type.getDeclaredConstructor().newInstance();

            // 依赖注入
            for (Field field : type.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)){
                    // 反射标记
                    field.setAccessible(true);
//                    System.out.println("field.getType().getName() == >"+field.getType().getName());
                    Object bean =  getBean(getBeanName(field.getType()));
                    field.set(instance,bean);
                }
            }

            // 回调
            //      容器 回调
            if (instance instanceof ApplicationContextAware){
                ((ApplicationContextAware)instance).setApplicationContext(this);
            }
            //      beanName 回调
            if (instance instanceof BeanNameAware){
                ((BeanNameAware)instance).setBeanName(beanName);
            }

            //
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessBeforeInitialization(beanName,instance);
            }

            // 初始化
            if (instance instanceof InitializingBean){
                ((InitializingBean)instance).afterPropertiesSet();
            }

            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessAfterInitialization(beanName,instance);
            }

            // Bean

            // aop 代理对象
            if (type.isAnnotationPresent(Transactional.class)){
                // 执行aop
                Enhancer enhancer = new Enhancer();
                enhancer.setSuperclass(type);
                Object target = instance;
                enhancer.setCallback(new MethodInterceptor() {
                    @Override
                    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                        // 执行事务方法
                        Object result = null;
                        try {
                            result = method.invoke(target,objects);
                            //提交事务
                            return result;
                        }catch (Exception e){
                            // 回滚事务
                        }
                        return result;
                    }
                });
                instance = enhancer.create();
            }


            return instance;
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
        return null;
    }

    /**
     * 配置bean的名字 ，用于后期维护
     * @param clazz 类
     * @return name
     */
    private static String getBeanName(Class<?> clazz) {
        return Introspector.decapitalize(clazz.getSimpleName());
    }

    /**
     * 创建未使用注解创建的bean 未缓存
     * @param clazz 类
     * @return instance
     */
    public Object getBean(Class<?> clazz){
        Object instance;
        try {
            instance = clazz.getDeclaredConstructor().newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)){
                    // 反射标记
                    field.setAccessible(true);
//                    System.out.println("field.getType().getName() == >"+field.getType().getName());
                    field.set(instance, getBean(field.getType()));
                    getBean(field.getType());
                }
            }
            // 创建依赖注入
            return instance;
        }catch (InstantiationException e){
            System.err.println("cannot instantiate ===> " + clazz.getSimpleName());
            e.printStackTrace();
        }catch (IllegalAccessException e){
            System.err.println("cannot access ===> " + clazz.getSimpleName());
            e.printStackTrace();
        }
            catch (NoSuchMethodException | InvocationTargetException e){
            e.printStackTrace();
        }
        return null;
    }

    private void scan(Class configClass) {
        defaultMainScan();
//        componentScan(configClass);
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
        if (!path.contains(".")){
            try {
                if (path.equals(""))
                    throw new ScanException();
                else
                    throw new ScanException(path);
            } catch (ScanException e) {
                e.printStackTrace();
            }
            return;
        }
        URL resource = classLoader.getResource(path.replace(".", "/"));
        scanFolder(classLoader, resource, path.split("\\.")[0]);
        System.out.println(path);
        scanPlugin(classLoader, resource, path.split("\\.")[0]);
    }

    /**
     * 插件扫描
     *
     * @param classLoader classLoader
     * @param resource    资源
     * @param packageName 包
     */
    private void scanPlugin(ClassLoader classLoader, URL resource, String packageName) {
        File file = new File(resource.getFile());

        System.out.println("file: ===> " + file);
        System.out.println("packageName: ===> " + packageName);


    }

    /**
     * 递归文件夹下的bean
     *
     * @param classLoader classLoader
     * @param resource    URL
     */
    private void scanFolder(ClassLoader classLoader, URL resource, String packageName) {
        File file = new File(resource.getFile());
        if (file.isDirectory()){
            for (File fi: Objects.requireNonNull(file.listFiles())){
                try {
                    String absolutePath = fi.getAbsolutePath();
                    if (fi.isDirectory()){
                        // 当前是文件夹
                        absolutePath = absolutePath.substring(absolutePath.indexOf(packageName));
                        if (absolutePath.contains("\\"))
                            absolutePath = absolutePath.replace("\\","/");
                        resource = classLoader.getResource(absolutePath);
                        assert resource != null;
                        scanFolder(classLoader,resource,packageName);
                        continue;
                    }
                    if (!absolutePath.contains(".class"))
                        continue;
                    absolutePath = absolutePath.substring(absolutePath.indexOf(packageName),absolutePath.indexOf(".class"));
                    if (absolutePath.contains("\\"))
                        absolutePath = absolutePath.replace("\\","/");
                    absolutePath = absolutePath.replace("/",".");
                    Class<?> clazz = classLoader.loadClass(absolutePath);
                    if (clazz.isAnnotationPresent(Component.class)){
                        // 设置bean对象的map name
                        if (BeanPostProcessor.class.isAssignableFrom(clazz)){
                            BeanPostProcessor instance = (BeanPostProcessor) clazz.getDeclaredConstructor().newInstance();
                            beanPostProcessorList.add(instance);
                        }
                        String beanName = clazz.getAnnotation(Component.class).value();
                        if (Objects.equals(beanName, "")){
                            beanName = getBeanName(clazz);
                        }
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
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                         NoSuchMethodException e) {
                    throw new RuntimeException(e);
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
