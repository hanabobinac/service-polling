package se.company.services.models;

import java.util.Date;
import java.text.SimpleDateFormat;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;


public class ServiceRecord {

    private final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private String url;
    private String name;
    private Boolean status = false;
    private Date created;
    private Date updated;

    public ServiceRecord(JsonObject json) {
        this.url = json.getString("url");
        this.name = json.getString("name");
        this.status = json.getBoolean("status");
        this.created = stringToDate(json.getString("created"));
        this.updated = stringToDate(json.getString("updated"));
    }

    public ServiceRecord(JsonArray jsonArray) {
        this.url = jsonArray.getString(0);
        this.name = jsonArray.getString(1);
        var s = jsonArray.getString(2);
        if ("1".equals(s)) {
            this.status = true;
        } else {
            this.status = false;
        }
        this.created = stringToDate(jsonArray.getString(3));
        this.updated = stringToDate(jsonArray.getString(4));
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

    public Boolean status() {
        return this.status;
    }

    public void status(Boolean status) {
        this.status = status;
    }

    public Date created() {
        return this.created;
    }

    public void created(Date created) {
        this.created = created;
    }

    public Date updated() {
        return this.updated;
    }

    public void updated(Date updated) {
        this.updated = updated;
    }

    public String statusString() {
        if (status()) {
            return "OK";
        } else {
            return "FAIL";
        }
    }

    public String createdString() {
        return dateToString(this.created);
    }

    public String updatedString() {
        return dateToString(this.updated);
    }

    public JsonObject toJson() {
        var json = new JsonObject();
        json.put("url", url());
        json.put("name", name());
        json.put("status", statusString());
        json.put("created", createdString());
        json.put("updated", updatedString());
        return json;
    }

    public String dateToString(Date date) {
        if (date == null) {
            return null;
        }
        var dateFormat = new SimpleDateFormat(DATE_FORMAT);
        return dateFormat.format(date);
    }

    public Date stringToDate(String s) {
        try {
            Date date = new SimpleDateFormat(DATE_FORMAT).parse(s);
            return date;
        } catch (Exception ex) {
            return null;
        }
    }
}
