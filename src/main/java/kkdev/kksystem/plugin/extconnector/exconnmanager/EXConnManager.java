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
import static kkdev.kksystem.base.classes.controls.PinControlData.DEF_BTN_BACK;
import static kkdev.kksystem.base.classes.controls.PinControlData.DEF_BTN_DOWN;
import static kkdev.kksystem.base.classes.controls.PinControlData.DEF_BTN_ENTER;
import static kkdev.kksystem.base.classes.controls.PinControlData.DEF_BTN_UP;
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
            } else if (AD.AdapterType == EXAdapterConfig.EXAdapter_Types.EXA_Bluetooth) {
                Adapters.put(AD.AdapterID, new EXAdapterBluetooth(AD));
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

    Thread EXConnReader = new Thread(new Runnable() {
        public void run() //Этот метод будет выполняться в побочном потоке
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

}
