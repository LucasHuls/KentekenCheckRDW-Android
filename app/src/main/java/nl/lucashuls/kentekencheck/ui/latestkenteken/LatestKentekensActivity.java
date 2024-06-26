package nl.lucashuls.kentekencheck.ui.latestkenteken;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

import nl.lucashuls.kentekencheck.R;
import nl.lucashuls.kentekencheck.utils.LoadingSpinnerUtil;
import nl.lucashuls.kentekencheck.utils.RDWApiUtils;

public class LatestKentekensActivity extends AppCompatActivity {
    private RecyclerView recyclerViewLatestKentekens;
    private LatestKentekensAdapter latestKentekensAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latest_kentekens);

        recyclerViewLatestKentekens = findViewById(R.id.recyclerViewLatestKentekens);
        recyclerViewLatestKentekens.setLayoutManager(new LinearLayoutManager(this));

        fetchLatestKentekens();
    }

    private void fetchLatestKentekens() {
        // Show spinner before starting the data fetch
        LoadingSpinnerUtil.showSpinner((ViewGroup) findViewById(R.id.fullscreen_spinner).getParent());

        // Fetch latest kentekens data from RDW API and populate the recycler view
        RDWApiUtils.fetchLatestKentekensData(this, new RDWApiUtils.RDWApiCallback() {
            @Override
            public void onSuccess(JSONArray data) {
                // Hide spinner once the data is loaded
                LoadingSpinnerUtil.hideSpinner((ViewGroup) findViewById(R.id.fullscreen_spinner).getParent());
                latestKentekensAdapter = new LatestKentekensAdapter(data, LatestKentekensActivity.this);
                recyclerViewLatestKentekens.setAdapter(latestKentekensAdapter);
            }

            @Override
            public void onFailure(String errorMessage) {
                // Hide spinner and show error message
                LoadingSpinnerUtil.hideSpinner((ViewGroup) findViewById(R.id.fullscreen_spinner).getParent());
                Toast.makeText(LatestKentekensActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
