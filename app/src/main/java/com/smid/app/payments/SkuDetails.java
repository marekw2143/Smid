package com.smid.app.payments;

/**
 * Created by marek on 09.07.16.
 */
public class SkuDetails {
    private String skuId;
    private String price;
    private String price_currency_code;
    private String title;
    private String description;

    public SkuDetails(String skuId, String price, String price_currency_code, String title, String description) {
        this.skuId = skuId;
        this.price = price;
        this.price_currency_code = price_currency_code;
        this.title = title;
        this.description = description;
    }

    public String getSkuId() {
        return skuId;
    }

    public String getPrice() {
        return price;
    }

    public String getPrice_currency_code() {
        return price_currency_code;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
