package com.maffin.recipes.network;

/**
 * Класс, описывающий модель данных одного рцепта.
 */
public class Receipt {
    private int id;
    private int type_id;
    private String name;
    private String description;
    private String source;
    private int time;
    private int energy;

    public int getId() {
        return id;
    }

    public int getType_id() {
        return type_id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSource() {
        return source;
    }

    public int getTime() {
        return time;
    }

    public int getEnergy() {
        return energy;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setType_id(int type_id) {
        this.type_id = type_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }
}
