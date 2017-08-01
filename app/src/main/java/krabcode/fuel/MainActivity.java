package krabcode.fuel;

import android.app.AlertDialog;
import android.content.Context;
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

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    private FuelstampController fuelstampController;

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

            //fill today's date in the date field
            EditText editDate = (EditText) rootView.findViewById(R.id.editText_date);
            SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.default_date_format), Locale.ENGLISH);
            editDate.setText(sdf.format(Calendar.getInstance().getTime()));

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
                            .setMessage(R.string.really_clear)
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

            return rootView;
        }

        private void clearForm(View rootView){
            EditText editLitres = (EditText) rootView.findViewById(R.id.editText_litres);
            EditText editTotalCost = (EditText) rootView.findViewById(R.id.editText_cost);
            EditText editCostPerLitre = (EditText) rootView.findViewById(R.id.editText_costPerLitre);
            EditText editKm = (EditText) rootView.findViewById(R.id.editText_km);
            EditText editDate = (EditText) rootView.findViewById(R.id.editText_date);

            editLitres.setText("");
            editTotalCost.setText("");
            editCostPerLitre.setText("");
            editKm.setText("");
            editDate.setText("");
        }

        private void submitForm(View rootView){
            //parse the input
            EditText editLitres = (EditText) rootView.findViewById(R.id.editText_litres);
            EditText editTotalCost = (EditText) rootView.findViewById(R.id.editText_cost);
            EditText editKm = (EditText) rootView.findViewById(R.id.editText_km);
            EditText editDate = (EditText) rootView.findViewById(R.id.editText_date);

            float litres = Float.parseFloat(editLitres.getText().toString());
            float totalCost = Float.parseFloat(editTotalCost.getText().toString());
            float km = Float.parseFloat(editKm.getText().toString());
            DateFormat df = new SimpleDateFormat(getString(R.string.default_date_format), Locale.ENGLISH);
            try {
                Date date =  df.parse(editDate.getText().toString());
                //all the data in to the main list in a Fuelstamp object
                FuelstampController controller = FuelstampController.getInstance(getActivity().getApplication());
                boolean saveSuccess = controller.add(litres,totalCost, km, date);
                if(saveSuccess)
                {
                    Toast.makeText(rootView.getContext(), R.string.save_success_message, Toast.LENGTH_SHORT).show();
                    clearForm(rootView);
                }else{
                    Toast.makeText(rootView.getContext(), R.string.save_failure_message, Toast.LENGTH_SHORT).show();
                }
            } catch (ParseException e) {
                //if the date is validated this exception will never happen
                e.printStackTrace();
            }
        }

        private boolean validateForm(View rootView){
            boolean success = true;  //will try to disprove this assumption

            EditText editLitres = (EditText) rootView.findViewById(R.id.editText_litres);
            EditText editTotalCost = (EditText) rootView.findViewById(R.id.editText_cost);
            EditText editKm = (EditText) rootView.findViewById(R.id.editText_km);
            EditText editDate = (EditText) rootView.findViewById(R.id.editText_date);

            float litres = 0;
            float totalCost = 0;
            float km = 0;
            Date date;

            try{
                litres = Float.parseFloat(editLitres.getText().toString());
            }catch (Exception ex){
                Toast.makeText(rootView.getContext(), R.string.parseError_litres, Toast.LENGTH_SHORT).show();
                success = false;
            }
            try{
                totalCost = Float.parseFloat(editTotalCost.getText().toString());
            }catch (Exception ex){
                Toast.makeText(rootView.getContext(), R.string.parseError_total, Toast.LENGTH_SHORT).show();
                success = false;
            }
            try{
                km = Float.parseFloat(editKm.getText().toString());
            }catch (Exception ex){
                Toast.makeText(rootView.getContext(), R.string.parseError_km, Toast.LENGTH_SHORT).show();
                success = false;
            }
            try{
                DateFormat df = new SimpleDateFormat(getString(R.string.default_date_format), Locale.ENGLISH);
                date =  df.parse(editDate.getText().toString());
            }catch (Exception ex){
                Toast.makeText(rootView.getContext(), R.string.parseError_date, Toast.LENGTH_SHORT).show();
                success = false;
            }
            return success;
        }

        private static void autocompleteEmptyField(View rootView){
            EditText viewLitres = (EditText) rootView.findViewById(R.id.editText_litres);
            EditText viewTotalCost = (EditText) rootView.findViewById(R.id.editText_cost);
            EditText viewCostPerLitre = (EditText) rootView.findViewById(R.id.editText_costPerLitre);

            String litres = viewLitres.getText().toString();
            String total = viewTotalCost.getText().toString();
            String costPerLitre = viewCostPerLitre.getText().toString();

            if(isOneOfTheseAnEmptyString(litres, total, costPerLitre))
            {
                if(litres.equals("") && viewLitres.isFocused()){
                    //complete missing litres value
                    try{
                        float f_total = Float.parseFloat(total);
                        float f_costPerLitre = Float.parseFloat(costPerLitre);
                        viewLitres.setText(new StringBuilder().append(f_total/f_costPerLitre));
                    }catch(Exception ex){
                        Toast.makeText(rootView.getContext(), R.string.parseError_1, Toast.LENGTH_SHORT).show();
                    }
                }else if(total.equals("") && viewTotalCost.isFocused()){
                    //complete missing total value
                    try{
                        float f_litres = Float.parseFloat(litres);
                        float f_costPerLitre = Float.parseFloat(costPerLitre);
                        viewTotalCost.setText(new StringBuilder().append(f_litres*f_costPerLitre));
                    }catch(Exception ex){
                        Toast.makeText(rootView.getContext(), R.string.parseError_2, Toast.LENGTH_SHORT).show();
                    }
                }else if(costPerLitre.equals("") && viewCostPerLitre.isFocused()){
                    //complete missing costPerLitre value
                    try{
                        float f_litres = Float.parseFloat(litres);
                        float f_total = Float.parseFloat(total);
                        viewCostPerLitre.setText(new StringBuilder().append(f_total/f_litres));
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
            View rootView = inflater.inflate(R.layout.fragment_log, container, false);
            FuelstampController.getInstance(getContext());
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
            View rootView = inflater.inflate(R.layout.fragment_analytics, container, false);
            return rootView;
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
