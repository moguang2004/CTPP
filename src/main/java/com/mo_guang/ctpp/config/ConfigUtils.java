package com.mo_guang.ctpp.config;

public class ConfigUtils {
    // GTM模块配置访问
    public static boolean gtmEnabled(String machineName) {
        try {
            return MainConfig.INSTANCE.gtmConfig.getClass()
                .getField("enable" + machineName)
                .getBoolean(MainConfig.INSTANCE.gtmConfig);
        } catch (Exception e) {
            throw new RuntimeException("Invalid GTM config name: " + machineName, e);
        }
    }

    // CTNH模块配置访问
    public static boolean ctnhEnabled(String machineName) {
        try {
            return MainConfig.INSTANCE.ctnhConfig.getClass()
                .getField("enable" + machineName)
                .getBoolean(MainConfig.INSTANCE.ctnhConfig);
        } catch (Exception e) {
            throw new RuntimeException("Invalid CTNH config name: " + machineName, e);
        }
    }
}
