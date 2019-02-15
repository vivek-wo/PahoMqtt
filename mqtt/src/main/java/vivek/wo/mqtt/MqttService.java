package vivek.wo.mqtt;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by VIVEK-WO on 2018/3/12.
 */

public class MqttService extends Service {
    static final String TAG = "MqttService";

    private static final int ALARM_DELAYINMILLISECONDS = 5 * 1000;
    private static final int ALARM_MAX_DELAYINMILLISECONDS = 2 * 60 * 1000;

    private NetworkConnectionIntentReceiver mNetworkConnectionIntentReceiver;
    private AlarmManager mAlarmManager;
    private AlarmReceiver mAlarmReceiver;
    private PendingIntent mPendingIntent;
    private ConnectivityManager mConnectivityManager;

    private Map<String, Client> mClientMap = new HashMap<>();

    private Handler mHandler;
    private long mDelayInMilliseconds = ALARM_DELAYINMILLISECONDS;

    private IClient.Stub iClient = new IClient.Stub() {

        Client getClient(String clientHandler) throws RemoteException {
            Client client = mClientMap.get(clientHandler);
            if (client == null) {
                throw new RemoteException(clientHandler + " NO MQTT CLIENT!");
            }
            if (!client.isConnected()) {
                throw new RemoteException(clientHandler + " MQTT CLIENT NOT CONNECTED!");
            }
            return client;
        }

        Client getOrCreate(String clientHandler) {
            Client client = mClientMap.get(clientHandler);
            if (client == null) {
                client = new Client(clientHandler);
                mClientMap.put(clientHandler, client);
            }
            return client;
        }

        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws
                RemoteException {
            return super.onTransact(code, data, reply, flags);
        }

        @Override
        public void addIClientListener(String clientHandler, IClientListener iClientListener)
                throws RemoteException {
            getOrCreate(clientHandler).addIClientListener(iClientListener);
        }

        @Override
        public void removeIClientListener(String clientHandler, IClientListener iClientListener)
                throws RemoteException {
            getOrCreate(clientHandler).removeIClientListener(iClientListener);
        }

        @Override
        public boolean isConnected(String clientHandler) throws RemoteException {
            Client client = mClientMap.get(clientHandler);
            return client != null && client.isConnected();
        }

        @Override
        public void connect(String clientHandler, String serverURI, String clientId,
                            ConnectOptions options, IActionListener iActionListener) throws RemoteException {
            Client client = getOrCreate(clientHandler);
            if (serverURI.startsWith("ssl://")) {
                if (options.getAssetCrtName() == null || options.getAssetCrtName().length() == 0) {
                    client.setSSLSocketFactory(new ClientSSLSocketFactory().create());
                } else {
                    client.setSSLSocketFactory(new ClientSSLSocketFactory(getApplicationContext(),
                            options.getAssetCrtName()).create());
                }
            }
            client.resetClient(serverURI, clientId);
            client.connect(options, iActionListener);
        }

        @Override
        public void subscribe(String clientHandler, String topicFilter, int qos,
                              IActionListener iActionListener) throws RemoteException {
            getClient(clientHandler).subscribe(topicFilter, qos, iActionListener);
        }

        @Override
        public void subscribe(String clientHandler, String[] topicFilters, int[] qos,
                              IActionListener iActionListener) throws RemoteException {
            getClient(clientHandler).subscribe(topicFilters, iActionListener);
        }

        @Override
        public void disconnect(String clientHandler) throws RemoteException {
            getClient(clientHandler).disconnect();
        }

        @Override
        public void publish(String clientHandler, String topic, byte[] payload, int qos,
                            boolean retained, IActionListener iActionListener) throws RemoteException {
            getClient(clientHandler).publish(topic, payload, qos, retained, iActionListener);
        }

        @Override
        public void publish(String clientHandler, String topic, String message, int qos,
                            boolean retained, int messageId, IActionListener iActionListener) throws RemoteException {
            MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setPayload(message.getBytes());
            mqttMessage.setQos(qos);
            mqttMessage.setRetained(retained);
            mqttMessage.setId(messageId);
            getClient(clientHandler).publish(topic, mqttMessage, iActionListener);
        }
    };

    class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mDelayInMilliseconds < ALARM_MAX_DELAYINMILLISECONDS) {
                mDelayInMilliseconds += ALARM_DELAYINMILLISECONDS;
            }
            boolean isNetworkConnected = isNetworkConnected();
            Log.d(TAG, "Alarm onReceive: isNetworkConnected " + isNetworkConnected + ","
                    + intent.getAction());
            if (!isNetworkConnected) {
                return;
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    execClientReconnect("Alarm");
                }
            });
        }
    }

    private class NetworkConnectionIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isNetworkConnected = isNetworkConnected();
            Log.d(TAG, "NetworkConnection onReceive: isNetworkConnected " + isNetworkConnected +
                    "," + intent
                    .getAction());
            if (!isNetworkConnected) {
                return;
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    execClientReconnect("NetworkConnection");
                }
            });
        }
    }

    private void execClientReconnect(String tag) {
        for (Client client : mClientMap.values()) {
            boolean isAutomaticReconnect = client.isAutomaticReconnect();
            Log.d(TAG, tag + " run: Client Handler  " + client.getClientHandler() + " , " +
                    "isAutomaticReconnect " + isAutomaticReconnect);
            if (!isAutomaticReconnect) {
                boolean isConnected = client.isConnected();
                Log.d(TAG, tag + " run: isConnected " + isConnected);
                if (!isConnected) {
                    stopKeepAlive();
                    client.reconnect();
                }
            }
        }
    }

    private void registerReceiver() {
        if (mNetworkConnectionIntentReceiver == null) {
            mNetworkConnectionIntentReceiver = new NetworkConnectionIntentReceiver();
            registerReceiver(mNetworkConnectionIntentReceiver, new IntentFilter(ConnectivityManager
                    .CONNECTIVITY_ACTION));
        }
    }

    private void unregisterReceiver() {
        if (mNetworkConnectionIntentReceiver != null) {
            unregisterReceiver(mNetworkConnectionIntentReceiver);
            mNetworkConnectionIntentReceiver = null;
        }
    }

    private boolean isNetworkConnected() {
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        boolean isConnected = ((networkInfo != null)
                && (networkInfo.isAvailable())
                && (networkInfo.isConnected()));
        Log.d(TAG, "check isNetworkConnected: " + isConnected);
        return isConnected;
    }

    private void startKeepAlive(long delayInMilliseconds) {
        long nextAlarmInMilliseconds = System.currentTimeMillis()
                + delayInMilliseconds;
        Log.d(TAG, "schedule next alarm at " + nextAlarmInMilliseconds);
        if (Build.VERSION.SDK_INT >= 23) {
            // In SDK 23 and above, dosing will prevent setExact, setExactAndAllowWhileIdle will
            // force
            // the device to run this task whilst dosing.
            Log.d(TAG, "alarm scheule using setExactAndAllowWhileIdle, next: " +
                    delayInMilliseconds);
            mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    nextAlarmInMilliseconds,
                    mPendingIntent);
        } else if (Build.VERSION.SDK_INT >= 19) {
            Log.d(TAG, "alarm scheule using setExact, delay: " + delayInMilliseconds);
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, nextAlarmInMilliseconds,
                    mPendingIntent);
        } else {
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, nextAlarmInMilliseconds,
                    mPendingIntent);
        }
    }

    private void stopKeepAlive() {
        mAlarmManager.cancel(mPendingIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mConnectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        String action = getClass().getName() + ".reveicer.action.alarm";
        mPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(action), PendingIntent
                .FLAG_UPDATE_CURRENT);
        mAlarmReceiver = new AlarmReceiver();
        registerReceiver(mAlarmReceiver, new IntentFilter(action));
        HandlerThread handlerThread = new HandlerThread(getClass().getName() + "[TaskHandler]");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
        registerReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iClient;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mAlarmReceiver);
        unregisterReceiver();
    }
}
