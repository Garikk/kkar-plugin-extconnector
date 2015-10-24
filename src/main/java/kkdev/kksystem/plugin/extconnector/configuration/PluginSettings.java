/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kkdev.kksystem.plugin.extconnector.configuration;

import kkdev.kksystem.base.classes.plugins.simple.SettingsManager;

/**
 *
 * @author blinov_is
 */
public abstract class PluginSettings {

   public static String HID_CONF;
   private static SettingsManager Settings;

    public static EXConfig MainConfiguration;

    public static void InitConfig(String GlobalConfigUID, String MyUID) {
        //
        HID_CONF=GlobalConfigUID+"_"+MyUID + ".json";
        //
        
        Settings=new SettingsManager(HID_CONF,EXConfig.class);
        
        
        System.out.println("[EXA][CONFIG] Load configuration");
        MainConfiguration=(EXConfig)Settings.LoadConfig();

        if (MainConfiguration == null) {
            System.out.println("[EXA][CONFIG] Error Load configuration, try create default config");
            Settings.SaveConfig(kk_DefaultConfig.MakeDefaultConfig());
            MainConfiguration=(EXConfig)Settings.LoadConfig();
        }
        if (MainConfiguration == null) {
            System.out.println("[EXA][CONFIG] Load configuration, fatal");
            return;
        }
        //
    }
}
