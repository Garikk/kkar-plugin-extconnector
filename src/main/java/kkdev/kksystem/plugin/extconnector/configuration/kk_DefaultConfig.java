/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kkdev.kksystem.plugin.extconnector.configuration;

public abstract class kk_DefaultConfig {

    public static EXConfig MakeDefaultConfig() {
        EXConfig DefConfig = new EXConfig();

        DefConfig.Adapters = new EXAdapterConfig[1];

        EXAdapterConfig AD = new EXAdapterConfig();

        AD.AdapterID="KKMASTER_INET";
        AD.AdapterName="KK Master Site";
        AD.Inet_ServerHost="supergarikk.dlinkddns.com";
        AD.Inet_ServerPort=80;
        
        DefConfig.Adapters[0]=AD;
        
        return DefConfig;
    }

}
