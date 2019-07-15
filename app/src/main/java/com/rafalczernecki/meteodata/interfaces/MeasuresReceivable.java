package com.rafalczernecki.meteodata.interfaces;

import com.rafalczernecki.meteodata.entities.SingleQuantityMeasure;

import java.util.ArrayList;

public interface MeasuresReceivable {
    void receiveMeasures(ArrayList<SingleQuantityMeasure> measures);
}
