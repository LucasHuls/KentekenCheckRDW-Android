package nl.lucashuls.kentekencheck.ui.home;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import nl.lucashuls.kentekencheck.data.entities.Kenteken;
import nl.lucashuls.kentekencheck.data.entities.KentekenWithMaintenance;
import nl.lucashuls.kentekencheck.data.entities.Maintenance;
import nl.lucashuls.kentekencheck.data.repository.KentekenRepository;

public class HomeViewModel extends AndroidViewModel {

    private final KentekenRepository repository;
    private final LiveData<List<Kenteken>> allKentekens;
    private final LiveData<List<KentekenWithMaintenance>> kentekensWithMaintenance;
    private final ExecutorService executorService;

    public HomeViewModel(Application application) {
        super(application);
        repository = new KentekenRepository(application);
        allKentekens = repository.getAllKentekens();
        kentekensWithMaintenance = repository.getKentekensWithMaintenance();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Kenteken>> getAllKentekens() {
        return allKentekens;
    }

    public void insert(Kenteken kenteken) {
        repository.insert(kenteken);
    }

    public void delete(Kenteken kenteken) {
        repository.delete(kenteken);
    }

    public void update(Kenteken kenteken) {
        repository.update(kenteken);
    }

    public void insertMaintenance(Maintenance maintenance) {
        repository.insertMaintenance(maintenance);
    }

    public LiveData<List<KentekenWithMaintenance>> getKentekensWithMaintenance() {
        return kentekensWithMaintenance;
    }

    // Get Kenteken by Kenteken from the database in a separate thread
    public void getKentekenByKenteken(String kenteken, MutableLiveData<Kenteken> liveData) {
        executorService.execute(() -> {
            Kenteken result = repository.getKentekenByKenteken(kenteken);
            liveData.postValue(result);
        });
    }

    // Delete Maintenance from the database in a separate thread
    public void deleteMaintenance(Maintenance maintenance) {
        executorService.execute(() -> repository.deleteMaintenance(maintenance));
    }
}
