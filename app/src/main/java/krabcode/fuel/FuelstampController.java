package krabcode.fuel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.*;

/**
 * Class for providing the interface for the gui to manipulate the data in the "fuelstamps" list of submitted form entries
 *
 * Created by Jakub on 1. 8. 2017.
 */

public class FuelstampController {

    //the only important object in this class
    private ArrayList<Fuelstamp> fuelstamps;

    private Context context;

    private static FuelstampController mInstance= null;

    protected FuelstampController(){}

    public static synchronized FuelstampController getInstance(Context context){
        if(mInstance == null){
            mInstance = new FuelstampController();
            mInstance.context = context;
            mInstance.loadFuelstampsFromSavedPreferences();
        }
        return mInstance;
    }

    /**
     *
     * @param litres
     * @param cost
     * @param km
     * @param date
     * @return whether save succeeded
     */
    public boolean add(float litres, float cost, float km, Date date)
    {
        Fuelstamp fuelstamp = new Fuelstamp();
        fuelstamp.litres = litres;
        fuelstamp.cost = cost;
        fuelstamp.km = km;
        fuelstamp.date = date;
        fuelstamp.id = generateUniqueId();
        fuelstamps.add(fuelstamp);
        return save();
    }

    private String generateUniqueId() {
        String newId;
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
        Fuelstamp deadStamp = null;
        for(Fuelstamp liveStamp : fuelstamps)
        {
            if(liveStamp.id.equals(id))
            {
                deadStamp = liveStamp;
            }
        }
        if(deadStamp != null)
        {
            // Nick Cave & The Bad Seeds - The Mercy Seat
            // https://www.youtube.com/watch?v=Ahr4KFl79WI
            fuelstamps.remove(deadStamp);
        }
    }

    private void loadFuelstampsFromSavedPreferences()
    {
        fuelstamps = new ArrayList<Fuelstamp>();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String json = (String)settings.getAll().get(R.string.SharedPreferences_savedFuelstampsKey);
        FuelstampJsonReference parsedFuelstampList = new Gson().fromJson(json, FuelstampJsonReference.class);
        if(parsedFuelstampList != null)
        {
            for(Fuelstamp stamp : parsedFuelstampList.fuelstampList)
            {
                fuelstamps.add(stamp);
            }
        }


    }

    /**
     * @return whether save succeeded
     */
    private boolean save()
    {
        boolean success = true;
        try{
        String json = new Gson().toJson(fuelstamps);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings.edit().putString(context.getString(R.string.SharedPreferences_savedFuelstampsKey), json);
        settings.edit().apply();
        }catch (Exception ex)
        {
            success = false;
            ex.printStackTrace();
        }
        return success;
    }

}
