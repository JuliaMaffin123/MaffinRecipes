package com.maffin.recipes.network;

/**
 * Класс, описывающий модель данных одного шага изготовления блюда.
 */
public class Step {

    private int id;
    private String description;
    private int receipt_id;
    private int photo;

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public int getReceipt_id() {
        return receipt_id;
    }

    public int getPhoto() {
        return photo;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setReceipt_id(int receipt_id) {
        this.receipt_id = receipt_id;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }
}
