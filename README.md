ICEREST因为架构设计上的缺陷，建议切换到新项目https://github.com/cloudoptlab/cloudopt-next

# ICEREST概述

![](http://i4.piimg.com/1949/9b7b792d5b9a1261.jpg)

ICEREST是一个非常轻量级只有200k左右的RESTful路由框架，通过ICEREST你可以处理url的解析，数据的封装,Json的输出，和传统的方法融合，请求的参数便是方法的参数，方法的返回值便是请求的返回值，原则就是：你会写方法，你就会用。

由于ICEREST非常简单所以只需要看一遍文档就能轻松使用。在ICEREST并没有提供orm，所以你还需要选择一个orm哦，推荐使用[MongoPlugin](https://github.com/T-baby/MongoDB-Plugin)与ICEREST搭配，体验RESTful和MongoDB搭配的极速开发。

- 极简设计，几乎0配置。

- 脱离传统MVC，专业的事由专业的做。

- 支持AOP，拦截器配置灵活，配合[MongoPlugin](https://github.com/T-baby/MongoDB-Plugin)轻松校验传输数据。

- 与MOTAN无缝结合。

# RESTful是什么？

RESTful是一种软件架构风格，设计风格而不是标准，只是提供了一组设计原则和约束条件。它主要用于客户端和服务器交互类的软件。基于这个风格设计的软件可以更简洁，更有层次，更易于实现缓存等机制。

如果希望更深入了解RESTful可以看：

- [《理解本真的REST架构风格》](http://www.infoq.com/cn/articles/understanding-restful-style)

- [《HTTP API设计指南》](http://www.cybermkd.com/restful-api-she-ji-zhi-nan/)

- [《RESTful API设计指南》](http://www.cybermkd.com/restful-apishe-ji-zhi-nan/)

# 相关文档

中文文档：https://github.com/T-baby/ICEREST/wiki/
