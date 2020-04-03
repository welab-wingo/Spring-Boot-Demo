> Spring Boot + Shiro + JPA + Thymeleaf

* [导入依赖](#导入依赖)
* [自定义 Realm 类](#自定义 Realm 类)
* [Shiro 配置类](#Shiro 配置类)
* [控制器类](#控制器类)

### 导入依赖

```xml
<!-- shiro -->
<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-web</artifactId>
    <version>1.4.0</version>
</dependency>
<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-spring</artifactId>
    <version>1.4.0</version>
</dependency>
<dependency>
    <groupId>com.github.theborakompanioni</groupId>
    <artifactId>thymeleaf-extras-shiro</artifactId>
    <version>2.0.0</version>
</dependency>
```

### 自定义 Realm 类

```java
@Slf4j
public class UserRealm extends AuthorizingRealm {

    @Autowired
    private UserRepository userRepository;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection arg0) {
        log.info("执行授权逻辑");
        Subject subject = SecurityUtils.getSubject();
        User user = userRepository.findUserByUsername((String) subject.getSession().getAttribute("username"));

        // 给用户进行授权
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addStringPermission(user.getPerms());

        return info;
    }


    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken arg0) throws AuthenticationException {
        log.info("执行认证逻辑");

        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();


        UsernamePasswordToken token = (UsernamePasswordToken)arg0;
        User user = userRepository.findUserByUsername(token.getUsername());
        session.setAttribute("username", token.getUsername());
        if(user==null){
            // shiro 底层会抛出UnKnowAccountException
            return null;
        }
        // 判断密码
        return new SimpleAuthenticationInfo(user,user.getPassword(),getName());
    }
}

```

### Shiro 配置类

```java
@Slf4j
@Configuration
public class ShiroConfig {

    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Autowired DefaultWebSecurityManager securityManager){

        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();

        // 设置安全管理器
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        // 设置过滤器链
        Map<String,String> filterMap = new LinkedHashMap<>(16);

        filterMap.put("/index", "anon");
        filterMap.put("/favicon.ico", "anon");
        // 添加访问权限
        filterMap.put("/toAdd", "perms[user:add]");
        filterMap.put("/toUpdate", "perms[user:update]");

        // 注意：要放在允许访问的页面后面，否则允许访问无效
        filterMap.put("/**", "authc");

        shiroFilterFactoryBean.setLoginUrl("/login");
        shiroFilterFactoryBean.setSuccessUrl("/index");
        // 未授权跳转页面
        shiroFilterFactoryBean.setUnauthorizedUrl("/error");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);
        return shiroFilterFactoryBean;
    }


    @Bean
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Autowired UserRealm userRealm){

        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

        // 关联 realm
        securityManager.setRealm(userRealm);
        return securityManager;
    }

    @Bean
    public UserRealm getRealm(){
        return new UserRealm();
    }

    /** 配置 ShiroDialect，用于 Thymeleaf 和 Shiro 标签配合使用 */
    @Bean
    public ShiroDialect getShiroDialect(){
        return new ShiroDialect();
    }
}
```

### 控制器类

```java
@Controller
public class UserController {

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/index")
    public String hello(Model model){
        Subject subject = SecurityUtils.getSubject();
        model.addAttribute("username", subject.getSession().getAttribute("username"));
        return "index";
    }

    @GetMapping("/toAdd")
    public String add(){
        return "/user/add";
    }

    @GetMapping("/toUpdate")
    public String update(){
        return "/user/update";
    }

    @GetMapping("/toExit")
    public String exit(){
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "redirect:/login";
    }

    @PostMapping("/login")
    public String login(String username, String password, Model model){
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username,password);
        try {
            Session session = subject.getSession();
            session.setAttribute("username", username);
            subject.login(token);
            return "redirect:/index";
        } catch (UnknownAccountException e) {
            model.addAttribute("msg", "用户名不存在");
            return "login";
        }catch (IncorrectCredentialsException e) {
            model.addAttribute("msg", "密码错误");
            return "login";
        }
    }
}
```

