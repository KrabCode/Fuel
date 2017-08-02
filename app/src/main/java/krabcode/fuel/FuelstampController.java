package krabcode.fuel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Random;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.jjoe64.graphview.series.DataPoint;

/**
 * Class for providing the interface for the gui to manipulate the data in the "fuelstamps" list of submitted form entries
 *
 * Created by Jakub on 1. 8. 2017.
 */

public class FuelstampController {

    //the only important object in this class
    //try not to look directly at it
    private ArrayList<Fuelstamp> fuelstamps;


    private Context context;

    private static FuelstampController mInstance= null;

    protected FuelstampController(){}

    public static synchronized FuelstampController getInstance(Context context){
        if(mInstance == null){
            mInstance = new FuelstampController();
            mInstance.context = context;
            mInstance.loadFuelstampsFromDisk();
        }
        return mInstance;
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //BASIC OPERATIONS
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     *
     * @param litres
     * @param cost
     * @param km
     * @param date
     * @return whether save succeeded
     */
    public boolean add(float litres, float cost, float km, Date date, boolean full)
    {
        Fuelstamp fuelstamp = new Fuelstamp();
        fuelstamp.litres = litres;
        fuelstamp.cost = cost;
        fuelstamp.km = km;
        fuelstamp.date = date;
        fuelstamp.full = full;
        fuelstamp.id = generateUniqueId();
        fuelstamps.add(fuelstamp);
        return save();
    }

    public void remove(String id)
    {
        Fuelstamp deadStamp = null;
        for(Fuelstamp liveStamp : fuelstamps)
        {
            if(liveStamp.id.equals(id))
            {
                deadStamp = liveStamp;
                break;
            }
        }
        if(deadStamp != null)
        {
            // Nick Cave & The Bad Seeds - The Mercy Seat
            // https://www.youtube.com/watch?v=Ahr4KFl79WI
            fuelstamps.remove(deadStamp);
            save();
        }
    }

    private String generateUniqueId() {
        String newId;
        Random rand = new Random();
        do{
            int n = rand.nextInt(Integer.MAX_VALUE);
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

    public ArrayList<Fuelstamp> getFuelstamps()
    {
        Collections.sort(fuelstamps);
        return fuelstamps;
    }

    private void loadFuelstampsFromDisk()
    {

        try{
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            String json = settings.getString(context.getString(R.string.SharedPreferences_savedFuelstampsKey), null);
            GsonBuilder gson = new GsonBuilder();
            Type listType = new TypeToken<ArrayList<Fuelstamp>>(){}.getType();
            fuelstamps = gson.create().fromJson(json, listType);
        }catch (Exception e)
        {
            e.printStackTrace();
            fuelstamps = new ArrayList<Fuelstamp>();
        }
        if(fuelstamps == null)
        {
            fuelstamps = new ArrayList<Fuelstamp>();
        }
    }

    /**
     * @return whether save succeeded
     */
    private boolean save()
    {
        boolean success = true;
        try{
            Collections.sort(fuelstamps);
            String json = new Gson().toJson(fuelstamps);
            SharedPreferences.Editor settings = PreferenceManager.getDefaultSharedPreferences(context).edit();
            settings.putString(context.getString(R.string.SharedPreferences_savedFuelstampsKey), json);
            settings.apply();
        }catch (Exception ex){
            //should never happen
            success = false;
            ex.printStackTrace();
        }
        return success;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //ANALYTICS
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public DataPoint[] getEfficiencyDataPoints() {
        //sort from  oldest to  newest
        Collections.sort(fuelstamps);
        Collections.reverse(fuelstamps);

        ArrayList<DataPoint> dataSet = new ArrayList<>();
        int runningLitresTotal = 0;
        Fuelstamp lastFull = null;
        for(Fuelstamp current : fuelstamps)
        {
            if(current.full == true){
                //can't calculate anything
                if(lastFull != null)
                {
                    float kmDifference = current.km - lastFull.km;
                    DataPoint dp = new DataPoint(lastFull.date, (kmDifference / 100) / runningLitresTotal);
                    dataSet.add(dp);
                }
                //reset
                lastFull = current;
                runningLitresTotal = 0;
            }
            runningLitresTotal += current.litres;
        }
        Collections.sort(fuelstamps);
        return dataSet.toArray(new DataPoint[]{});
    }
}
