package br.ufma.lsdi.digitalphenotyping.dataprocessor.base;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.listeners.ISubscriberListener;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.cddl.pubsub.Subscriber;
import br.ufma.lsdi.cddl.pubsub.SubscriberFactory;

public class DPUtilities {
    List<String> nameSensors = new ArrayList();
    Subscriber[] subscribers;
    int numSensors = 0;

    public DPUtilities(List<String> listSensors){
        numSensors = listSensors.size();
        nameSensors = listSensors;
        Log.i("DPUtilities","#### numSensors: " + numSensors);
        subscribers = new Subscriber[numSensors];
    }

    public void configSubscribers(){
//        for(int i=0; i<numSensors; i++){
//            if(!this.nameSensors.contains(listSensors.get(i).toString())){
//                Log.i("DPUtilities","#### Sensor Add: " + listSensors.get(i).toString());
//                this.nameSensors.add(listSensors.get(i).toString());
//            }
//        }

        for(int i=0; i<numSensors; i++) {
            subscribers[i] = SubscriberFactory.createSubscriber();
            subscribers[i].addConnection(CDDL.getInstance().getConnection());
            subscribeMessage(i, this.nameSensors.get(i).toString());
        }
    }

    public void subscribeMessage(int position, String serviceName) {
        Log.i("DPUtilities","#### position: " + position +",serviceName: " + serviceName);
        subscribers[position].subscribeServiceByName(serviceName);
        testSubcriber();
        subscribers[position].setSubscriberListener(createSubcriber(position));
    }

    ISubscriberListener[] subscriberListener;
    public void testSubcriber(){
        subscriberListener = new ISubscriberListener[numSensors];
    }

    public ISubscriberListener createSubcriber(int position){
        return subscriberListener[position] = new ISubscriberListener() {
            @Override
            public void onMessageArrived(Message message) {
                Log.i("DPUtilities","#### MESSAGE-UTILITIES: " + message);

            }
        };
    }

//    public ISubscriberListener[] subscriberListener = new ISubscriberListener() {
//        @Override
//        public void onMessageArrived(Message message) {
//            Log.i("DPUtilities","#### MESSAGE-UTILITIES: " + message);
//        }
//    };
}