/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kkdev.kksystem.plugin.extconnector.adapters.inet;

import kkdev.kksystem.base.classes.plugins.PluginMessage;
import kkdev.kksystem.plugin.extconnector.adapters.IEXAdapter;
import kkdev.kksystem.plugin.extconnector.configuration.EXAdapterConfig;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 *
 * @author sayma_000
 */
public class EXAdapterInet implements IEXAdapter{
    EXAdapterConfig Configuration;
    public EXAdapterInet(EXAdapterConfig Conf)
    {
        Configuration=Conf;
    }
    
    @Override
    public PluginMessage ExecutePinCommand(PluginMessage PP) {
        return null;
      
    }
    
    @Override
    public void SetActive() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void SetInactive() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void SendPinData()
    {
          HttpClient client = HttpClientBuilder.create().build();
    }
            
  
}
