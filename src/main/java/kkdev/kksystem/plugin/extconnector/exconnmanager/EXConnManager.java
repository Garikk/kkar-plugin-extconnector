/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kkdev.kksystem.plugin.extconnector.exconnmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import kkdev.kksystem.base.classes.base.PinBaseCommand;
import kkdev.kksystem.base.classes.base.PinBaseData;
import kkdev.kksystem.base.classes.base.PinBaseDataTaggedObj;
import kkdev.kksystem.base.classes.plugins.PluginMessage;
import kkdev.kksystem.base.classes.plugins.simple.managers.PluginManagerBase;
import kkdev.kksystem.base.constants.PluginConsts;
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
        Connector = Conn;
        Mapping=new HashMap<>();
        Adapters=new HashMap<>();

        PluginSettings.InitConfig(Conn.GlobalConfID, Conn.PluginInfo.GetPluginInfo().PluginUUID);
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
                Adapters.put(AD.AdapterID, new EXAdapterJsonPin(AD,this));
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
        if (Pin.PinName.equals(PluginConsts.KK_PLUGIN_BASE_PIN_COMMAND))
        {
            ProcessSystemCommand(Pin);
        }
        
        if (Mapping.containsKey(Pin.SenderUID)) {
            for (String AD : Mapping.get(Pin.SenderUID)) {
                Adapters.get(AD).ExecutePinCommand(Pin);
            }
        }
    }

    @Override
    public void ExecPINCommand(PluginMessage PM) {
        Connector.ExecutePin(PM);
    }

    private void ProcessSystemCommand(PluginMessage Pin)
    {
        switch (Pin.PinName)
        {
            case PluginConsts.KK_PLUGIN_BASE_CONTROL_COMMAND:
                PinBaseCommand PBK = (PinBaseCommand)Pin.PinData;
                if (PBK.BaseCommand==PinBaseCommand.BASE_COMMAND_TYPE.INTERNET_STATE_ACTIVE )
                    AlertStateChange(new SysExtLinkStates(true,false,false));
                else if  (PBK.BaseCommand==PinBaseCommand.BASE_COMMAND_TYPE.INTERNET_STATE_INACTIVE)
                    AlertStateChange(new SysExtLinkStates());
                break;
        }
    }
    
    private void AlertStateChange(SysExtLinkStates State)
    {
        for (IEXAdapter A:Adapters.values())
        {
            A.ExtSysLinkStateChange(State);
        }
    }
    
    
    Thread EXConnReader = new Thread(new Runnable() {
        public void run() 
        {
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
        PinBaseDataTaggedObj ObjDat;
        ObjDat=new PinBaseDataTaggedObj();
        ObjDat.DataType=PinBaseData.BASE_DATA_TYPE.TAGGED_OBJ;
        ObjDat.Tag=Tag;
        ObjDat.Value=Data;
        
        this.BASE_SendPluginMessage(this.CurrentFeature, PluginConsts.KK_PLUGIN_BASE_BASIC_TAGGEDOBJ_DATA, ObjDat);
    }

    @Override
    public void SendPIN_PluginMessage(String FeatureID, String PinName, Object Data) {
       this.BASE_SendPluginMessage(FeatureID, PinName, Data);
    }

}
