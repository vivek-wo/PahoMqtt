package vivek.wo.mqtt.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import vivek.wo.mqtt.PahoMqtt

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        var pahoMqtt = PahoMqtt()
//        pahoMqtt.setup(applicationContext)
        PahoMqtt(object : PahoMqtt.OnServiceConnectionListener {
            override fun onServiceDisconnected() {
            }

            override fun onServiceConnected(pahoMqtt: PahoMqtt?, isReconnect: Boolean) {
            }
        }).setup(applicationContext)
    }
}
