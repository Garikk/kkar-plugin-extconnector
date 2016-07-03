/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kkdev.kksystem.plugin.extconnector.adapters.json_pin;

import kkdev.kksystem.base.classes.plugins.PluginMessage;
import static kkdev.kksystem.base.constants.PluginConsts.KK_PLUGIN_BASE_BASIC_TAGGEDOBJ_DATA;
import kkdev.kksystem.plugin.extconnector.adapters.IEXAdapter;
import kkdev.kksystem.plugin.extconnector.adapters.SysExtLinkStates;
import kkdev.kksystem.plugin.extconnector.configuration.EXAdapterConfig;
import kkdev.kksystem.plugin.extconnector.exconnmanager.IEXConnManager;
import com.google.gson.Gson;
import kkdev.kksystem.base.classes.base.PinData;
import kkdev.kksystem.base.classes.base.PinDataTaggedObj;
import kkdev.kksystem.base.classes.base.PinDataTaggedString;
import kkdev.kksystem.base.classes.base.PluginMessageData;
import kkdev.kksystem.base.classes.controls.PinDataControl;
import kkdev.kksystem.base.classes.display.PinDataLed;
import kkdev.kksystem.base.classes.odb2.PinDataOdb2;
import kkdev.kksystem.base.constants.PluginConsts;
import static kkdev.kksystem.base.constants.SystemConsts.KK_BASE_FEATURES_SYSTEM_MULTIFEATURE_UID;

/**
 *
 * @author sayma_000
 */
public class EXAdapterJsonPin implements IEXAdapter {

    boolean Enabled;
    IEXConnManager ConnManager;
    EXAdapterConfig MyConf;
    Gson gson;

    public EXAdapterJsonPin(EXAdapterConfig Conf, IEXConnManager ConnectionManager) {
        gson = new Gson();
        MyConf = Conf;
        ConnManager = ConnectionManager;
    }

    @Override
    public PluginMessage ExecutePinCommand(PluginMessage PP) {
        if (!Enabled) {
            return null;
        }

        ProcessPIN(PP);
        //
        return null;
    }

    @Override
    public void SetActive() {
        Enabled = true;
    }

    @Override
    public void SetInactive() {
        Enabled = false;
    }

    @Override
    public void ReadPinCommands() {
        //this is active adapter, not need to manual read
    }

    @Override
    public int GetIntervalTune() {
        return 0;  //this is active adapter, not need to tune reading
    }

    @Override
    public void ExtSysLinkStateChange(SysExtLinkStates State) {
        //not need (apply only for passive connections)
    }

    private void ProcessPIN(PluginMessage PP) {
        if (PP.pinName.equals(KK_PLUGIN_BASE_BASIC_TAGGEDOBJ_DATA)) {
            ProcessTaggedPin((PluginMessageData)PP);
        } else {
            ProcessRegularPin(PP);
        }

    }

    private void ProcessTaggedPin(PluginMessageData PP) {
        PinDataTaggedObj ObjDat;

        ObjDat = (PinDataTaggedObj) PP.getPinData();

        if (!ObjDat.tag.equals(MyConf.PinTag)) {
            return;
        }
        PluginMessage PM;
        PM = (PluginMessage) gson.fromJson((String) ObjDat.value, PluginMessage.class);
        if (PM.pinName.equals(PluginConsts.KK_PLUGIN_BASE_ODB2_COMMAND)) {
            PM.setPinData(gson.fromJson((String) PM.getPinData(), PinDataOdb2.class));
        } else if (PM.pinName.equals(PluginConsts.KK_PLUGIN_BASE_ODB2_DATA)) {
            PM.setPinData(gson.fromJson((String) PM.getPinData(), PinDataOdb2.class));
        } else if (PM.pinName.equals(PluginConsts.KK_PLUGIN_BASE_CONTROL_COMMAND)) {
            PM.setPinData(gson.fromJson((String) PM.getPinData(), PinDataControl.class));
        }else if (PM.pinName.equals(PluginConsts.KK_PLUGIN_BASE_CONTROL_DATA)) {
            PM.setPinData(gson.fromJson((String) PM.getPinData(), PinDataControl.class));
        }else if (PM.pinName.equals(PluginConsts.KK_PLUGIN_BASE_LED_DATA)) {
            PM.setPinData(gson.fromJson((String) PM.getPinData(), PinDataLed.class));
        }else if (PM.pinName.equals(PluginConsts.KK_PLUGIN_BASE_PIN_COMMAND))
        {
            PM.setPinData(gson.fromJson((String)PM.getPinData(), PinData.class));
        }
        

        ConnManager.SendPIN_PluginMessage(PM.FeatureID, PM.pinName, PM.getPinData());
    }

    private void ProcessRegularPin(PluginMessage PP) {
        PinDataTaggedString Dat;
        Dat=new PinDataTaggedString();
        Dat.value=gson.toJson(PP);

        Dat.featureID.add(KK_BASE_FEATURES_SYSTEM_MULTIFEATURE_UID);
        Dat.tag=MyConf.PinTag;
        
        //
        ConnManager.SendPIN_PluginMessage(KK_BASE_FEATURES_SYSTEM_MULTIFEATURE_UID,KK_PLUGIN_BASE_BASIC_TAGGEDOBJ_DATA,Dat);
    }

}
