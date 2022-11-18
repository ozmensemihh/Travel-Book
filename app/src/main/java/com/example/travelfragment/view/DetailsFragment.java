package com.example.travelfragment.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.net.wifi.WifiEnterpriseConfig;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.room.Room;
import androidx.room.util.ViewInfo;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travelfragment.R;
import com.example.travelfragment.database.TraavelDAO;
import com.example.travelfragment.database.TravelDatabase;
import com.example.travelfragment.databinding.FragmentDetailsBinding;
import com.example.travelfragment.model.Travel;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class DetailsFragment extends Fragment {

    SQLiteDatabase sqLiteDatabase;
    String info="";
    Bitmap selectedBitmap;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionRsultLauncher;
    private FragmentDetailsBinding binding;
    CompositeDisposable disposable = new CompositeDisposable();
    TravelDatabase travelDatabase;
    TraavelDAO dao;
    Travel travel;

    public DetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerLauncher();
        travelDatabase = Room.databaseBuilder(requireContext(),TravelDatabase.class,"travel").build();
        dao = travelDatabase.getTravelDAO();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentDetailsBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sqLiteDatabase = requireActivity().openOrCreateDatabase("travel", Context.MODE_PRIVATE,null);


        if(getArguments() != null){
            info = DetailsFragmentArgs.fromBundle(getArguments()).getInfo();
        }else {
            info="new";
        }

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save(view);
            }
        });

        binding.imgFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(view);
            }
        });

        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete(view);
            }
        });

        if (info.equals("new")){
            binding.edtArtname.setText("");
            binding.edtArtistname.setText("");
            binding.edtYear.setText("");
            binding.imgFoto.setImageResource(R.drawable.ic_baseline_photo_size_select_actual_24);
            binding.btnSave.setVisibility(View.VISIBLE);
            binding.btnDelete.setVisibility(View.GONE);

        }else{
            int id = DetailsFragmentArgs.fromBundle(getArguments()).getTravelid();
            binding.btnSave.setVisibility(View.GONE);
            binding.btnDelete.setVisibility(View.VISIBLE);
            binding.imgFoto.setClickable(false);

            binding.edtArtname.setEnabled(false);


            binding.edtArtistname.setEnabled(false);
            binding.edtYear.setEnabled(false);

            disposable.add(dao.getById(id)
                    .observeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(DetailsFragment.this::handleResponseOld));
        }

    }

    private void handleResponseOld(Travel responsetravel){
        travel = responsetravel;
        binding.edtArtname.setText(travel.getArtname());
        binding.edtArtistname.setText(travel.getArtistname());
        binding.edtYear.setText(travel.getYear());
        Bitmap bitmap = BitmapFactory.decodeByteArray(travel.image,0,travel.image.length);
        binding.imgFoto.setImageBitmap(bitmap);

    }

    private void selectImage(View view){
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"İzin gerekli",Snackbar.LENGTH_INDEFINITE).setAction("Evet", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        permissionRsultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            }else {
                permissionRsultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }else{
            Intent intentToGalery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGalery);
        }
    }

    private void save(View view){
        String name = binding.edtArtname.getText().toString();
        String artistName = binding.edtArtistname.getText().toString();
        String year = binding.edtYear.getText().toString();

        Bitmap bitmap = makeSmaller(selectedBitmap,300);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] byteImage = outputStream.toByteArray();

        Travel travel = new Travel(name,artistName,year,byteImage);

        disposable.add(dao.insert(travel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(DetailsFragment.this::handleResponse));
    }

    private void delete(View view){
        disposable.add(dao.delete(travel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(DetailsFragment.this::handleResponse));
    }

    private void handleResponse(){
        NavDirections directions = DetailsFragmentDirections.actionDetailsFragmentToListFragment();
        Navigation.findNavController(requireView()).navigate(directions);
    }

    private void  registerLauncher(){
        permissionRsultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result){
                    Intent intentToGalery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGalery);
                }else {
                    Toast.makeText(requireContext(),"İzin gerekli",Toast.LENGTH_LONG).show();
                }
            }
        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode()== Activity.RESULT_OK){
                    Intent responseIntent = result.getData();
                    if (responseIntent != null){
                        Uri bitmapUri = responseIntent.getData();
                        try {

                            if(Build.VERSION.SDK_INT>28){
                                ImageDecoder.Source source = ImageDecoder.createSource(requireActivity().getContentResolver(),bitmapUri);
                                selectedBitmap = ImageDecoder.decodeBitmap(source);
                                binding.imgFoto.setImageBitmap(selectedBitmap);
                            }else{
                                selectedBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),bitmapUri);
                                binding.imgFoto.setImageBitmap(selectedBitmap);
                            }

                        }catch (Exception e){

                            e.printStackTrace();
                        }


                    }

                }
            }
        });
    }


    private Bitmap makeSmaller(Bitmap bitmap, int maxSize){
        int with = bitmap.getWidth();
        int height = bitmap.getHeight();
        float rotate = (float) with / (float) height;
        if (rotate > 1){
            with = maxSize;
            height = (int) (with/rotate);
        }else {
            height = maxSize;
            with = (int) (height/rotate);
        }

        return Bitmap.createScaledBitmap(bitmap,with,height,true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding=null;
        disposable.clear();
    }
}