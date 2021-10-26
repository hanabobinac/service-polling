package se.company.services.database;

import se.company.services.utils.Logger;

public class DBCreator {

    private DBConnector connector;

    public static final String CREATE_TABLE_SERVICES = "CREATE TABLE IF NOT EXISTS services " +
            "(url TEXT UNIQUE, name TEXT,status TEXT, created TEXT, updated TEXT, PRIMARY KEY(url))";

    public DBCreator(DBConnector connector) {
        this.connector = connector;
    }

    public void createTables() {
        connector.query(CREATE_TABLE_SERVICES).onComplete(done -> {
            if (done.succeeded()) {
                Logger.debug("Table services created or already exists.");
            } else {
                done.cause().printStackTrace();
                Logger.error("Failed to create table services.");
            }
        });
    }
}
