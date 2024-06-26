package nl.lucashuls.kentekencheck.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import nl.lucashuls.kentekencheck.data.AppDatabase;
import nl.lucashuls.kentekencheck.data.dao.KentekenDao;
import nl.lucashuls.kentekencheck.data.dao.MaintenanceDao;
import nl.lucashuls.kentekencheck.data.entities.Kenteken;
import nl.lucashuls.kentekencheck.data.entities.KentekenWithMaintenance;
import nl.lucashuls.kentekencheck.data.entities.Maintenance;

public class KentekenRepository {

    private final KentekenDao kentekenDao;
    private final MaintenanceDao maintenanceDao;
    private final LiveData<List<Kenteken>> allKentekens;
    private final LiveData<List<KentekenWithMaintenance>> kentekensWithMaintenance;
    private final LiveData<List<Kenteken>> kentekensWithAlerts;

    public KentekenRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        kentekenDao = db.kentekenDao();
        maintenanceDao = db.maintenanceDao();
        allKentekens = kentekenDao.getAllKentekens();
        kentekensWithMaintenance = kentekenDao.getKentekensWithMaintenance();
        kentekensWithAlerts = kentekenDao.getKentekensWithAlerts();
    }

    public LiveData<List<Kenteken>> getAllKentekens() {
        return allKentekens;
    }

    public void insert(Kenteken kenteken) {
        AppDatabase.databaseWriteExecutor.execute(() -> kentekenDao.insert(kenteken));
    }

    public void delete(Kenteken kenteken) {
        AppDatabase.databaseWriteExecutor.execute(() -> kentekenDao.delete(kenteken));
    }

    public void update(Kenteken kenteken) {
        AppDatabase.databaseWriteExecutor.execute(() -> kentekenDao.update(kenteken));
    }

    public Kenteken getKentekenByKenteken(String kenteken) {
        return kentekenDao.getKentekenByKenteken(kenteken);
    }

    public void insertMaintenance(Maintenance maintenance) {
        AppDatabase.databaseWriteExecutor.execute(() -> maintenanceDao.insert(maintenance));
    }

    public LiveData<List<KentekenWithMaintenance>> getKentekensWithMaintenance() {
        return kentekensWithMaintenance;
    }

    public LiveData<List<Kenteken>> getKentekensWithAlerts() {
        return kentekensWithAlerts;
    }

    public void deleteMaintenance(Maintenance maintenance) {
        AppDatabase.databaseWriteExecutor.execute(() -> maintenanceDao.delete(maintenance));
    }
}
