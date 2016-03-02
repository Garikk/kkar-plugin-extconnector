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
import static java.lang.System.out;
import kkdev.kksystem.base.classes.base.PinBaseData;
import kkdev.kksystem.base.classes.base.PinBaseDataTaggedObj;
import kkdev.kksystem.base.classes.controls.PinControlData;
import kkdev.kksystem.base.classes.odb2.PinOdb2Command;
import kkdev.kksystem.base.classes.odb2.PinOdb2Data;
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
        if (PP.PinName == KK_PLUGIN_BASE_BASIC_TAGGEDOBJ_DATA) {
            ProcessTaggedPin(PP);
        } else {
            ProcessRegularPin(PP);
        }

    }

    private void ProcessTaggedPin(PluginMessage PP) {
        PinBaseDataTaggedObj ObjDat;

        ObjDat = (PinBaseDataTaggedObj) PP.PinData;

        if (!ObjDat.Tag.equals(MyConf.PinTag)) {
            return;
        }
        PluginMessage PM;
        PM = (PluginMessage) gson.fromJson((String) ObjDat.Value, PluginMessage.class);
        if (PM.PinName.equals(PluginConsts.KK_PLUGIN_BASE_ODB2_COMMAND)) {
            PM.PinData = gson.fromJson((String) PM.PinData, PinOdb2Command.class);
        } else if (PM.PinName.equals(PluginConsts.KK_PLUGIN_BASE_ODB2_DATA)) {
            PM.PinData =  gson.fromJson((String) PM.PinData, PinOdb2Data.class);
        } else if (PM.PinName.equals(PluginConsts.KK_PLUGIN_BASE_CONTROL_COMMAND)) {
            PM.PinData =  gson.fromJson((String) PM.PinData, PinControlData.class);
        }else if (PM.PinName.equals(PluginConsts.KK_PLUGIN_BASE_CONTROL_DATA)) {
            PM.PinData =  gson.fromJson((String) PM.PinData, PinControlData.class);
        }

        ConnManager.SendPIN_PluginMessage(PM.FeatureID, PM.PinName, PM.PinData);
    }

    private void ProcessRegularPin(PluginMessage PP) {
        PinBaseDataTaggedObj Dat;
        Dat=new PinBaseDataTaggedObj();
        PP.PinData=gson.toJson(PP.PinData);
        Dat.Value=gson.toJson(PP);
        Dat.FeatureID=KK_BASE_FEATURES_SYSTEM_MULTIFEATURE_UID;
        Dat.DataType=PinBaseData.BASE_DATA_TYPE.TAGGED_OBJ;
        Dat.Tag=MyConf.PinTag;
        
        //
        ConnManager.SendPIN_PluginMessage(KK_BASE_FEATURES_SYSTEM_MULTIFEATURE_UID,KK_PLUGIN_BASE_BASIC_TAGGEDOBJ_DATA,Dat);
    }

}
