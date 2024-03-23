package com.example.prosrok;

public class DataModel {
    private String id;
    private String barcode;
    private String item_name;
    private String item_quantity;
    private String expiration_date;
    private String comment_text;


    public DataModel() {
        // Пустой конструктор требуется для Firebase
    }

    public DataModel(String id, String barcode, String item_name, String item_quantity, String expiration_date, String comment_text) {
        this.id = id;
        this.barcode = barcode;
        this.item_name = item_name;
        this.item_quantity = item_quantity;
        this.expiration_date = expiration_date;
        this.comment_text = comment_text;
    }

    public String getId() {
        return id;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getItem_name() {
        return item_name;
    }

    public String getItem_quantity() {
        return item_quantity;
    }

    public String getExpiration_date() {
        return expiration_date;
    }

    public String getComment_text() {
        return comment_text;
    }
}