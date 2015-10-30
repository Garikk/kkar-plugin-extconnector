/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kkdev.kksystem.plugin.extconnector.configuration;

/**
 *
 * @author sayma_000
 */
public class EXAdapterConfig {
    public enum EXAdapter_Types
    {
        EXA_Bluetooth,
        EXA_Internet
        
    }
    public EXAdapter_Types AdapterType;
    
    public String AdapterName;
    public String AdapterID;
    
    public String Inet_ServerHost;
    public int Inet_ServerPort;
    public String Inet_ExService;
    
    public String BT_DeviceName;
}
