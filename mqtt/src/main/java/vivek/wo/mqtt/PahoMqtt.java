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
    private Context mContext;
    private IClient iClient;
    private boolean mIsReconnect = false;
    private OnServiceConnectionListener mOnServiceConnectionListener;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected ");
            iClient = IClient.Stub.asInterface(service);
            try {
                iClient.asBinder().linkToDeath(mBinderDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if (mOnServiceConnectionListener != null) {
                mOnServiceConnectionListener.onServiceConnected(PahoMqtt.this, mIsReconnect);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected ");
        }
    };
    private IBinder.DeathRecipient mBinderDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.d(TAG, "onServiceDisconnected Death ");
            iClient.asBinder().unlinkToDeath(mBinderDeathRecipient, 0);
            iClient = null;
            if (mOnServiceConnectionListener != null) {
                mOnServiceConnectionListener.onServiceDisconnected();
            }
            mIsReconnect = true;
            setup(mContext);
        }
    };

    public PahoMqtt(OnServiceConnectionListener onServiceConnectionListener) {
        mOnServiceConnectionListener = onServiceConnectionListener;
    }

    public PahoMqtt() {
    }

    public void setup(Context context) {
        mContext = context;
        Intent intent = new Intent(context, MqttService.class);
        context.startService(intent);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private IClient getIClient() throws RemoteException {
        if (iClient == null) {
            throw new RemoteException("Binder Death!");
        }
        return iClient;
    }

    public static interface OnServiceConnectionListener {
        void onServiceConnected(PahoMqtt pahoMqtt, boolean isReconnect);

        void onServiceDisconnected();
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

    public void publish(String clientHandler, String topic, byte[] payload, int qos,
                        boolean retained) throws RemoteException {
        getIClient().publish(clientHandler, topic, payload, qos, retained);
    }

    public void publish(String clientHandler, String topic, String message, int qos,
                        boolean retained, int messageId) throws RemoteException {
        getIClient().publish(clientHandler, topic, message, qos, retained, messageId);
    }

}
