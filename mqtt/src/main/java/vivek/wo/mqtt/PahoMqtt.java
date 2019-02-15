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

    /**
     * @param onServiceConnectionListener 服务连接监听器
     */
    public PahoMqtt(OnServiceConnectionListener onServiceConnectionListener) {
        mOnServiceConnectionListener = onServiceConnectionListener;
    }

    public PahoMqtt() {
    }

    /**
     * 启动服务
     *
     * @param context 上下文
     */
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

    /**
     * 添加消息监听
     *
     * @param clientHandler    客户端唯一标识
     * @param onClientListener 消息监听器
     * @throws RemoteException
     */
    public void addIClientListener(String clientHandler, OnClientListener onClientListener) throws
            RemoteException {
        getIClient().addIClientListener(clientHandler, onClientListener);
    }

    /**
     * 移除消息监听
     *
     * @param clientHandler    客户端唯一标识
     * @param onClientListener 消息监听器
     * @throws RemoteException
     */
    public void removeIClientListener(String clientHandler, OnClientListener onClientListener)
            throws RemoteException {
        getIClient().removeIClientListener(clientHandler, onClientListener);
    }

    /**
     * 连接
     *
     * @param clientHandler 客户端唯一标识
     * @param serverURI     MQTT 服务地址
     * @param clientId      MQTT ClientID 唯一标识
     * @param options       MQTT 连接参数配置
     * @param onActionListener
     * @throws RemoteException
     */
    public void connect(String clientHandler, String serverURI, String clientId, ConnectOptions
            options, OnActionListener onActionListener) throws RemoteException {
        getIClient().connect(clientHandler, serverURI, clientId, options, onActionListener);
    }

    /**
     * 订阅
     *
     * @param clientHandler 客户端唯一标识
     * @param topicFilter   订阅主题
     * @param qos           主题消息QOS
     * @param onActionListener
     * @throws RemoteException
     */
    public void subscribe(String clientHandler, String topicFilter, int qos,
                          OnActionListener onActionListener) throws RemoteException {
        getIClient().subscribe(clientHandler, topicFilter, qos, onActionListener);
    }

    /**
     * 订阅
     *
     * @param clientHandler 客户端唯一标识
     * @param topicFilters  订阅主题
     * @param qos           主题消息QOS
     * @param onActionListener
     * @throws RemoteException
     */
    public void subscribe(String clientHandler, String[] topicFilters, int[] qos,
                          OnActionListener onActionListener) throws RemoteException {
        getIClient().subscribe(clientHandler, topicFilters, qos, onActionListener);
    }

    /**
     * 断开连接
     *
     * @param clientHandler 客户端唯一标识
     * @throws RemoteException
     */
    public void disconnect(String clientHandler) throws RemoteException {
        getIClient().disconnect(clientHandler);
    }

    /**
     * 发布
     *
     * @param clientHandler 客户端唯一标识
     * @param topic
     * @param payload
     * @param qos
     * @param retained
     * @param onActionListener
     * @throws RemoteException
     */
    public void publish(String clientHandler, String topic, byte[] payload, int qos,
                        boolean retained, OnActionListener onActionListener) throws RemoteException {
        getIClient().publish(clientHandler, topic, payload, qos, retained, onActionListener);
    }

    /**
     * 发布
     *
     * @param clientHandler 客户端唯一标识
     * @param topic
     * @param message
     * @param qos
     * @param retained
     * @param messageId
     * @param onActionListener
     * @throws RemoteException
     */
    public void publish(String clientHandler, String topic, String message, int qos,
                        boolean retained, int messageId, OnActionListener onActionListener) throws RemoteException {
        getIClient().publish(clientHandler, topic, message, qos, retained, messageId, onActionListener);
    }

}
