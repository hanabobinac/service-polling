package se.company.services.models;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

import java.util.Date;

public class ServiceRecord {

    private String url;
    private String name;
    private Date date;
    private Boolean status = false;

    public ServiceRecord(String url, String name) {
        this.url = url;
        this.name = name;
    }

    public ServiceRecord(JsonObject json) {
        this.url = json.getString("url");
        this.name = json.getString("name");
    }

    public ServiceRecord(JsonArray jsonArray) {
        this.url = jsonArray.getString(0);
        this.name = jsonArray.getString(1);
    }

    public String name() {
        return this.name;
    }

    public void name(String name) {
        this.name = name;
    }

    public String url() {
        return this.url;
    }

    public void url(String url) {
        this.url = url;
    }

    public Date date() {
        return this.date;
    }

    public void date(Date date) {
        this.date = date;
    }

    public Boolean status() {
        return this.status;
    }

    public void status(Boolean status) {
        this.status = status;
    }
}
