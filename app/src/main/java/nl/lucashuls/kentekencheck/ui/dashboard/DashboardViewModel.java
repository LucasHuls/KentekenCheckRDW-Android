package nl.lucashuls.kentekencheck.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONArray;

import nl.lucashuls.kentekencheck.utils.RDWApiUtils;

public class DashboardViewModel extends ViewModel {
    private final MutableLiveData<String> result;

    public DashboardViewModel() {
        result = new MutableLiveData<>();
    }

    public LiveData<String> getResult() {
        return result;
    }

    // Search for the kenteken based on the input and update the result
    public void searchKenteken(String kenteken) {
        RDWApiUtils.fetchKentekenData(null, kenteken, new RDWApiUtils.RDWApiCallback() {
            @Override
            public void onSuccess(JSONArray data) {
                if (data.length() == 0) {
                    result.postValue("Error: No data found for the given kenteken");
                } else {
                    result.postValue(data.toString());
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                result.postValue("Error: " + errorMessage);
            }
        });
    }


}
