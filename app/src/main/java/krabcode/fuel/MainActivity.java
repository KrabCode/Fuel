package krabcode.fuel;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //MAIN ACTIVITY
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //INPUT FORM
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static class FormFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public FormFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static MainActivity.FormFragment newInstance(int sectionNumber) {
            MainActivity.FormFragment fragment = new MainActivity.FormFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_form, container, false);

            //hook up the autocomplete
            EditText editLitres = (EditText) rootView.findViewById(R.id.editText_litres);
            editLitres.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    autocompleteEmptyField(rootView);
                }
            });
            EditText editTotalCost = (EditText) rootView.findViewById(R.id.editText_cost);
            editTotalCost.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    autocompleteEmptyField(rootView);
                }
            });
            EditText editCostPerLitre = (EditText) rootView.findViewById(R.id.editText_costPerLitre);
            editCostPerLitre.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    autocompleteEmptyField(rootView);
                }
            });

            //hook buttons up to methods
            Button buttonSave = (Button) rootView.findViewById(R.id.button_save);
            buttonSave.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //error messages are printed in the validateForm method
                    if(validateForm(rootView))
                    {
                        submitForm(rootView);
                    }
                }
            });

            Button buttonClear = (Button) rootView.findViewById(R.id.button_clear);
            buttonClear.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(rootView.getContext())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(R.string.clear_alert_title)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    clearForm(rootView);
                                }

                            })
                            .setNegativeButton(R.string.no, null)
                            .show();
                }
            });

            //fill today's date in the date field
            setEditDateTextToToday(rootView);

            return rootView;
        }

        private void clearForm(View rootView){
            EditText editLitres = (EditText) rootView.findViewById(R.id.editText_litres);
            EditText editTotalCost = (EditText) rootView.findViewById(R.id.editText_cost);
            EditText editCostPerLitre = (EditText) rootView.findViewById(R.id.editText_costPerLitre);
            EditText editKm = (EditText) rootView.findViewById(R.id.editText_km);

            editLitres.setText("");
            editTotalCost.setText("");
            editCostPerLitre.setText("");
            editKm.setText("");
            setEditDateTextToToday(rootView);
        }

        private void setEditDateTextToToday(View rootView)
        {
            EditText editDate = (EditText) rootView.findViewById(R.id.editText_date);
            SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.default_date_format), Locale.ENGLISH);
            editDate.setText(sdf.format(Calendar.getInstance().getTime()));
        }

        private boolean validateForm(View rootView){
            EditText editLitres = (EditText) rootView.findViewById(R.id.editText_litres);
            EditText editTotalCost = (EditText) rootView.findViewById(R.id.editText_cost);
            EditText editKm = (EditText) rootView.findViewById(R.id.editText_km);
            EditText editDate = (EditText) rootView.findViewById(R.id.editText_date);
            try{
                float litres = Float.parseFloat(editLitres.getText().toString());
            }catch (Exception ex){
                Toast.makeText(rootView.getContext(), R.string.parseError_litres, Toast.LENGTH_SHORT).show();
                return false;
            }
            try{
                float totalCost = Float.parseFloat(editTotalCost.getText().toString());
            }catch (Exception ex){
                Toast.makeText(rootView.getContext(), R.string.parseError_total, Toast.LENGTH_SHORT).show();
                return false;
            }
            try{
                float km = Float.parseFloat(editKm.getText().toString());
            }catch (Exception ex){
                Toast.makeText(rootView.getContext(), R.string.parseError_km, Toast.LENGTH_SHORT).show();
                return false;
            }
            try{
                DateFormat df = new SimpleDateFormat(getString(R.string.default_date_format), Locale.ENGLISH);
                Date date =  df.parse(editDate.getText().toString());
            }catch (Exception ex){
                Toast.makeText(rootView.getContext(), R.string.parseError_date, Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }

        /**
         * call validateForm first before calling this
         * @param rootView
         */
        private void submitForm(View rootView){
            //parse the input
            EditText editLitres = (EditText) rootView.findViewById(R.id.editText_litres);
            EditText editTotalCost = (EditText) rootView.findViewById(R.id.editText_cost);
            EditText editKm = (EditText) rootView.findViewById(R.id.editText_km);
            EditText editDate = (EditText) rootView.findViewById(R.id.editText_date);
            CheckBox checkFull = (CheckBox) rootView.findViewById(R.id.checkBox_full);

            float litres = Float.parseFloat(editLitres.getText().toString());
            float totalCost = Float.parseFloat(editTotalCost.getText().toString());
            float km = Float.parseFloat(editKm.getText().toString());
            boolean full = checkFull.isChecked();
            DateFormat df = new SimpleDateFormat(getString(R.string.default_date_format), Locale.ENGLISH);
            try {
                Date date =  df.parse(editDate.getText().toString());
                //all the data in to the main list in a Fuelstamp object
                FuelstampController controller = FuelstampController.getInstance(getActivity().getApplication());
                boolean saveSuccess = controller.add(litres,totalCost, km, date, full);
                if(saveSuccess)
                {
                    Toast.makeText(rootView.getContext(), R.string.save_success_message, Toast.LENGTH_SHORT).show();
                    clearForm(rootView);
                    //notify the log that its data set changed
                    ((ArrayAdapter)((ListView) getActivity().findViewById(R.id.listView_fuelstamp_history)).getAdapter()).notifyDataSetChanged();
                }else{
                    Toast.makeText(rootView.getContext(), R.string.save_failure_message, Toast.LENGTH_SHORT).show();
                }
            } catch (ParseException e) {
                //if the date is validated this exception will never happen
                e.printStackTrace();
            }
        }

        private static void autocompleteEmptyField(View rootView){
            EditText viewLitres = (EditText) rootView.findViewById(R.id.editText_litres);
            EditText viewTotalCost = (EditText) rootView.findViewById(R.id.editText_cost);
            EditText viewCostPerLitre = (EditText) rootView.findViewById(R.id.editText_costPerLitre);

            String litres = viewLitres.getText().toString();
            String total = viewTotalCost.getText().toString();
            String costPerLitre = viewCostPerLitre.getText().toString();
            DecimalFormat df = new DecimalFormat("#.##");

            if(isOneOfTheseAnEmptyString(litres, total, costPerLitre))
            {
                if(litres.equals("") && viewLitres.isFocused()){
                    //complete missing litres value
                    try{
                        float f_total = Float.parseFloat(total);
                        float f_costPerLitre = Float.parseFloat(costPerLitre);
                        float result = f_total / f_costPerLitre;
                        viewLitres.setText(df.format(result));
                    }catch(Exception ex){
                        Toast.makeText(rootView.getContext(), R.string.parseError_1, Toast.LENGTH_SHORT).show();
                    }
                }else if(total.equals("") && viewTotalCost.isFocused()){
                    //complete missing total value
                    try{
                        float f_litres = Float.parseFloat(litres);
                        float f_costPerLitre = Float.parseFloat(costPerLitre);
                        float result = (f_litres * f_costPerLitre);
                        viewTotalCost.setText(df.format(result));
                    }catch(Exception ex){
                        Toast.makeText(rootView.getContext(), R.string.parseError_2, Toast.LENGTH_SHORT).show();
                    }
                }else if(costPerLitre.equals("") && viewCostPerLitre.isFocused()){
                    //complete missing costPerLitre value
                    try{
                        float f_litres = Float.parseFloat(litres);
                        float f_total = Float.parseFloat(total);
                        float result = f_total/f_litres;
                        viewCostPerLitre.setText(df.format(result));
                    }catch(Exception ex){
                        Toast.makeText(rootView.getContext(), R.string.parseError_3, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        public static boolean isOneOfTheseAnEmptyString(String value1, String value2, String value3){
            int emptyCount = 0;
            if(value1.equals(""))
            {
                emptyCount++;
            }
            if(value2.equals(""))
            {
                emptyCount++;
            }
            if(value3.equals(""))
            {
                emptyCount++;
            }
            if(emptyCount == 1)
            {
                return true;
            }
            return false;
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //BACKLOG
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static class LogFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public LogFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static LogFragment newInstance(int sectionNumber) {
            LogFragment fragment = new LogFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_log, container, false);

            ListView log = (ListView) rootView.findViewById(R.id.listView_fuelstamp_history);
            final ArrayList<Fuelstamp> fuelstamps = FuelstampController.getInstance(rootView.getContext()).getFuelstamps();
            if(fuelstamps!= null)
            {
                final ArrayAdapter<Fuelstamp> adapter = new ArrayAdapter<Fuelstamp>(rootView.getContext(), R.layout.simple_list_item, fuelstamps);
                log.setAdapter(adapter);
                log.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final Fuelstamp selectedFuelstamp = FuelstampController.getInstance(rootView.getContext()).getFuelstamps().get(position);
                        if(selectedFuelstamp != null)
                        {
                            new AlertDialog.Builder(rootView.getContext())
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setTitle(R.string.delete_entry_dialog_title)
                                    .setMessage(selectedFuelstamp.toString())
                                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            FuelstampController.getInstance(rootView.getContext()).remove(selectedFuelstamp.id);
                                            adapter.notifyDataSetChanged();
                                        }
                                    })
                                    .setNegativeButton(R.string.no, null)
                                    .show();
                        }
                    }
                });
            }

            return rootView;
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //ANALYTICS
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static class AnalyticsFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public AnalyticsFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static AnalyticsFragment newInstance(int sectionNumber) {
            AnalyticsFragment fragment = new AnalyticsFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_analytics, container, false);
            GraphView efficiency = (GraphView) rootView.findViewById(R.id.graph_efficiency);
            efficiency.setTitle(getString(R.string.graph_efficiency_title));
            efficiency.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
            efficiency.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space
            populateEfficiency(rootView);
            return rootView;
        }

        private void populateEfficiency(View rootView)
        {
            DataPoint[] efficiencyDataPoints = FuelstampController.getInstance(rootView.getContext()).getEfficiencyDataPoints();
            if(efficiencyDataPoints.length > 0)
            {
                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(efficiencyDataPoints);

                GraphView efficiency = (GraphView) rootView.findViewById(R.id.graph_efficiency);
                efficiency.removeAllSeries();
                efficiency.addSeries(series);
                // set manual x bounds to have nice steps
                efficiency.getViewport().setMinX( efficiencyDataPoints[0].getX());
                efficiency.getViewport().setMaxX( efficiencyDataPoints[efficiencyDataPoints.length-1].getX());
                efficiency.getViewport().setXAxisBoundsManual(true);
            }
        }
    }



    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //PAGING
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return FormFragment.newInstance(position + 1);
                case 1:
                    return LogFragment.newInstance(position + 1);
                case 2:
                    return AnalyticsFragment.newInstance(position + 1);
                default:
                    return FormFragment.newInstance(position + 1);
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.title_page_1);
                case 1:
                    return getString(R.string.title_page_2);
                case 2:
                    return getString(R.string.title_page_3);
            }
            return null;
        }
    }
}
