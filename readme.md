[Ehcache 3.10 Documentation 官方](https://www.ehcache.org/documentation/3.10/getting-started.html)

官方文档有ehcache3两种配置方式说明：xml和api。对应demo代码是CacheConfig。

[CQEngine学习](https://blog.csdn.net/baichoufei90/article/details/83744909)

注意：
- application.yml配置的是是 jcache 不是 ehcache
- listener一定要在cache加载数据完成之后注册，否则应用启动加载数据过程也会打印监听器日志
- CacheManager在ehcache、spring-cache中都有，他们的启动和使用方式不一样，demo中用的
是ehcache的实现
- 