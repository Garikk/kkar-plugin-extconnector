/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kkdev.kksystem.plugin.extconnector.adapters.inet;

import com.google.gson.Gson;
import kkdev.kksystem.base.classes.plugins.PluginMessage;


/**
 *
 * @author sayma_000
 */
public class WM_EXConn_PinData {
    public PluginMessage[] PinData;
    public String GetJson()
    {
         Gson gson = new Gson();

            return gson.toJson(PinData);
    }
      public void FromJson(String JsonData)
    {
         Gson gson = new Gson();

         PinData=gson.fromJson(JsonData, PinData.getClass());
    }
}
