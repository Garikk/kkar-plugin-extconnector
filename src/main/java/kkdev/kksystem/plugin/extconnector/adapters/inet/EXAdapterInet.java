/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kkdev.kksystem.plugin.extconnector.adapters.inet;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Set;
import kkdev.kksystem.base.classes.plugins.ControllerConfiguration;
import kkdev.kksystem.base.classes.plugins.PluginMessage;
import kkdev.kksystem.base.classes.plugins.webkkmaster.WM_Answer;
import static kkdev.kksystem.base.classes.plugins.webkkmaster.WM_KKMasterConsts.*;
import kkdev.kksystem.plugin.extconnector.adapters.IEXAdapter;
import kkdev.kksystem.plugin.extconnector.adapters.SysExtLinkStates;
import kkdev.kksystem.plugin.extconnector.configuration.EXAdapterConfig;
import kkdev.kksystem.plugin.extconnector.exconnmanager.IEXConnManager;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

/**
 *
 * @author sayma_000
 */
public class EXAdapterInet implements IEXAdapter {
    SysExtLinkStates LinkState;
    Deque<PluginMessage> PMStack;
    IEXConnManager ConnManager;
    int IntervalTune;

    EXAdapterConfig Configuration;
    final static String ___TEST_KKCAR_UUID_ = "2e2efd7b-ab83-42fa-9c00-2e45bb4b3ba1";

    public EXAdapterInet(EXAdapterConfig Conf, IEXConnManager Conn) {
        Configuration = Conf;
        ConnManager = Conn;
        PMStack = new ArrayDeque<>();
        LinkState=new SysExtLinkStates();
    }

    @Override
    public int GetIntervalTune() {
        return IntervalTune;
    }

    @Override
    public PluginMessage ExecutePinCommand(PluginMessage PP) {
        PMStack.add(PP);
        return null;
    }

    @Override
    public void SetActive() {
        //   throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void SetInactive() {
        //   throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void ReadPinCommands() {
        //==
        if (!LinkState.SystemInternetstate)
            return;
        //===
        WM_EXConn_PinData[] ForRet = GetPinData();
        //
        PluginMessage[] PlM=new PluginMessage[1];
        while (PMStack.size()>0)
        {
            PlM[0]=PMStack.poll();
            PostPinData(PlM);
        }
        //
        
        
        if (ForRet==null)
            return;
        
        for (WM_EXConn_PinData DT : ForRet) {
            for (PluginMessage PM : DT.PinData) {
                ConnManager.ExecPINCommand(PM);
            }
        }

    }

    private void SendPinData() {
        HttpClient client = HttpClientBuilder.create().build();
    }

    private WM_EXConn_PinData[] GetPinData() {

        WM_Answer Ans;
        Gson gson = new Gson();

        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost("http://" + Configuration.Inet_ServerHost + ":" + Configuration.Inet_ServerPort + "/" + Configuration.Inet_ExService);

            post.setEntity(new UrlEncodedFormEntity(GetPinRequest()));

            HttpResponse response = client.execute(post);
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            Ans = gson.fromJson(rd, WM_Answer.class);

            System.out.println("[EXA][DBG] " + Configuration.Inet_ServerHost + ":" + Configuration.Inet_ServerPort + "/" + Configuration.Inet_ExService);
            if (Ans.AnswerState == 0) {
                WM_EXConn_PinData[] Ret = gson.fromJson(Ans.JsonData, WM_EXConn_PinData[].class);
                if (Ret.length > 0) {
                    IntervalTune = 1;
                } else {
                    IntervalTune = 0;
                }

                System.out.println("[EXA][DBG] OK");
                return Ret;
            } else {
                System.out.println("[EXA][DBG] Null");
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int PostPinData(PluginMessage[] PM) {

        ControllerConfiguration Ret = null;
        WM_Answer Ans;
        Gson gson = new Gson();

        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(Configuration.Inet_ServerHost + ":" + Configuration.Inet_ServerPort + "/" + Configuration.Inet_ExService);

            post.setEntity(new UrlEncodedFormEntity(GetPinPost(PluginMessagesToPinData(PM))));

            HttpResponse response = client.execute(post);
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            Ans = gson.fromJson(rd, WM_Answer.class
            );

            if (Ans.AnswerState == 0) {
                return 0;
            } else {
                return 1;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return 1;
    }

    private static List<NameValuePair> GetPinRequest() {
        List<NameValuePair> nameValuePairs = new ArrayList<>(1);
        nameValuePairs.add(new BasicNameValuePair(WEBMASTER_REQUEST_ACT,
                WEBMASTER_REQUEST_EXTCONN_GETPIN));
        nameValuePairs.add(new BasicNameValuePair(WEBMASTER_REQUEST_MYUUID,
                ___TEST_KKCAR_UUID_));

        return nameValuePairs;
    }

    private static List<NameValuePair> GetPinPost(WM_EXConn_PinData ReqFiles) {
        List<NameValuePair> nameValuePairs = new ArrayList<>(3);
        nameValuePairs.add(new BasicNameValuePair(WEBMASTER_REQUEST_ACT,
                WEBMASTER_REQUEST_EXTCONN_PUTPIN));
        nameValuePairs.add(new BasicNameValuePair(WEBMASTER_REQUEST_MYUUID,
                ___TEST_KKCAR_UUID_));
        nameValuePairs.add(new BasicNameValuePair(WEBMASTER_REQUEST_EXTCONN_PINDATA,
                ReqFiles.GetJson()));

        return nameValuePairs;
    }

    private WM_EXConn_PinData PluginMessagesToPinData(PluginMessage[] PM) {
        WM_EXConn_PinData Ret = new WM_EXConn_PinData();
        Ret.PinData = PM;
        return Ret;
    }

    @Override
    public void ExtSysLinkStateChange(SysExtLinkStates State) {
       LinkState=State;
    }

}
