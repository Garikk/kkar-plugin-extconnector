/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kkdev.kksystem.plugin.extconnector.exconnmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import kkdev.kksystem.base.classes.plugins.PluginMessage;
import kkdev.kksystem.base.classes.plugins.simple.managers.PluginManagerBase;
import kkdev.kksystem.plugin.extconnector.KKPlugin;
import kkdev.kksystem.plugin.extconnector.adapters.IEXAdapter;
import kkdev.kksystem.plugin.extconnector.adapters.inet.EXAdapterInet;
import kkdev.kksystem.plugin.extconnector.adapters.local.EXAdapterBluetooth;
import kkdev.kksystem.plugin.extconnector.configuration.EXAdapterConfig;
import kkdev.kksystem.plugin.extconnector.configuration.EXAdapterMapping;
import kkdev.kksystem.plugin.extconnector.configuration.PluginSettings;

/**
 *
 * @author sayma_000
 */
public class EXConnManager extends PluginManagerBase {

    HashMap<String, IEXAdapter> Adapters;
    HashMap<String, List<String>> Mapping; // Plugin - Adapter

    public void Init(KKPlugin Conn) {
        Connector = Conn;

        PluginSettings.InitConfig(Conn.GlobalConfID, Conn.PluginInfo.GetPluginInfo().PluginUUID);
        //
        ConfigAndInitHW();
        //
        StartAdapters();
    }
    
    public void StartAdapters()
    {
        for (IEXAdapter AD:Adapters.values())
        {
            AD.SetActive();
        }
    }

    private void ConfigAndInitHW() {
        for (EXAdapterConfig AD : PluginSettings.MainConfiguration.Adapters) {
            if (AD.AdapterType == EXAdapterConfig.EXAdapter_Types.EXA_Internet) {
                Adapters.put(AD.AdapterID, new EXAdapterInet(AD));
            } else if (AD.AdapterType == EXAdapterConfig.EXAdapter_Types.EXA_Bluetooth) {
                Adapters.put(AD.AdapterID, new EXAdapterBluetooth(AD));
            }
        }
        
        for (EXAdapterMapping MP:PluginSettings.MainConfiguration.PluginMapping)
        {
            if (!Mapping.containsKey(MP.TargetAdapter))
            {
                Mapping.put(MP.SourcePlugin,new ArrayList<>());
            }
            Mapping.get(MP.SourcePlugin).add(MP.TargetAdapter);
        }
    }
    
    public void ReceivePin(PluginMessage Pin) {
        SendPinToAdapter(Pin);
    }
    
    private void SendPinToAdapter(PluginMessage Pin)
    {
        if (Mapping.containsKey(Pin.SenderUID))
        {
            for (String AD:Mapping.get(Pin.SenderUID))
            {
                Adapters.get(AD).ExecutePinCommand(Pin);
            }
        
        }
    }

}
