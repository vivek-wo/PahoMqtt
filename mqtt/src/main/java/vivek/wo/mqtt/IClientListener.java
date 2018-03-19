package vivek.wo.mqtt;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * Created by VIVEK-WO on 2018/3/19.
 */

public interface IClientListener extends IInterface {

    public static abstract class Stub extends Binder implements IClientListener {
        public static final String DESCRIPTOR = "vivek.wo.mqtt.IClientListener";

        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        public static IClientListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iInterface = obj.queryLocalInterface(DESCRIPTOR);
            if (iInterface != null && iInterface instanceof IClientListener) {
                return (IClientListener) iInterface;
            }
            return new Stub.Proxy(obj);
        }

        @Override
        public IBinder asBinder() {
            return this;
        }

        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws
                RemoteException {
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                case TRANSACTION_messageArrived: {
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0;
                    _arg0 = data.readString();
                    int _arg1;
                    _arg1 = data.readInt();
                    int _arg2;
                    _arg2 = data.readInt();
                    String _arg3;
                    _arg3 = data.readString();
                    this.messageArrived(_arg0, _arg1, _arg2, _arg3);
                    reply.writeNoException();
                    return true;
                }
            }
            return super.onTransact(code, data, reply, flags);
        }

        public static class Proxy implements IClientListener {
            private IBinder mRemote;

            public Proxy(IBinder remote) {
                mRemote = remote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override
            public void messageArrived(String topic, int messageId, int qos, String message)
                    throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(topic);
                    _data.writeInt(messageId);
                    _data.writeInt(qos);
                    _data.writeString(message);
                    mRemote.transact(TRANSACTION_messageArrived, _data, _reply, 0);
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

        static final int TRANSACTION_messageArrived = IBinder.FIRST_CALL_TRANSACTION + 0;
    }

    public void messageArrived(String topic, int messageId, int qos, String message) throws
            RemoteException;
}
