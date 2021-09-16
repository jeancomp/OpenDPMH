package br.ufma.lsdi.digitalphenotyping.phenotypecomposer.base;

import android.content.Context;
import android.util.Log;

import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.cddl.network.ConnectionImpl;
import br.ufma.lsdi.cddl.pubsub.Publisher;
import br.ufma.lsdi.cddl.pubsub.PublisherFactory;
import br.ufma.lsdi.digitalphenotyping.Topics;

public class PublishPhenotype {
    private static final String TAG = PublishPhenotype.class.getName();
    private Publisher publisher = PublisherFactory.createPublisher();
    private static Context context;
    private static ConnectionImpl connection;
    private static PublishPhenotype instance = null;

    //Data SERVIDOR
    private String host = "broker.hivemq.com";
    private int port = 1883;
    private String clientID="febfcfbccaeabda";
    private String username;
    private String password;
    private String topic = "inference";

    //public PublishPhenotype(){ }

    public PublishPhenotype(ConnectionImpl con, Context cont){
        this.connection = con;
        this.context = cont;
        publisher.addConnection(connection);
    }

    public static PublishPhenotype getInstance() {
        if (instance == null) {
            instance = new PublishPhenotype(connection, context);
        }
        return instance;
    }

    public void publishPhenotypeComposer(Message message) {
        Log.i(TAG, "#### Data Publish to Server");
        Message msg = new Message();
        msg.setServiceName("inference");
        msg.setTopic(Topics.INFERENCE_TOPIC.toString());
        msg.setServiceValue(message.getServiceValue());
        Log.i(TAG, "#### Data: " + msg);

        publisher.publish(msg);
    }
}
