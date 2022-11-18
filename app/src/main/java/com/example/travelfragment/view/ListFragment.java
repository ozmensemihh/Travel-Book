package com.example.travelfragment.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.travelfragment.R;
import com.example.travelfragment.adapter.TravelAdapter;
import com.example.travelfragment.database.TraavelDAO;
import com.example.travelfragment.database.TravelDatabase;
import com.example.travelfragment.databinding.FragmentListBinding;
import com.example.travelfragment.model.Travel;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class ListFragment extends Fragment {

    TravelAdapter adapter;
    TraavelDAO dao;
    TravelDatabase travelDatabase;
    private FragmentListBinding binding;
    private CompositeDisposable disposable = new CompositeDisposable();

    public ListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        travelDatabase = Room.databaseBuilder(requireContext(),TravelDatabase.class,"travel").build();
        dao = travelDatabase.getTravelDAO();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentListBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getData();

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),3,RecyclerView.VERTICAL,false);
        binding.rv.setLayoutManager(layoutManager);

    }

    private void getData(){
        disposable.add(dao.gelAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ListFragment.this::handleResponse));
    }

    private void handleResponse(List<Travel> list){
        adapter = new TravelAdapter(list);
        binding.rv.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
        disposable.clear();
    }
}