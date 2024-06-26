package nl.lucashuls.kentekencheck.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import nl.lucashuls.kentekencheck.data.entities.Kenteken;
import nl.lucashuls.kentekencheck.data.entities.KentekenWithMaintenance;

@Dao
public interface KentekenDao {

    @Insert
    void insert(Kenteken kenteken);

    @Delete
    void delete(Kenteken kenteken);

    @Update
    void update(Kenteken kenteken);

    @Query("SELECT * FROM kenteken_table WHERE kenteken = :kenteken LIMIT 1")
    Kenteken getKentekenByKenteken(String kenteken);

    @Query("SELECT * FROM kenteken_table")
    LiveData<List<Kenteken>> getAllKentekens();

    @Query("SELECT * FROM kenteken_table WHERE alertsEnabled = 1")
    LiveData<List<Kenteken>> getKentekensWithAlerts();

    @Transaction
    @Query("SELECT * FROM kenteken_table")
    LiveData<List<KentekenWithMaintenance>> getKentekensWithMaintenance();
}
