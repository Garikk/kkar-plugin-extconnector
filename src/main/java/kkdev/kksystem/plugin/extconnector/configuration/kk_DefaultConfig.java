/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kkdev.kksystem.plugin.extconnector.configuration;

import static kkdev.kksystem.base.constants.PluginConsts.KK_PLUGIN_BASE_PLUGIN_LEDDISPLAY_UUID;

public abstract class kk_DefaultConfig {

    public static EXConfig MakeDefaultConfig() {
        EXConfig DefConfig = new EXConfig();

        DefConfig.Adapters = new EXAdapterConfig[1];

        EXAdapterConfig AD = new EXAdapterConfig();

        AD.AdapterID="KKMASTER_INET";
        AD.AdapterName="KK Master Site";
        AD.AdapterType=EXAdapterConfig.EXAdapter_Types.EXA_Internet;
        AD.Inet_ExService="extconnector/extconnector";
        AD.Inet_ServerHost="supergarikk.dlinkddns.com";
        AD.Inet_ServerPort=80;
        
        DefConfig.Adapters[0]=AD;
        
        DefConfig.PluginMapping=new EXAdapterMapping[1];
        
        EXAdapterMapping AM=new EXAdapterMapping();
        AM.SourcePlugin=KK_PLUGIN_BASE_PLUGIN_LEDDISPLAY_UUID;
        AM.TargetAdapter="KKMASTER_INET";
        
        DefConfig.PluginMapping[0]=AM;
        
        
        return DefConfig;
    }

}
