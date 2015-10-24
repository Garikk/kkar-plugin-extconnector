/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kkdev.kksystem.plugin.extconnector.adapters.local;

import kkdev.kksystem.base.classes.plugins.PluginMessage;
import kkdev.kksystem.plugin.extconnector.adapters.IEXAdapter;
import kkdev.kksystem.plugin.extconnector.configuration.EXAdapterConfig;

/**
 *
 * @author sayma_000
 */
public class EXAdapterBluetooth implements IEXAdapter {
   public EXAdapterBluetooth(EXAdapterConfig Conf)
    {
    }

    @Override
    public PluginMessage ExecutePinCommand(PluginMessage PP) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void SetActive() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void SetInactive() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
