package br.ufma.lsdi.digitalphenotyping.phenotypecomposer.base;

import br.ufma.lsdi.cddl.network.ConnectionImpl;

public class SaveConnection {
    private static SaveConnection instance = null;
    public static ConnectionImpl connection;

    public SaveConnection(){ }

    public static SaveConnection getInstance() {
        if (instance == null) {
            instance = new SaveConnection();
        }
        return instance;
    }

    public ConnectionImpl getConnection(){
        return this.connection;
    }

    public void setActivity(ConnectionImpl con){
        this.connection = con;
    }
}