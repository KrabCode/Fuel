package krabcode.fuel;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import android.preference.PreferenceManager;
import android.view.View;
import org.json.*;

/**
 * Created by Jakub on 1. 8. 2017.
 */

public class FuelstampController {

    private ArrayList<Fuelstamp> fuelstamps;



    private static FuelstampController mInstance= null;

    protected FuelstampController(){}

    public static synchronized FuelstampController getInstance(){
        if(null == mInstance){
            mInstance = new FuelstampController();
            mInstance.load();
        }
        return mInstance;
    }

    public void add(float litres, float cost, float km, Date date)
    {
        Fuelstamp fuelstamp = new Fuelstamp();
        fuelstamp.litres = litres;
        fuelstamp.cost = cost;
        fuelstamp.km = km;
        fuelstamp.date = date;
        fuelstamp.id = generateUniqueId();
        fuelstamps.add(fuelstamp);
        save();
    }

    private String generateUniqueId() {
        String newId = "";
        Random rand = new Random();
        do{
            int n = rand.nextInt(50) + 1;
            newId = "" + n;
        }while(getFuelstampById(newId) != null);
        return newId;
    }

    public Fuelstamp getFuelstampById(String id)
    {
        for(Fuelstamp fStamp : fuelstamps)
        {
            if(fStamp.id.equals(id))
            {
                return fStamp;
            }
        }
        return null;
    }

    public void remove(String id)
    {

    }

    public void load()
    {
        //PreferenceManager.getDefaultSharedPreferences(rootView.getContext());
    }

    public void save()
    {

    }
}
