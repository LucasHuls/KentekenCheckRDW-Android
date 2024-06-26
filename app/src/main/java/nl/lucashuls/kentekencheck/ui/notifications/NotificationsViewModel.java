package nl.lucashuls.kentekencheck.ui.notifications;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import nl.lucashuls.kentekencheck.data.entities.Kenteken;
import nl.lucashuls.kentekencheck.data.repository.KentekenRepository;

public class NotificationsViewModel extends AndroidViewModel {

    private final LiveData<List<Kenteken>> kentekensWithAlerts;

    public NotificationsViewModel(Application application) {
        super(application);
        KentekenRepository repository = new KentekenRepository(application);
        kentekensWithAlerts = repository.getKentekensWithAlerts();
    }

    public LiveData<List<Kenteken>> getKentekensWithAlerts() {
        return kentekensWithAlerts;
    }
}
