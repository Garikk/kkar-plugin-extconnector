package kkdev.kksystem.plugins.bluetooth;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import kkdev.kksystem.base.constants.PluginConsts.KK_PLUGIN_TYPE;
import kkdev.kksystem.base.classes.plugins.PluginInfo;
import kkdev.kksystem.base.classes.plugins.simple.IPluginInfoRequest;
import kkdev.kksystem.base.constants.PluginConsts;

/**
 *
 * @author blinov_is e
 */
public class BTPluginInfo  implements IPluginInfoRequest {
    @Override
    public PluginInfo GetPluginInfo()
    {
        PluginInfo Ret=new PluginInfo();
        
        Ret.PluginName="KKBTAdapter";
        Ret.PluginDescription="Basic Bluetooth adapter plugin";
        Ret.PluginType = KK_PLUGIN_TYPE.PLUGIN_PROCESSOR;
        Ret.PluginVersion=1;
        Ret.Enabled=true;
        Ret.ReceivePins = GetMyReceivePinInfo();
        Ret.TransmitPins = GetMyTransmitPinInfo();
        Ret.PluginUUID="51534a41-4200-40f6-adac-1a915678cde9";
        return Ret;
    }
    
    
    private String[] GetMyReceivePinInfo(){
    
        String[] Ret=new String[1];

        Ret[0]=PluginConsts.KK_PLUGIN_BASE_LED_DATA;
        
        return Ret;
    }
    private String[] GetMyTransmitPinInfo(){
    
        String[] Ret=new String[1];
    
        Ret[0]=PluginConsts.KK_PLUGIN_BASE_CONTROL_DATA;
        
        return Ret;
    }
    
}
