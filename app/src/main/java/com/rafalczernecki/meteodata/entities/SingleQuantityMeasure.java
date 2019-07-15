package com.rafalczernecki.meteodata.entities;

public class SingleQuantityMeasure {
    public static final int TEMPERATURE = 1;
    public static final int AIR_PRESSURE = 2;
    public static final int HUMIDITY = 3;

    private Long measureTimeInMillis;
    private Double value;

    public Long getMeasureTimeInMillis() {
        return measureTimeInMillis;
    }

    public void setMeasureTimeInMillis(Long measureTimeInMillis) {
        this.measureTimeInMillis = measureTimeInMillis;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
