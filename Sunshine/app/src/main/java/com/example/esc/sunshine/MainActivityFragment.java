package com.example.esc.sunshine;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import Controller.WeatherController;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    public ArrayList<String>values;
    public static ArrayAdapter<String>adapter;
    public ListView list;
    public MainActivityFragment() {
        values = new ArrayList<>();
        values.add("saturday-sunny-88/63");
        values.add("today-cloudy-88/63");
        values.add("sunday-heavy rain-88/63");
        values.add("monday-sunny-88/63");
        values.add("tuesday-foggy-88/63");
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        adapter = new ArrayAdapter<String>(getContext(),R.layout.list_item_forecast,R.id.list_item_forecast_textview
        ,values);
        list = (ListView) view.findViewById(R.id.listView_forecast);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getContext(),adapter.getItem(position),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(),DetailActivity.class).putExtra("weather",adapter.getItem(position));
                startActivity(intent);
            }
        });
        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
      inflater.inflate(R.menu.forecastfragment, menu);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.action_refresh){
            WeatherController.getWeather();
        }
        return true;
    }
}
