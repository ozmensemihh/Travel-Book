package com.example.travelfragment.database;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.travelfragment.model.Travel;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface TraavelDAO {


    @Query("select * from Travel")
    Flowable<List<Travel>> gelAll();

    @Query("select * from Travel where id=:id")
    Flowable<Travel> getById(int id);

    @Insert
    Completable insert(Travel travel);

    @Delete
    Completable delete(Travel travel);

    @Query("Select * from  Travel where name LIKE ' % ' || :search || ' % ' " )
    Flowable<List<Travel>> search(String search);

}
