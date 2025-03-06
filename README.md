# CTPP-Goblin

## 介绍
一个用于制造GoblinTech次世代柴油机的ct++分支版本
## 0.0.5更新内容
1. 完全实装了配置文件，可以通过模组设置来显示或隐藏机器，或者调整某些机器的参数，并完成了本地化（~~但蒸汽涡轮齿轮箱未实现，所以那个配置选项没用~~）    
2. 给使用机械动力机壳的多方块机器实现了链接纹理（~~应该不会有人想要个取消链接纹理的功能吧~~）  
3. 新增条件注册系统 - 通过 `CTPPRegistration.conditionalRegistration()` 实现配置驱动的机器注册（~~但是是手打的，以后选项多了还得一个个打~~）：
```java
// 核心方法
public static <T> T conditionalRegistration(boolean enable, Supplier<T> registration)

// 配置访问助手（需配合使用）
public static boolean ConfigUtils.gtmEnabled(String configName)    // 访问GTM模块配置
public static boolean ConfigUtils.ctnhEnabled(String configName)   // 访问CTNH模块配置

// 使用示例
public static MachineDefinition MY_MACHINE = CTPPRegistration.conditionalRegistration(
    ConfigUtils.gtmEnabled("MyMachine"), // 检查配置项 enableMyMachine
    () -> REGISTRATE.multiblock(...).register()
);
```  
to do:  
根据机械动力的纹理来执行链接纹理  
适配机械动力6.0  
添加ponder  