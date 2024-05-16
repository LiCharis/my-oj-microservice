### 下面是整个系统后端的架构设计，所涉及的模块以及各模块之间的关系。
整个系统的后端分为:
1. 用户服务:提供用户登录、用户的增删改查等管理功能
2. 题目服务:提供题目的增删改查管理、题目提交功能
3. 判题服务:提供判题功能，调用代码沙箱并比对判题结果
4. 代码沙箱:提供编译执行代码、返回结果的功能
5. 公共模块:提供公共代码，比如数据模型、全局请求响应封装、全局异常处理、工具类等
6. 网关服务:提供统一的 API转发、聚合文档、全局跨域解决等功能
各模块之间的关系，3个核心:
1. 由网关服务集中接受前端的请求，并转发到对应的业务服务
2. 判题服务需要调用题目服务获取题目信息和测试用例、调用代码沙箱完成代码的编译和执行并比对结果，服务间通过 Open Feign 相互调用。
3. 所有服务都需要引入公共模块。

### 下面是判题机模块的架构方式以及代码沙箱的抽象调用接口和实现类。
判题机模块的作用是:查询题目提交和题目信息，调用代码沙箱，把代码和输入用例交给代码沙箱去执行，收集输出结果并执行判题逻辑.
为了设计灵活、可扩展的判题机模块，我用了多种设计模式和方法1)为了提高系统的灵活性，对于代码沙箱的调用，我没有选择硬编码的方式指定单一的代码沙箱，而是定义了一个通用的代码沙箱调用接口，并且提供了多种代码沙箱调用的实现类，比如:
示例代码沙箱:仅为了跑通业务流程
远程代码沙箱:调用自己开发的沙箱接口
第三方代码沙箱:调用网上现成的代码沙箱服务
1. 为了简化代码沙箱调用实例的获取，我使用工厂模式，根据代码沙箱的类型字符串来生成对应的代码沙箱调用实现类。
2. 为了解决代码沙箱硬编码的问题，我将代码沙箱的类型字符串配置化，可以通过更改 application.yml 中的配置动态切换调用的代码沙箱。
3. 为了在调用代码沙箱前后进行统一的日志操作，我使用代理模式对代码沙箱进行封装，能够在不改变代码沙箱调用实现类的前提下，集中地增强代码沙箱的能力。

### OpenFeign的介绍及在微服务项目中的体现方式，以及需要使用 OpenFeign调用的接口。
Open Feign 是一个声明式 Web 客户端调用库，它简化了创建 HTTP 请求的过程，并允许你将 HTTP 请求映射到 Java 接口的方法上。
使用它后，可以更方便地调用远程的 API服务，无需自己手动编写 HTTP 请求，提高开发效率本项目中，我通过 IDEA 开发工具的 Find Usages 功能快速定位了服务的调用关系，并且梳理了服务调用依赖表，总共有以下接口需要 OpenFeign 调用:
用户服务:
userService.getByld(userld)
userService.listByds(userldSet)
题目服务:
questionService.getByld(questionld)
questionSubmitService.getByld(questionSubmitld)
questionSubmitService.updateByld(questionSubmitUpdate)判题服务:
judgeService.doJudge(questionSubmitld)

### 通过自定义 Gateway的 GlobalFilter 来保护接口的，体实现过程如下：
背景:为防止服务内部相互调用的接口被外部系统访问，需要在网关集中定义接口保护逻辑具体实现:GlobalFilter 是 Spring Cloud Gateway 提供的全局请求拦载接口，我编写了一个类实现了该接口，重写了其 filter 方法，并在该方法中编写了接口保护逻辑。接口保护逻辑:获取到当前请求的目标路径，使用 AntPathMatcher 判断该路径是否包含 inner，如果包含的话，设置响应码为 403 FORBIDDEN 并返回:不包会的话，放行请求继续执行值得一提的是，为了让上述逻辑优先生效，我让这个类实现了 Ordered 接口并目将 getOrder 方法的返回值设置为0(最高优先级)，能够做到尽早拦截违规请求，避免不必要的开销。

### 项目中实现了接口文档的统一聚合，具体的实现过程如下：
使用 Knife4j 接囗文档生成器，参考官方文档完成了聚合:https://doc.xiaominfo.com/docs/middleware-sources/spring-cloud-gateway/spring-gateway-introduction具体步骤如下:
1.先给所有业务服务引入 Knife4j 依赖，同时在配置文件中开启接口文档
给网关服务引入 Knife4j Gateway 依赖，并且通过在配置文件中指定 discover 模式，让系统自动查找业务服2务的文档、自动完成聚合
3.访问网关的 doc.html 地址，即可查看聚合接口文档
