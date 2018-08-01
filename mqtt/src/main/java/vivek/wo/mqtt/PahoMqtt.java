package vivek.wo.mqtt;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * Created by VIVEK-WO on 2018/3/12.
 */

public class PahoMqtt {
    private static final String TAG = "PahoMqtt";

    PahoMqtt() {
    }

    private IClient iClient;

    public void setup(Context context) {
        Intent intent = new Intent(context, MqttService.class);
        context.startService(intent);
        context.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "onServiceConnected");
                iClient = IClient.Stub.asInterface(service);
                try {
                    iClient.asBinder().linkToDeath(mBinderDeathRecipient, 0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "onServiceDisconnected");
            }
        }, Context.BIND_AUTO_CREATE);
    }

    private IBinder.DeathRecipient mBinderDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.d(TAG, "onServiceDisconnected Death ");
            iClient.asBinder().unlinkToDeath(mBinderDeathRecipient, 0);
            iClient = null;
            //重连操作
        }
    };

    IClient getIClient() throws RemoteException {
        if (iClient == null) {
            throw new RemoteException("Binder Death!");
        }
        return iClient;
    }

    public void addIClientListener(String clientHandler, OnClientListener onClientListener) throws
            RemoteException {
        getIClient().addIClientListener(clientHandler, onClientListener);
    }

    public void removeIClientListener(String clientHandler, OnClientListener onClientListener)
            throws RemoteException {
        getIClient().removeIClientListener(clientHandler, onClientListener);
    }

    public void connect(String clientHandler, String serverURI, String clientId, ConnectOptions
            options) throws RemoteException {
        getIClient().connect(clientHandler, serverURI, clientId, options);
    }

    public void subscribe(String clientHandler, String topicFilter, int qos) throws
            RemoteException {
        getIClient().subscribe(clientHandler, topicFilter, qos);
    }

    public void subscribe(String clientHandler, String[] topicFilters, int[] qos) throws
            RemoteException {
        getIClient().subscribe(clientHandler, topicFilters, qos);
    }

    public void disconnect(String clientHandler) throws RemoteException {
        getIClient().disconnect(clientHandler);
    }

    public void pusblish(String clientHandler, String topic, byte[] payload, int qos,
                         boolean retained) throws RemoteException {
        getIClient().pusblish(clientHandler, topic, payload, qos, retained);
    }

    public void pusblish(String clientHandler, String topic, String message, int qos,
                         boolean retained, int messageId) throws RemoteException {
        getIClient().pusblish(clientHandler, topic, message, qos, retained, messageId);
    }

}
