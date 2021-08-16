package br.ufma.lsdi.digitalphenotyping;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import java.util.List;
import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.pubsub.Publisher;
import br.ufma.lsdi.cddl.pubsub.PublisherFactory;

public class DPManager implements DPInterface {
    private static final String TAG = DPManager.class.getName();
    private String statusCon = "undefined";
    private static DPManager instance = null;
    Configurations configurations = Configurations.getInstance();
    private Context context;
    private Activity activity;
    private int communicationTechnology;
    private Boolean secure;
    private MainService myService;
    private boolean servicesStarted = false;
    Publisher publisher = PublisherFactory.createPublisher();


    /**
     * Construtor do DPManager
     */
    public DPManager(){
    }


    public static DPManager getInstance() {
        if (instance == null) {
            instance = new DPManager();
        }
        return instance;
    }

    @Override
    public void start(Activity activity, String host, int port, String username, String password, String topic, int compositionMode){
        try{
            Log.i(TAG, "#### INICIANDO FRAMEWORK");

            this.activity = activity;
            this.context = (Context) activity;
            configurations.getInstance().setActivity(activity);

            // configura endereço do servidor externo para o PhenotypeComposer
            configurations.getInstance().setExternalServer(host, port, username, password, topic);

            this.communicationTechnology = 4;   // Pré-configuramos o communicationTechnology inicia por 4
            this.secure = false;                // True==Certificado digitais, False==Não usa Cert. Digitais

            initPermissionsRequired();

            if(!servicesStarted) {
                Log.i(TAG, "#### Started framework MainService.");
                Intent intent = new Intent(this.context, MainService.class);
                //intent.putExtra("clientID",getClientID());
                intent.putExtra("communicationTechnology", this.communicationTechnology);
                intent.putExtra("secure", getSecure());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    getContext().startForegroundService(intent);
                } else {
                    getContext().startService(intent);
                }
                servicesStarted = true;
            }
        }catch (Exception e){
            Log.e(TAG, "#### Error: " + e.getMessage());
        }
    }


    @Override
    public void stop(){
        Log.i(TAG,"#### Stopped framework MainService.");
        // PARA O SERVICE PRIMEIRO PLANO
        myService.stopForeground(true);

        try {
            if(servicesStarted) {
                Intent intent = new Intent(getContext(), MainService.class);
                getContext().stopService(intent);

                servicesStarted = false;
            }
        }catch (Exception e){
            Log.e(TAG,e.toString());
        }
    }


    @Override
    public void startDataProcessors(List<String> nameProcessors){
        Log.i(TAG,"#### SSSSSSSSSSSSSSSSSSSSSservicesStarted: " + servicesStarted);
        if(!servicesStarted) {
            Log.i(TAG, "#### Started processors");
            if (!nameProcessors.isEmpty()) {
                for (int i = 0; i < nameProcessors.size(); i++) {
                    publishMessage(Topics.START_PROCESSOR_TOPIC.toString(), nameProcessors.get(i).toString());
                }
            }
        }
        else{
            Log.e(TAG,"#### Error: Started MainService Service");
        }
    }


    @Override
    public void stopDataProcessors(List<String> nameProcessors){
        if(servicesStarted) {
            Log.i(TAG, "#### Stopped processors");
            if (!nameProcessors.isEmpty()) {
                for (int i = 0; i < nameProcessors.size(); i++) {
                    publishMessage(Topics.STOP_PROCESSOR_TOPIC.toString(), nameProcessors.get(i).toString());
                }
            }
        }
        else{
            Log.e(TAG,"#### Error: Started MainService Service");
        }
    }


    @Override
    public List<String> getDataProcessorsList(){
        Log.i(TAG, "#### Processors list");
        List<String> processors = null;

        processors = myService.getProcessors();
        Log.i(TAG, "#### Processors list: " + processors);
        return processors;
    }


    @Override
    public List<String> getActiveDataProcessorsList(){
        List<String> processors = null;
        List<String> activeProcessors = null;

        return activeProcessors;
    }


    @Override
    public void setExternalServer(String hostServer, int port, String username, String password, String topic){

    }


    @Override
    public void setConfiguration(int number){

    }


    public Context getContext() {
        return context;
    }


    public void setContext(Context context) {
        this.context= context;
    }


    public void setActivity(Activity a){
        this.activity = a;
    }


    public Activity getActivity(){
        return activity;
    }


    public void setCommunicationTechnology(int communicationTechnology) {
        this.communicationTechnology = communicationTechnology;
    }


    public int getCommunicationTechnology() {
        return communicationTechnology;
    }


    public void setSecure(Boolean secure) {
        this.secure = secure;
    }

    public Boolean getSecure() {
        return this.secure;
    }


    public void setStatusCon(String statusCon){
        this.statusCon = statusCon;
    }


    public String getStatusCon(){
        return statusCon;
    }


    public void setBusSystem(MainService bus){
        this.myService = bus;
    }


    public MainService getBusSystem(){
        return myService;
    }


    public void publishMessage(String service, String text) {
        publisher.addConnection(CDDL.getInstance().getConnection());

        MyMessage message = new MyMessage();
        message.setServiceName(service);
        message.setServiceValue(text);
        publisher.publish(message);
    }


    private static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    public void initPermissionsRequired() {
        // Checa as permissões para rodar os sensores virtuais
        int PERMISSION_ALL = 1;

        String[] PERMISSIONS = {
                // Service location
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.FOREGROUND_SERVICE,
                Manifest.permission.RECORD_AUDIO

                // Outros services
        };


        if (!hasPermissions(getContext(), PERMISSIONS)) {
            Log.i(TAG, "##### Permission enabled for framework");
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
        }
    }
}
