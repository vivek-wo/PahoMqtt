package vivek.wo.mqtt;

public class PahoMqtt {

    public PahoMqtt get() {
        return Holder.PAHO_MQTT;
    }

    public static class Holder {
        private static final PahoMqtt PAHO_MQTT = new PahoMqtt();
    }


}
