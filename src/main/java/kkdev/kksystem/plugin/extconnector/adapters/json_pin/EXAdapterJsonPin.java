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
import kkdev.kksystem.base.classes.base.PinBaseDataTaggedObj;

/**
 *
 * @author sayma_000
 */
public class EXAdapterJsonPin implements IEXAdapter {
    boolean Enabled;
    IEXConnManager ConnManager;
    EXAdapterConfig MyConf;
    Gson gson;
   
   public EXAdapterJsonPin(EXAdapterConfig Conf,IEXConnManager ConnectionManager)
    {
        gson=new Gson();
        MyConf=Conf;
        ConnectionManager=ConnManager;
    }

    @Override
    public PluginMessage ExecutePinCommand(PluginMessage PP) {
        if (!Enabled)
            return null;
                
        ProcessPIN(PP);
        //
        return null;
    }

    @Override
    public void SetActive() {
        Enabled=true;
    }

    @Override
    public void SetInactive() {
        Enabled=false;
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

    private void ProcessPIN(PluginMessage PP)
    {
        if (PP.PinName==KK_PLUGIN_BASE_BASIC_TAGGEDOBJ_DATA)
        {
            ProcessTaggedPin(PP);
        }
        else
        {
            ProcessRegularPin(PP);
        }
    
    }
    
    private void ProcessTaggedPin(PluginMessage PP)
    {
          PinBaseDataTaggedObj ObjDat;
          
          ObjDat=(PinBaseDataTaggedObj)PP.PinData;
        
          if (ObjDat.Tag!=MyConf.PinTag)
              return;
          
        PluginMessage PM;
  
        PM = (PluginMessage)gson.fromJson((String)ObjDat.Value, PluginMessage.class);
        ConnManager.SendPIN_PluginMessage(PM.FeatureID,PM.PinName, PM.PinData);
               
        
    }
    
    private void ProcessRegularPin(PluginMessage PP)
    {
        String Json;
        Json = gson.toJson(PP);
        //
        PluginMessage PM=new PluginMessage();
        PM.PinName=KK_PLUGIN_BASE_BASIC_TAGGEDOBJ_DATA;
        PM.PinData=PP;
        //
        ConnManager.ExecPINCommand(PM);
    }

}
