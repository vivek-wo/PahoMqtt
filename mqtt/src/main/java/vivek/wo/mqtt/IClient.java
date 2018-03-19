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

    public static abstract class Stub extends Binder implements IClient {
        public static final String DESRCIBTOR = "vivek.wo.mqtt.IClient";

        public Stub() {
            this.attachInterface(this, DESRCIBTOR);
        }

        public static IClient asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iInterface = obj.queryLocalInterface(DESRCIBTOR);
            if (iInterface != null && iInterface instanceof IClient) {
                return (IClient) iInterface;
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
                    reply.writeString(DESRCIBTOR);
                    return true;
                }
                case TRANSACTION_addIClientListener: {
                    data.enforceInterface(DESRCIBTOR);
                    IClientListener _arg0;
                    _arg0 = IClientListener.Stub.asInterface(data.readStrongBinder());
                    this.addIClientListener(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_removeIClientListener: {
                    data.enforceInterface(DESRCIBTOR);
                    IClientListener _arg0;
                    _arg0 = IClientListener.Stub.asInterface(data.readStrongBinder());
                    this.removeIClientListener(_arg0);
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
                return DESRCIBTOR;
            }

            @Override
            public void addIClientListener(IClientListener iClientListener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESRCIBTOR);
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
            public void removeIClientListener(IClientListener iClientListener) throws
                    RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESRCIBTOR);
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
            public IBinder asBinder() {
                return mRemote;
            }
        }

        static final int TRANSACTION_addIClientListener = IBinder.FIRST_CALL_TRANSACTION + 0;
        static final int TRANSACTION_removeIClientListener = IBinder.FIRST_CALL_TRANSACTION + 1;

    }

    public void addIClientListener(IClientListener iClientListener) throws RemoteException;

    public void removeIClientListener(IClientListener iClientListener) throws RemoteException;

}
