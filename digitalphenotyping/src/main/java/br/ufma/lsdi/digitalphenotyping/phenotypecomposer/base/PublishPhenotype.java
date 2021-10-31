package br.ufma.lsdi.digitalphenotyping.phenotypecomposer.base;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.cddl.network.ConnectionImpl;
import br.ufma.lsdi.cddl.pubsub.Publisher;
import br.ufma.lsdi.cddl.pubsub.PublisherFactory;
import br.ufma.lsdi.digitalphenotyping.Topics;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.digitalphenotypeevent.DigitalPhenotypeEvent;

public class PublishPhenotype {
    private static final String TAG = PublishPhenotype.class.getName();
    private Publisher publisher = PublisherFactory.createPublisher();
    private static PublishPhenotype instance = null;
    private static ConnectionImpl connection;
    private static Context context;
    private static String topic = "";

    //public PublishPhenotype(){ }

    public PublishPhenotype(Context context, ConnectionImpl con){
        this.connection = con;
        this.context = context;
        publisher.addConnection(connection);
    }

    public static PublishPhenotype getInstance() {
        if (instance == null) {
            instance = new PublishPhenotype(context, connection);
        }
        return instance;
    }

    public void publishPhenotypeComposer(DigitalPhenotypeEvent dpe){
        DigitalPhenotype dp = new DigitalPhenotype();
        dp.setDpeList(dpe);
        String valor = stringFromObject(dp);

        Message message = new Message();
        message.setServiceName("opendpmh");
        message.setTopic(Topics.OPENDPMH_TOPIC.toString());
        message.setServiceValue(valor);
        publisher.publish(message);
    }

    public void publishPhenotypeComposer(DigitalPhenotype digitalPhenotypeList) {
        Log.i(TAG, "#### Data Publish to Server");
        String valor = stringFromObject(digitalPhenotypeList);

        Message msg = new Message();
        msg.setServiceName("opendpmh");
        msg.setTopic(Topics.OPENDPMH_TOPIC.toString());
        msg.setServiceValue(valor);
        publisher.publish(msg);
    }

    public String stringFromObject(DigitalPhenotype dp){
        Gson gson = new Gson();
        String jsonString = gson.toJson(dp);
        return jsonString;
    }
}
