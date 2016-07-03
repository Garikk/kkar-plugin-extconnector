/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kkdev.kksystem.plugin.extconnector.exconnmanager;

import java.util.Set;
import kkdev.kksystem.base.classes.base.PinData;
import kkdev.kksystem.base.classes.plugins.PluginMessage;

/**
 *
 * @author blinov_is
 */
public interface IEXConnManager {
    public void ExecPINCommand(PluginMessage PM);
    public void SendPIN_ObjPin(String Tag,PinData Data);
    public void SendPIN_ObjPin(String Tag,Object Data);
    public void SendPIN_PluginMessage(String FeatureID,String PinName, PinData Data);
    public void SendPIN_PluginMessage(Set<String> FeatureID,String PinName, PinData Data);
    public void SendPIN_PluginMessage(Set<String> FeatureID,String PinName, Object Data);
}
