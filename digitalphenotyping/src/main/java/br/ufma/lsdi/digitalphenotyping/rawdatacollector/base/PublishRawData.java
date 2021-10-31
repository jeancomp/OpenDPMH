package br.ufma.lsdi.digitalphenotyping.rawdatacollector.base;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.cddl.network.ConnectionImpl;
import br.ufma.lsdi.cddl.pubsub.Publisher;
import br.ufma.lsdi.cddl.pubsub.PublisherFactory;

public class PublishRawData {
    private static final String TAG = PublishRawData.class.getName();
    private Publisher publisher = PublisherFactory.createPublisher();
    private static PublishRawData instance;
    private static ConnectionImpl connection;
    private static Context context;
    private static String topic = "";

    public PublishRawData(Context cont, ConnectionImpl con, String topic){
        this.connection = con;
        this.context = cont;
        this.topic = topic;
        publisher.addConnection(connection);
    }

    public static PublishRawData getInstance() {
        if (instance == null) {
            instance = new PublishRawData(context, connection, topic);
        }
        return instance;
    }

    public void publishRawDataComposer(Message message) {
        Log.i(TAG, "#### Data Publish to Server");

        message.setServiceName(topic);
        message.setTopic(topic);
        publisher.publish(message);
        Log.i(TAG,"#### 3333: " + message);
    }

    /*public void publishRawDataComposer(Message message) {
        Log.i(TAG, "#### Data Publish to Server");
        String valor = stringFromObject(message);

        Message msg = new Message();
        msg.setServiceName(topic);
        msg.setTopic(topic);
        msg.setServiceValue(valor);
        publisher.publish(msg);
    }*/

    public String stringFromObject(Message message){
        Gson gson = new Gson();
        String jsonString = gson.toJson(message);
        return jsonString;
    }
}
