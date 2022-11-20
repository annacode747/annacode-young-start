package cn.annacode.youngStart.org.framework.interfaces;

public interface BeanPostProcessor {
    /**
     * 批量处理 bean创建 前
     * @param beanName beanName
     * @param bean bean对象
     */
    public Object postProcessBeforeInitialization(String beanName,Object bean);

    /**
     * 批量处理bean创建 后
     * @param beanName beanName
     * @param bean bean对象
     */
    public Object postProcessAfterInitialization(String beanName,Object bean);
}
