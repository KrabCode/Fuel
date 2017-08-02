package krabcode.fuel;

import android.support.annotation.NonNull;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Class for holding one complete validated refuel entry.
 * Created by Jakub on 1. 8. 2017.
 */

public class Fuelstamp implements Comparable<Fuelstamp>{

    public float litres;
    public float cost;
    public float km;
    public boolean full;
    public Date date;
    public String id;

    public Fuelstamp(){}

    public String toString()
    {
        //I'd love to use string constants but I have no reference to them
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        DecimalFormat twoDigitPrecision = new DecimalFormat("0.00");
        DecimalFormat roundToNearestReal = new DecimalFormat("0.#");
        float costPerLitre = cost/litres;
        String result;
        if(full)
        {
            result = "●";
        }else{
            result = "○";
        }
        result += "\t\t" + sdf.format(date) + "\t\t\t\t" + roundToNearestReal.format(litres) + "l\t\t\t\t" + twoDigitPrecision.format(costPerLitre)+"Kč/l\t\t\t\t" + roundToNearestReal.format(km) +"km";
        return result;
    }

    @Override
    public int compareTo(@NonNull Fuelstamp o) {
        return o.date.compareTo(date);
    }
}
