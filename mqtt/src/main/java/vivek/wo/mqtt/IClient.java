package vivek.wo.mqtt;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * Created by VIVEK-WO on 2018/3/14.
 */

public interface IClient extends IInterface {

    boolean isConnected(String clientHandler) throws RemoteException;

    void connect(String clientHandler, String serverURI, String clientId,
                 ConnectOptions options) throws RemoteException;

    void addIClientListener(String clientHandler, IClientListener iClientListener) throws
            RemoteException;

    void removeIClientListener(String clientHandler, IClientListener iClientListener)
            throws RemoteException;

    void publish(String clientHandler, String topic, byte[] payload, int qos,
                 boolean retained) throws RemoteException;

    void subscribe(String clientHandler, String topicFilter, int qos) throws
            RemoteException;

    void subscribe(String clientHandler, String[] topicFilters, int[] qos) throws
            RemoteException;

    void disconnect(String clientHandler) throws RemoteException;

    void publish(String clientHandler, String topic, String message, int qos,
                 boolean retained, int messageId) throws RemoteException;

    public static abstract class Stub extends Binder implements IClient {
        public static final String DESCRIPTOR = "vivek.wo.mqtt.IClient";

        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        public static IClient asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iInterface = obj.queryLocalInterface(DESCRIPTOR);
            if (iInterface != null && iInterface instanceof IClient) {
                return (IClient) iInterface;
            }
            return new Stub.Proxy(obj);

        }

        @Override
        public IBinder asBinder() {
            return this;
        }

        static final int TRANSACTION_publish = IBinder.FIRST_CALL_TRANSACTION + 6;
        static final int TRANSACTION_publish2 = IBinder.FIRST_CALL_TRANSACTION + 7;

        static final int TRANSACTION_addIClientListener = IBinder.FIRST_CALL_TRANSACTION + 0;
        static final int TRANSACTION_removeIClientListener = IBinder.FIRST_CALL_TRANSACTION + 1;
        static final int TRANSACTION_connect = IBinder.FIRST_CALL_TRANSACTION + 2;
        static final int TRANSACTION_subscribe = IBinder.FIRST_CALL_TRANSACTION + 3;
        static final int TRANSACTION_subscribe2 = IBinder.FIRST_CALL_TRANSACTION + 4;
        static final int TRANSACTION_disconnect = IBinder.FIRST_CALL_TRANSACTION + 5;

        static final int TRANSACTION_isConnected = IBinder.FIRST_CALL_TRANSACTION + 8;

        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws
                RemoteException {
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                case TRANSACTION_addIClientListener: {
                    data.enforceInterface(DESCRIPTOR);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    IClientListener _arg1;
                    _arg1 = IClientListener.Stub.asInterface(data.readStrongBinder());
                    this.addIClientListener(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_removeIClientListener: {
                    data.enforceInterface(DESCRIPTOR);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    IClientListener _arg1;
                    _arg1 = IClientListener.Stub.asInterface(data.readStrongBinder());
                    this.removeIClientListener(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_isConnected: {
                    data.enforceInterface(DESCRIPTOR);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    boolean _result = this.isConnected(_arg0);
                    reply.writeNoException();
                    reply.writeInt(_result ? 1 : 0);
                    return true;
                }
                case TRANSACTION_connect: {
                    data.enforceInterface(DESCRIPTOR);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    java.lang.String _arg1;
                    _arg1 = data.readString();
                    java.lang.String _arg2;
                    _arg2 = data.readString();
                    vivek.wo.mqtt.ConnectOptions _arg3;
                    if ((0 != data.readInt())) {
                        _arg3 = vivek.wo.mqtt.ConnectOptions.CREATOR.createFromParcel(data);
                    } else {
                        _arg3 = null;
                    }
                    this.connect(_arg0, _arg1, _arg2, _arg3);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_subscribe: {
                    data.enforceInterface(DESCRIPTOR);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    java.lang.String _arg1;
                    _arg1 = data.readString();
                    int _arg2;
                    _arg2 = data.readInt();
                    this.subscribe(_arg0, _arg1, _arg2);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_subscribe2: {
                    data.enforceInterface(DESCRIPTOR);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    java.lang.String[] _arg1;
                    _arg1 = data.createStringArray();
                    int[] _arg2;
                    _arg2 = data.createIntArray();
                    this.subscribe(_arg0, _arg1, _arg2);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_disconnect: {
                    data.enforceInterface(DESCRIPTOR);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    this.disconnect(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_publish: {
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0;
                    _arg0 = data.readString();
                    String _arg1;
                    _arg1 = data.readString();
                    byte[] _arg2;
                    _arg2 = data.createByteArray();
                    int _arg3;
                    _arg3 = data.readInt();
                    boolean _arg4;
                    _arg4 = data.readInt() == 1;
                    this.publish(_arg0, _arg1, _arg2, _arg3, _arg4);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_publish2: {
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0;
                    _arg0 = data.readString();
                    String _arg1;
                    _arg1 = data.readString();
                    String _arg2;
                    _arg2 = data.readString();
                    int _arg3;
                    _arg3 = data.readInt();
                    boolean _arg4;
                    _arg4 = data.readInt() == 1;
                    int _arg5;
                    _arg5 = data.readInt();
                    this.publish(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
                    reply.writeNoException();
                    return true;
                }
            }
            return super.onTransact(code, data, reply, flags);
        }

        public static class Proxy implements IClient {
            private IBinder mRemote;

            public Proxy(IBinder remote) {
                mRemote = remote;
            }

            public String getDescribtor() {
                return DESCRIPTOR;
            }

            @Override
            public void addIClientListener(String clientHandler, IClientListener iClientListener)
                    throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(clientHandler);
                    _data.writeStrongBinder((iClientListener != null ? iClientListener.asBinder()
                            : null));
                    mRemote.transact(TRANSACTION_addIClientListener, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void removeIClientListener(String clientHandler, IClientListener
                    iClientListener) throws
                    RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(clientHandler);
                    _data.writeStrongBinder((iClientListener != null ? iClientListener.asBinder()
                            : null));
                    mRemote.transact(TRANSACTION_removeIClientListener, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public boolean isConnected(String clientHandler) throws RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                boolean _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(clientHandler);
                    this.mRemote.transact(TRANSACTION_isConnected, _data, _reply, 0);
                    _reply.readException();
                    _result = 0 != _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public void connect(String clientHandler, String serverURI, String clientId,
                                ConnectOptions options) throws RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(clientHandler);
                    _data.writeString(serverURI);
                    _data.writeString(clientId);
                    if ((options != null)) {
                        _data.writeInt(1);
                        options.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    mRemote.transact(TRANSACTION_connect, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void subscribe(String clientHandler, String topicFilter, int qos) throws
                    RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(clientHandler);
                    _data.writeString(topicFilter);
                    _data.writeInt(qos);
                    mRemote.transact(TRANSACTION_subscribe, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void subscribe(String clientHandler, String[] topicFilters, int[] qos) throws
                    RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(clientHandler);
                    _data.writeStringArray(topicFilters);
                    _data.writeIntArray(qos);
                    mRemote.transact(TRANSACTION_subscribe2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void disconnect(String clientHandler) throws RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(clientHandler);
                    mRemote.transact(TRANSACTION_disconnect, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void publish(String clientHandler, String topic, byte[] payload, int qos,
                                boolean retained) throws RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(clientHandler);
                    _data.writeString(topic);
                    _data.writeByteArray(payload);
                    _data.writeInt(qos);
                    _data.writeInt(retained ? 1 : 0);
                    mRemote.transact(TRANSACTION_publish, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void publish(String clientHandler, String topic, String message, int qos,
                                boolean retained, int messageId) throws RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(clientHandler);
                    _data.writeString(topic);
                    _data.writeString(message);
                    _data.writeInt(qos);
                    _data.writeInt(retained ? 1 : 0);
                    _data.writeInt(messageId);
                    mRemote.transact(TRANSACTION_publish2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public IBinder asBinder() {
                return mRemote;
            }
        }


    }

}
