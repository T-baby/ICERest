# ICEREST
一个基于基于resty,极简的restful框架

文档正在马不停蹄的撰写中,QQ群:557692142

```java
  @GET("/users/:name")
  //在路径中自定义解析的参数 如果有其他符合 也可以用 /users/{name}
  // 参数名就是方法变量名  除路径参数之外的参数也可以放在方法参数里  传递方式 user={json字符串}
  public Map find(String name,User user) {
    // return Lister.of(name);
    return Maper.of("k1", "v1,name:" + name, "k2", "v2");
    //返回什么数据直接return
  }
```