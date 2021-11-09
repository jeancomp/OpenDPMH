package com.jp.myapplication;

public class Resource {
    public Status status;
    public final Object data;
    public final String message;

    public Resource(Status status, Object data, String message) {
        super();
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public Status getStatus() {
        return this.status;
    }

    public Object getData() {
        return this.data;
    }

    public final String getMessage() {
        return this.message;
    }

    public final Status component1() {
        return this.status;
    }

    public final Object component2() {
        return this.data;
    }

    public final String component3() {
        return this.message;
    }

    public final Resource copy(Status status, Object data, String message) {
        return new Resource(status, data, message);
    }

    public static Resource resourceCopy(Resource resource, Status status, Object obj, String message, int value, Object var5) {
        if ((value & 1) != 0) {
            status = resource.status;
        }

        if ((value & 2) != 0) {
            obj = resource.data;
        }

        if ((value & 4) != 0) {
            message = resource.message;
        }

        return resource.copy(status, obj, message);
    }

    public boolean equals(Object obj) {
        if (this != obj) {
            if (obj instanceof Resource) {
                return true;
            }
            return false;
        } else {
            return true;
        }
    }

    public final Resource successDb(Object data) {
        return new Resource(Status.SUCCESS_DB, data, (String)null);
    }

    public final Resource successNetwork(Object data) {
        return new Resource(Status.SUCCESS_NETWORK, data, (String)null);
    }

    public final Resource error(String msg, Object data) {
        return new Resource(Status.ERROR, data, msg);
    }

    public final Resource loading(Object data) {
        return new Resource(Status.LOADING, data, (String)null);
    }

    public String toString() {
        return "Resource(status=" + this.status + ", data=" + this.data + ", message=" + this.message + ")";
    }
}
