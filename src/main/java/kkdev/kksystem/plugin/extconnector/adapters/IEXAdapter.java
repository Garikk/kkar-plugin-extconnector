/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kkdev.kksystem.plugin.extconnector.adapters;

import kkdev.kksystem.base.classes.plugins.PluginMessage;

/**
 *
 * @author sayma_000
 */
public interface IEXAdapter {
    public void SetActive();
    public void SetInactive();
    public  PluginMessage ExecutePinCommand(PluginMessage PP);
    public void ReadPinCommands();
    public int GetIntervalTune();
}
