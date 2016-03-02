/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kkdev.kksystem.plugin.extconnector.configuration;

import static kkdev.kksystem.base.constants.PluginConsts.KK_PLUGIN_BASE_PLUGIN_BLUETOOTH_UUID;
import static kkdev.kksystem.base.constants.PluginConsts.KK_PLUGIN_BASE_PLUGIN_HID;
import static kkdev.kksystem.base.constants.PluginConsts.KK_PLUGIN_BASE_PLUGIN_LEDDISPLAY_UUID;
import static kkdev.kksystem.base.constants.PluginConsts.KK_PLUGIN_BASE_PLUGIN_ODB2_UUID;

public abstract class kk_DefaultConfig {

    public static EXConfig MakeDefaultConfig() {
        EXConfig DefConfig = new EXConfig();

        DefConfig.Adapters = new EXAdapterConfig[2];

        EXAdapterConfig AD = new EXAdapterConfig();

        AD.AdapterID="KKMASTER_INET";
        AD.AdapterName="KK Master Site";
        AD.AdapterType=EXAdapterConfig.EXAdapter_Types.EXA_Internet;
        AD.Inet_ExService="extconnector/extconnector";
        AD.Inet_ServerHost="kkdev-kkcar.tk";
        AD.Inet_ServerPort=80;
        AD.Passive=true;
        //
        DefConfig.Adapters[0]=AD;
        //
        AD = new EXAdapterConfig();

        AD.AdapterID="BT_EXCONNECTOR";
        AD.AdapterName="Bluetooth EX Connector";
        AD.AdapterType=EXAdapterConfig.EXAdapter_Types.EXA_Plugin_TaggedPin_Json_KKPin;
        AD.PinTag="BTEXACONNECTOR";
        AD.Passive=false;
        //
        DefConfig.Adapters[1]=AD;
        //
        
        
        DefConfig.PluginMapping=new EXAdapterMapping[4];
        
        EXAdapterMapping AM=new EXAdapterMapping();
        AM.SourcePlugin=KK_PLUGIN_BASE_PLUGIN_LEDDISPLAY_UUID;
        AM.TargetAdapter="KKMASTER_INET";
        
        DefConfig.PluginMapping[0]=AM;
        
        AM=new EXAdapterMapping();
        AM.SourcePlugin=KK_PLUGIN_BASE_PLUGIN_BLUETOOTH_UUID;
        AM.TargetAdapter="BT_EXCONNECTOR";
        
        DefConfig.PluginMapping[1]=AM;
         
        AM=new EXAdapterMapping();
        AM.SourcePlugin=KK_PLUGIN_BASE_PLUGIN_ODB2_UUID;
        AM.TargetAdapter="BT_EXCONNECTOR";
        
        DefConfig.PluginMapping[2]=AM;
        
        AM=new EXAdapterMapping();
        AM.SourcePlugin=KK_PLUGIN_BASE_PLUGIN_HID;
        AM.TargetAdapter="BT_EXCONNECTOR";
        
        DefConfig.PluginMapping[3]=AM;
        return DefConfig;
    }

}
