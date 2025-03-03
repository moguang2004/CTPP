# CTPP-Goblin

## Introduction 
A ct++ fork for crafting next-generation GoblinTech diesel engines

## 0.0.5 Changelog
1. Fully implemented config system with in-game GUI support:
   - Machine visibility control ~~(Steam turbine gearbox not implemented, related config disabled)~~  
   - Parameter adjustments  
   - Localization completed   
   
2. Connected textures for Create-style multiblock casings ~~(No plans for disable option)~~

3. Conditional registration system via `CTPPRegistration.conditionalRegistration()`:
```java
// Core method
public static <T> T conditionalRegistration(boolean enable, Supplier<T> registration)

// Config helpers
public static boolean ConfigUtils.gtmEnabled(String configName)    // GTM module
public static boolean ConfigUtils.ctnhEnabled(String configName)   // CTNH module

// Usage
public static MachineDefinition MY_MACHINE = CTPPRegistration.conditionalRegistration(
    ConfigUtils.gtmEnabled("MyMachine"), // Checks enableMyMachine config
    () -> REGISTRATE.multiblock(...).register()
);
```