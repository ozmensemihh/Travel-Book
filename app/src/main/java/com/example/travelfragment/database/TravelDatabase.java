package com.example.travelfragment.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.travelfragment.model.Travel;

@Database(entities = {Travel.class},version = 1)
public abstract class TravelDatabase extends RoomDatabase {

    public abstract TraavelDAO getTravelDAO();

}
