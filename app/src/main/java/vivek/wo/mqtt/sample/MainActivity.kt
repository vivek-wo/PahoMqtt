package vivek.wo.mqtt.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import vivek.wo.mqtt.MqttAndroidClient

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var client = MqttAndroidClient.Builder("", "").build()
    }
}
