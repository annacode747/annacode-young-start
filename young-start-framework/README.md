## framework

目前不跟新

#### bean 对象

bean对象和普通对象的区别

className.class ----> 无参的构造方法 ----> 对象 ----> 依赖注入 ---> 初始化前(@PostConstruct) 初始化(afterPropertiesSet) 初始化后(AOP)  ----> 代理对象（和原类不是一个类型） ----> 单例池 ---> bean对象

这只是模仿Spring的魔法
