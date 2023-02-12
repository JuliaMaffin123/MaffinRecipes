package com.maffin.recipes.network;

/**
 * Класс, описывающий модель данных одного компонента рецепта.
 */
public class Component {
    private int id;
    private String name;
    private String count;
    private String description;
    private int receipt_id;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCount() {
        return count;
    }

    public String getDescription() {
        return description;
    }

    public int getReceipt_id() {
        return receipt_id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setReceipt_id(int receipt_id) {
        this.receipt_id = receipt_id;
    }
}
