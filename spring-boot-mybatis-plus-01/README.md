> Spring Boot + H2 + Mybatis-Plus 整合 Demo
>


* [环境搭建](#环境搭建)
* [项目搭建](#项目搭建)
	* [VO](#vo)
	* [Entity](#entity)
	* [Mapper](#mapper)
	* [Controller](#controller)


[Mybatis-Plus 官方文档](https://mp.baomidou.com/guide/crud-interface.html#service-crud-%E6%8E%A5%E5%8F%A3)

SQL 语句

```sql
-- 建表语句
CREATE TABLE user
(
	id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
	name VARCHAR(30) NULL DEFAULT NULL COMMENT '姓名',
	age INT(11) NULL DEFAULT NULL COMMENT '年龄',
	email VARCHAR(50) NULL DEFAULT NULL COMMENT '邮箱',
	PRIMARY KEY (id)
);
-- 插入数据
INSERT INTO user (name, age, email) VALUES
('Jone', 18, 'test1@baomidou.com'),
('Jack', 20, 'test2@baomidou.com'),
('Tom', 28, 'test3@baomidou.com'),
('Sandy', 21, 'test4@baomidou.com'),
('Billie', 24, 'test5@baomidou.com');
```

### 环境搭建

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <!-- mybatis-plus -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
        <version>3.3.1.tmp</version>
    </dependency>
    <!-- H2 数据库 -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <!-- 用于返回 JSON -->
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>fastjson</artifactId>
        <version>1.2.6</version>
    </dependency>
    <!-- 热部署 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <optional>true</optional>
        <scope>true</scope>
    </dependency>
```

```properties
# 若使用内存数据库 spring.datasource.url=jdbc:h2:mem:test
# ./ 表示根目录下生成数据库文件 ~/ 表示用户根目录
spring.datasource.url=jdbc:h2:./db/test
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
# 使用 H2 数据平台
spring.datasource.platform=h2
# 内存模式的数据库信息读取
# spring.datasource.schema=classpath:db/schema.sql
# spring.datasource.data=classpath:db/data.sql

spring.h2.console.settings.web-allow-others=true
# 浏览器访问路径 {path}/h2
spring.h2.console.path=/h2
# 程序启动时启动 H2
spring.h2.console.enabled=true

# 打印 SQL 语句
mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl
```

### 项目搭建

#### VO

```java
public class ResponseVO extends HashMap<String, Object> {

    private static final Integer SUCCESS_STATUS = 200;
    private static final Integer ERROR_STATUS = -1;
    private static final String SUCCESS_MSG = "一切正常";
    private static final String ERROR_MSG = "出现错误";

    private static final long serialVersionUID = 1L;

    public ResponseVO success(String msg) {
        put("msg", SUCCESS_MSG);
        put("status", SUCCESS_STATUS);
        return this;
    }

    public ResponseVO error(String msg) {
        put("msg", ERROR_MSG);
        put("status", ERROR_STATUS);
        return this;
    }
    
    public ResponseVO setData(String key, Object obj) {
        @SuppressWarnings("unchecked")
        HashMap<String, Object> data = (HashMap<String, Object>) get("data");
        if (data == null) {
            data = new HashMap<>(16);
            put("data", data);
        }
        data.put(key, obj);
        return this;
    }
    
    /**
     * 返回JSON字符串
     */
    @Override
    public String toString() {
        return JSONObject.toJSONString(this); // com.alibaba.fastjson
    }
}
```

#### Entity

```java
@Data
public class User {

    @TableId(type=IdType.AUTO)	// 自增
    private Long id;
    
    private String name;
    private Integer age;
    private String email;
}
```

#### Mapper

```java
@Component // 可不加：防止编译器报错
public interface UserMapper extends BaseMapper<User> {

}
```

### Controller

```java
@Controller
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping(value = "/selectList")
    @ResponseBody
    public ResponseVO selectList(){
        ResponseVO responseVO = new ResponseVO();
        List<User> userList = userMapper.selectList(new QueryWrapper<User>().orderByDesc("id"));
        responseVO.setData("userList",userList);
        return responseVO;
    }

    @GetMapping(value = "/insert/{name}/{age}")
    public String insert(@PathVariable("name") String name, @PathVariable("age") Integer age){
        User user = new User();
        user.setName(name);
        user.setAge(age);
        user.setEmail(name + age +"@wingo-email.com");
        userMapper.insert(user);
        return "redirect:/selectList";
    }

    @GetMapping(value = "/delete/{id}")
    public String delete(@PathVariable("id") Integer id){
        userMapper.deleteById(id);
        return "redirect:/selectList";
    }

    @GetMapping(value = "/age-eq/{age}")
    public ResponseVO ageEqual(@PathVariable("age") Integer age){
        ResponseVO responseVO = new ResponseVO();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("age",age);
        List<User> userList = userMapper.selectList(queryWrapper);
        responseVO.setData("userList",userList);
        return responseVO;
    }

    @GetMapping(value = "/age/{gt}/{lt}")
    @ResponseBody
    public ResponseVO age(@PathVariable("gt") Integer gt, @PathVariable("lt") Integer lt){
        ResponseVO responseVO = new ResponseVO();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.gt("age",gt);
        queryWrapper.lt("age",lt);
        List<User> userList = userMapper.selectList(queryWrapper);
        responseVO.setData("userList",userList);
        return responseVO;
    }
}
```