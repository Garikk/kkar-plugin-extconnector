/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kkdev.kksystem.plugin.extconnector.adapters;

/**
 *
 * @author blinov_is
 */
public class SysExtLinkStates {

    public boolean SystemInternetstate ;
    public boolean SystemWiFiState ;
    public boolean SystemBluetoothState ;
    public boolean SystemRabbitMQLink ;

    public SysExtLinkStates(boolean Inet, boolean Wifi, boolean BT, boolean RMQ) {
        SystemInternetstate = Inet;
        SystemWiFiState = Wifi;
        SystemBluetoothState = BT;
        SystemRabbitMQLink = RMQ ;
    }

    public SysExtLinkStates() {
        SystemInternetstate = false;
        SystemWiFiState = false;
        SystemBluetoothState = false;
        SystemRabbitMQLink = false;
    }
}
