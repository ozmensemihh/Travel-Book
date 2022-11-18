package com.example.travelfragment.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.room.Room;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.travelfragment.R;
import com.example.travelfragment.adapter.TravelAdapter;
import com.example.travelfragment.database.TraavelDAO;
import com.example.travelfragment.database.TravelDatabase;
import com.example.travelfragment.databinding.ActivityMainBinding;
import com.example.travelfragment.databinding.FragmentListBinding;
import com.example.travelfragment.model.Travel;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    ActivityMainBinding binding;
    TraavelDAO dao;
    TravelDatabase travelDatabase;
    FragmentListBinding fragmentListBinding;
    TravelAdapter adapter;
    private CompositeDisposable disposable = new CompositeDisposable();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        travelDatabase = Room.databaseBuilder(getApplicationContext(),TravelDatabase.class,"travel").build();
        dao = travelDatabase.getTravelDAO();

        fragmentListBinding = FragmentListBinding.inflate(getLayoutInflater());

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        MenuItem item =  menu.findItem(R.id.action_search);
        SearchView searchView= (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_add){
            NavDirections directions = ListFragmentDirections.actionListFragmentToDetailsFragment("new",0);
            Navigation.findNavController(this,R.id.fragmentContainerView).navigate(directions);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        disposable.add(dao.search(s)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(MainActivity.this::handleResponse));
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        disposable.add(dao.search(s)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(MainActivity.this::handleResponse));
        return true;
    }

    private void handleResponse(List<Travel> list){
        adapter = new TravelAdapter(list);
        fragmentListBinding.rv.setAdapter(adapter);
        fragmentListBinding.rv.getAdapter().notifyDataSetChanged();
        System.out.println(list.get(0).getArtname());
    }
}