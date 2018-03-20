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
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by VIVEK-WO on 2018/3/12.
 */

public class MqttService extends Service implements MqttCallbackExtended {
    private static final String TAG = MqttService.class.getSimpleName();
    private static final int DISCONNECT_QUIESCE_TIMEOUT = 0;

    private static final int ALARM_DELAYINMILLISECONDS = 5 * 1000;
    private static final int ALARM_MAX_DELAYINMILLISECONDS = 2 * 60 * 1000;

    private RemoteCallbackList<IClientListener> mIClientListenerList = new RemoteCallbackList<>();

    private NetworkConnectionIntentReceiver mNetworkConnectionIntentReceiver;
    private AlarmManager mAlarmManager;
    private AlarmReceiver mAlarmReceiver;
    private PendingIntent mPendingIntent;
    private ConnectivityManager mConnectivityManager;

    private String mServerURI;
    private String mClientId;
    private MqttAsyncClient mMqttClient;
    private MqttConnectOptions mOpts;
    private String[] mSubscribtionTopics;
    private Handler mHandler;

    private long mDelayInMilliseconds = ALARM_DELAYINMILLISECONDS;
    private boolean mIsExcuteInited = false;

    private IClient.Stub iClient = new IClient.Stub() {

        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws
                RemoteException {
            return super.onTransact(code, data, reply, flags);
        }

        @Override
        public void addIClientListener(IClientListener iClientListener) throws RemoteException {
            mIClientListenerList.register(iClientListener);
        }

        @Override
        public void removeIClientListener(IClientListener iClientListener) throws RemoteException {
            mIClientListenerList.unregister(iClientListener);
        }
    };

    class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: " + intent.getAction());
            if (mDelayInMilliseconds < ALARM_MAX_DELAYINMILLISECONDS) {
                mDelayInMilliseconds += ALARM_DELAYINMILLISECONDS;
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    boolean isConnected = isConnected();
                    Log.d(TAG, "run: AlarmReceiver " + isConnected);
                    if (!isConnected) {
                        connect();
                    }
                }
            });
        }
    }

    private class NetworkConnectionIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: IsExcuteInited " + mIsExcuteInited + "," + intent.getAction());
            if (!mIsExcuteInited || !isNetworkConnected()) {
                return;
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    boolean isConnected = isConnected();
                    Log.d(TAG, "run: NetworkConnectionIntentReceiver " + isConnected);
                    if (!isConnected) {
                        stopKeepAlive();
                        connect();
                    }
                }
            });
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
        if (intent.getAction().equals("vivek.wo.mqtt.MqttService.action.setup")) {
            mServerURI = intent.getStringExtra("serverURI");
            mClientId = intent.getStringExtra("clientId");
            ConnectOptions options = intent.getParcelableExtra("connectOptions");
            mSubscribtionTopics = intent.getStringArrayExtra("subscribtionTopics");
            setMqttConnectOptions(options);
            checkOnStartCommand();
        }
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

    private void setMqttConnectOptions(ConnectOptions options) {
        mOpts = new MqttConnectOptions();
        mOpts.setCleanSession(options.isCleanSession());
        mOpts.setAutomaticReconnect(options.isAutomaticReconnect());
        mOpts.setKeepAliveInterval(options.getKeepAliveInterval());
        mOpts.setConnectionTimeout(options.getConnectionTimeout());
        mOpts.setMaxInflight(options.getMaxInflight());
        if (options.getUserName() != null && !options.getUserName().trim().equals("")) {
            mOpts.setUserName(options.getUserName());
            mOpts.setPassword(options.getPassword().toCharArray());
        }
    }

    private void checkOnStartCommand() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                stopKeepAlive();
                disconnect();
                connect();
            }
        });
    }

    boolean isConnected() {
        return mMqttClient == null ? false : mMqttClient.isConnected();
    }

    void disconnect() {
        if (mMqttClient != null) {
            try {
                mMqttClient.disconnectForcibly(DISCONNECT_QUIESCE_TIMEOUT);
                mMqttClient.close();
            } catch (MqttException e) {
                e.printStackTrace();
            }
            mMqttClient = null;
        }
    }

    void connect() {
        if (isNetworkConnected()) {
            try {
                if (mMqttClient == null) {
                    mMqttClient = new MqttAsyncClient(mServerURI, mClientId, null);
                }
                mMqttClient.setCallback(this);
                if (mOpts != null) {
                    IMqttToken token = mMqttClient.connect(mOpts);
                    token.waitForCompletion();
                }
            } catch (MqttException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
        }
        if (!isConnected()) {
            startKeepAlive(mDelayInMilliseconds);
        }
        mIsExcuteInited = true;
    }

    void subscribe() throws MqttException {
        int[] qos = new int[mSubscribtionTopics.length];
        for (int i = 0; i < mSubscribtionTopics.length; i++) {
            qos[i] = 2;
        }
        IMqttToken token = mMqttClient.subscribe(mSubscribtionTopics, qos, null,
                new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.d(TAG, "onSuccess: subscribe");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.d(TAG, "onFailure: subscribe");
                    }
                });
        token.waitForCompletion();
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        Log.d(TAG, "connectComplete: " + reconnect);
        if (mSubscribtionTopics != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        subscribe();
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.d(TAG, "connectionLost: " + cause.getMessage());
        if (!mOpts.isAutomaticReconnect()) {
            mDelayInMilliseconds = ALARM_DELAYINMILLISECONDS;
            startKeepAlive(mDelayInMilliseconds);
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.d(TAG, "messageArrived: " + topic + " , " + message.getId());
        if (mIClientListenerList != null) {
            int count = mIClientListenerList.beginBroadcast();
            for (int i = 0; i < count; i++) {
                try {
                    mIClientListenerList.getBroadcastItem(i)
                            .messageArrived(topic, message.getId(), message.getQos(), message
                                    .toString());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mIClientListenerList.finishBroadcast();
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
