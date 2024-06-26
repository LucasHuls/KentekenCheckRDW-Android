package nl.lucashuls.kentekencheck.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

import nl.lucashuls.kentekencheck.data.entities.Maintenance;

@Dao
public interface MaintenanceDao {

    @Insert
    void insert(Maintenance maintenance);

    @Delete
    void delete(Maintenance maintenance);

    @Update
    void update(Maintenance maintenance);

}
