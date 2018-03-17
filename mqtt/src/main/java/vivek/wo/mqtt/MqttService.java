package vivek.wo.mqtt;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.Nullable;

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
    private static final int DISCONNECT_QUIESCE_TIMEOUT = 0;

    private static final int HANDLER_MESSAGE_CONNECT = 0X10;
    private static final int HANDLER_MESSAGE_SUBSCRIBE = 0X11;

    private NetworkConnectionIntentReceiver mNetworkConnectionIntentReceiver;

    private String mServerURI;
    private String mClientId;
    private MqttAsyncClient mMqttClient;
    private MqttConnectOptions mMqttConnectOptions;
    private String[] mSubscribtionTopics;
    private Handler mHandler;

    private class NetworkConnectionIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

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

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread handlerThread = new HandlerThread(getClass().getName() + "[TaskHandler]");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                handleEvent(msg);
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals("vivek.wo.mqtt.MqttService.action.setup")) {
            mServerURI = intent.getStringExtra("serverURI");
            mClientId = intent.getStringExtra("clientId");
            ConnectOptions options = intent.getParcelableExtra("connectOptions");
            setMqttConnectOptions(options);
            mSubscribtionTopics = intent.getStringArrayExtra("subscribtionTopics");
            checkOnStartCommand();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setMqttConnectOptions(ConnectOptions options) {
        mMqttConnectOptions = new MqttConnectOptions();
        mMqttConnectOptions.setCleanSession(options.isCleanSession());
        mMqttConnectOptions.setAutomaticReconnect(options.isAutomaticReconnect());
        mMqttConnectOptions.setKeepAliveInterval(options.getKeepAliveInterval());
        mMqttConnectOptions.setConnectionTimeout(options.getConnectionTimeout());
        mMqttConnectOptions.setMaxInflight(options.getMaxInflight());
        if (options.getUserName() != null && !options.getUserName().trim().equals("")) {
            mMqttConnectOptions.setUserName(options.getUserName());
            mMqttConnectOptions.setPassword(options.getPassword().toCharArray());
        }
    }

    private void checkOnStartCommand() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                removeMessage(HANDLER_MESSAGE_CONNECT);
                removeMessage(HANDLER_MESSAGE_SUBSCRIBE);
                disconnect();
                mHandler.sendEmptyMessage(HANDLER_MESSAGE_CONNECT);
            }
        });
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

    void connect() throws MqttException {
        if (mMqttClient == null) {
            mMqttClient = new MqttAsyncClient(mServerURI, mClientId, null);
        }
        mMqttClient.setCallback(this);
        IMqttToken token = mMqttClient.connect(mMqttConnectOptions);
        token.waitForCompletion();
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
                        System.out.println(getClass().getSimpleName() + " mqtt subscribe " +
                                "onSuccess");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        System.out.println(getClass().getSimpleName() + " mqtt subscribe " +
                                "onFailure");
                    }
                });
        token.waitForCompletion();
    }

    void handleEvent(Message msg) {
        switch (msg.what) {
            case HANDLER_MESSAGE_CONNECT:
                try {
                    connect();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                break;
            case HANDLER_MESSAGE_SUBSCRIBE:
                try {
                    subscribe();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    void removeMessage(int what) {
        if (mHandler.hasMessages(what)) {
            mHandler.removeMessages(what);
        }
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        System.out.println(getClass().getSimpleName() + " mqtt connectComplete " + reconnect);
        if (mSubscribtionTopics != null) {
            mHandler.sendEmptyMessage(HANDLER_MESSAGE_SUBSCRIBE);
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println(getClass().getSimpleName() + " mqtt connectionLost");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println(getClass().getSimpleName() + " mqtt messageArrived " + message);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
