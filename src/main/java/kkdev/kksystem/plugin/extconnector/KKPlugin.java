package kkdev.kksystem.plugin.extconnector;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import kkdev.kksystem.base.classes.plugins.PluginMessage;
import kkdev.kksystem.base.classes.plugins.simple.KKPluginBase;
import kkdev.kksystem.base.interfaces.IPluginBaseInterface;
import kkdev.kksystem.plugin.extconnector.exconnmanager.EXConnManager;

/**
 *
 * @author blinov_is
 */
public final class KKPlugin extends KKPluginBase {
    public KKPlugin() {
        super(new ECPluginInfo());
        Global.EM=new EXConnManager();
    }

    @Override
    public void PluginInit(IPluginBaseInterface BaseConnector, String GlobalConfUID) {
        Global.EM.Init(this);
        super.PluginInit(BaseConnector, GlobalConfUID); 
    }

   
    @Override
    public PluginMessage ExecutePin(PluginMessage Pin) {
        Global.EM.ReceivePin(Pin);
        super.ExecutePin(Pin);
 
        return null;
    }
}
