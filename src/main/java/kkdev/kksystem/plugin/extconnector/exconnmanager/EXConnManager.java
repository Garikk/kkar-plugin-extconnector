/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kkdev.kksystem.plugin.extconnector.exconnmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import kkdev.kksystem.base.classes.base.PinData;
import kkdev.kksystem.base.classes.base.PinDataTaggedObj;
import kkdev.kksystem.base.classes.notify.PinDataNotifySystemState;
import kkdev.kksystem.base.classes.plugins.PluginMessage;
import kkdev.kksystem.base.classes.plugins.simple.managers.PluginManagerBase;
import kkdev.kksystem.base.constants.PluginConsts;
import kkdev.kksystem.base.constants.SystemConsts;
import static kkdev.kksystem.base.constants.SystemConsts.KK_BASE_FEATURES_SYSTEM_MULTIFEATURE_UID;
import kkdev.kksystem.plugin.extconnector.KKPlugin;
import kkdev.kksystem.plugin.extconnector.adapters.IEXAdapter;
import kkdev.kksystem.plugin.extconnector.adapters.SysExtLinkStates;
import kkdev.kksystem.plugin.extconnector.adapters.inet.EXAdapterInet;
import kkdev.kksystem.plugin.extconnector.adapters.json_pin.EXAdapterJsonPin;
import kkdev.kksystem.plugin.extconnector.configuration.EXAdapterConfig;
import kkdev.kksystem.plugin.extconnector.configuration.EXAdapterMapping;
import kkdev.kksystem.plugin.extconnector.configuration.PluginSettings;

/**
 *
 * @author sayma_000
 */
public class EXConnManager extends PluginManagerBase implements IEXConnManager {

    HashMap<String, IEXAdapter> Adapters;
    HashMap<String, List<String>> Mapping; // Plugin - Adapter

    public void Init(KKPlugin Conn) {
        setPluginConnector(Conn);
        Mapping = new HashMap<>();
        Adapters = new HashMap<>();
        //
        currentFeature.put(SystemConsts.KK_BASE_UICONTEXT_DEFAULT, KK_BASE_FEATURES_SYSTEM_MULTIFEATURE_UID);
        //
        PluginSettings.InitConfig(Conn.globalConfID, Conn.pluginInfo.getPluginInfo().PluginUUID);
        //
        ConfigAndInitHW();
        //
        StartAdapters();
        //
        EXConnReader.start();
    }

    public void StartAdapters() {
        for (IEXAdapter AD : Adapters.values()) {
            AD.SetActive();
        }
    }

    private void ConfigAndInitHW() {
        for (EXAdapterConfig AD : PluginSettings.MainConfiguration.Adapters) {
            if (AD.AdapterType == EXAdapterConfig.EXAdapter_Types.EXA_Internet) {
                Adapters.put(AD.AdapterID, new EXAdapterInet(AD, this));
            } else if (AD.AdapterType == EXAdapterConfig.EXAdapter_Types.EXA_Plugin_TaggedPin_Json_KKPin) {
                Adapters.put(AD.AdapterID, new EXAdapterJsonPin(AD, this));
            }
        }

        for (EXAdapterMapping MP : PluginSettings.MainConfiguration.PluginMapping) {
            if (!Mapping.containsKey(MP.TargetAdapter)) {
                Mapping.put(MP.SourcePlugin, new ArrayList<>());
            }
            Mapping.get(MP.SourcePlugin).add(MP.TargetAdapter);
        }
    }

    public void ReceivePin(PluginMessage Pin) {
        SendPinToAdapter(Pin);
    }

    private void SendPinToAdapter(PluginMessage Pin) {
        
        if (Pin.pinName.equals(PluginConsts.KK_PLUGIN_BASE_PIN_COMMAND)) {
            ProcessSystemCommand(Pin);
        }
        //out.println("[EXA] Receive PIN to Adapter " + Pin.SenderUID + " " + Pin.PinName);
        if (Mapping.containsKey(Pin.SenderUID)) {
            for (String AD : Mapping.get(Pin.SenderUID)) {
                //out.println("[EXA] Send PIN "+Pin.PinName+" to Adapter " + AD);
                Adapters.get(AD).ExecutePinCommand(Pin);
            }
        }
    }

    @Override
    public void ExecPINCommand(PluginMessage PM) {
        getPluginConnector().executePin(PM);
    }

    private void ProcessSystemCommand(PluginMessage Pin) {
        switch (Pin.pinName) {
            case PluginConsts.KK_PLUGIN_BASE_CONTROL_COMMAND:
                PinDataNotifySystemState PBK = (PinDataNotifySystemState) Pin.getPinData();
                if (PBK.systemState==PinDataNotifySystemState.SystemStateInfo.INERNET_ACTIVE) {
                    AlertStateChange(new SysExtLinkStates(true, false, false, false));
                } else if (PBK.systemState==PinDataNotifySystemState.SystemStateInfo.INERNET_ACTIVE) {
                    AlertStateChange(new SysExtLinkStates());
                }
                break;
        }
    }

    private void AlertStateChange(SysExtLinkStates State) {
        for (IEXAdapter A : Adapters.values()) {
            A.ExtSysLinkStateChange(State);
        }
    }

    Thread EXConnReader = new Thread(new Runnable() {
        public void run() {
            int Interval = 1000;
            int IntervalMax = 10000;
            int IntervalMin = 500;
            int IntervalTuneStep = 500;
            int TuneStep = 0;

            Boolean Stop = false;

            while (!Stop) {

                TuneStep = IntervalTuneStep;

                for (IEXAdapter AD : Adapters.values()) {
                    AD.ReadPinCommands();

                    if (AD.GetIntervalTune() == 1) {
                        Interval = IntervalMin;
                        IntervalTuneStep = 0;
                    }

                }
                if (Interval <= IntervalMax) {
                    Interval += TuneStep;
                }

                try {
                    Thread.sleep(Interval);
                } catch (InterruptedException ex) {
                    Logger.getLogger(EXConnManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    });

    @Override
    public void SendPIN_ObjPin(String Tag, Object Data) {
           SendPIN_ObjPin(Tag,(PinData)Data);      
    }

        @Override
    public void SendPIN_PluginMessage(String FeatureID, String PinName, PinData Data) {
       this.BASE_SendPluginMessage(FeatureID,SystemConsts.KK_BASE_UICONTEXT_DEFAULT, PinName, Data);
    }

    @Override
    public void SendPIN_PluginMessage(Set<String> FeatureID, String PinName, PinData Data) {
        this.BASE_SendPluginMessage(FeatureID,SystemConsts.KK_BASE_UICONTEXT_DEFAULT, PinName, Data);
    }
        @Override
    public void SendPIN_PluginMessage(Set<String> FeatureID, String PinName, Object Data) {
         this.BASE_SendPluginMessage(FeatureID,SystemConsts.KK_BASE_UICONTEXT_DEFAULT, PinName, (PinData)Data);
    }

    @Override
    public void SendPIN_ObjPin(String Tag, PinData Data) {

       
         PinDataTaggedObj ObjDat;
        ObjDat = new PinDataTaggedObj();
        ObjDat.tag = Tag;
        ObjDat.value = Data;

        this.BASE_SendPluginMessage(this.currentFeature.get(SystemConsts.KK_BASE_UICONTEXT_DEFAULT),SystemConsts.KK_BASE_UICONTEXT_DEFAULT, PluginConsts.KK_PLUGIN_BASE_BASIC_TAGGEDOBJ_DATA, ObjDat);

    }




}
