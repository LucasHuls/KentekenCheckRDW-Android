package nl.lucashuls.kentekencheck.ui.result;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

import java.util.List;

import nl.lucashuls.kentekencheck.R;
import nl.lucashuls.kentekencheck.data.entities.Kenteken;
import nl.lucashuls.kentekencheck.data.entities.KentekenWithMaintenance;
import nl.lucashuls.kentekencheck.data.entities.Maintenance;
import nl.lucashuls.kentekencheck.ui.home.HomeViewModel;
import nl.lucashuls.kentekencheck.ui.maintenance.AddMaintenanceActivity;
import nl.lucashuls.kentekencheck.utils.DatePickerUtil;

public class ManageCarActivity extends AppCompatActivity {

    private HomeViewModel homeViewModel;
    private Kenteken currentKentekenObject;
    private String currentKenteken;
    private EditText editTextTag;
    private Button buttonAanschafDate;
    private CheckBox checkBoxAlerts;
    private ImageView imageViewLogo;
    private TextView textViewKenteken;
    private TextView textViewMerk;
    private LinearLayout maintenanceContainer;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_car);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        editTextTag = findViewById(R.id.editTextTag);
        checkBoxAlerts = findViewById(R.id.checkBoxAlerts);
        Button buttonSave = findViewById(R.id.buttonSave);
        imageViewLogo = findViewById(R.id.imageViewLogo);
        textViewKenteken = findViewById(R.id.textViewKenteken);
        textViewMerk = findViewById(R.id.textViewMerk);
        buttonAanschafDate = findViewById(R.id.buttonAanschafDate);
        maintenanceContainer = findViewById(R.id.maintenanceContainer);
        ImageButton buttonAddMaintenance = findViewById(R.id.buttonAddMaintenance);

        currentKenteken = getIntent().getStringExtra("kenteken");

        MutableLiveData<Kenteken> liveData = new MutableLiveData<>();
        homeViewModel.getKentekenByKenteken(currentKenteken, liveData);
        liveData.observe(this, kenteken -> {
            if (kenteken != null) {
                currentKentekenObject = kenteken;
                loadCarData(kenteken);
            }
        });
        homeViewModel.getKentekensWithMaintenance().observe(this, kentekensWithMaintenance -> {
            // Display maintenance data for the current kenteken
            for (KentekenWithMaintenance k : kentekensWithMaintenance) {
                if (k.kenteken.kenteken.equals(currentKenteken)) {
                    displayMaintenanceData(k.maintenanceList);
                }
            }
        });

        // Add maintenance record for the current kenteken
        buttonAddMaintenance.setOnClickListener(v -> {
            Intent intent = new Intent(ManageCarActivity.this, AddMaintenanceActivity.class);
            intent.putExtra("kenteken", currentKenteken);
            startActivity(intent);
        });

        // Save changes to the current kenteken
        buttonSave.setOnClickListener(v -> {
            if (currentKentekenObject != null) {
                currentKentekenObject.tag = editTextTag.getText().toString();
                currentKentekenObject.alertsEnabled = checkBoxAlerts.isChecked();
                homeViewModel.update(currentKentekenObject);
                Toast.makeText(ManageCarActivity.this, getString(R.string.car_data_saved), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Show date picker dialog for the aanschaf date of the current kenteken
        buttonAanschafDate.setOnClickListener(v -> DatePickerUtil.showDatePickerDialog(ManageCarActivity.this, currentKentekenObject.AanschafDate, date -> {
            currentKentekenObject.AanschafDate = date;
            buttonAanschafDate.setText(date);
        }));
    }

    // Load car data into the UI elements
    private void loadCarData(Kenteken kenteken) {
        textViewKenteken.setText(kenteken.kenteken);
        textViewMerk.setText(kenteken.merk + " " + kenteken.model);
        editTextTag.setText(kenteken.tag);
        checkBoxAlerts.setChecked(kenteken.alertsEnabled);
        buttonAanschafDate.setText(kenteken.AanschafDate);

        Glide.with(this)
                .load(kenteken.logoUrl)
                .error(R.drawable.sharp_car_crash_24)
                .into(imageViewLogo);
    }

    // Display maintenance data for the current kenteken
    private void displayMaintenanceData(List<Maintenance> maintenanceList) {
        maintenanceContainer.removeAllViews();
        for (Maintenance maintenance : maintenanceList) {
            View maintenanceItemView = getLayoutInflater().inflate(R.layout.item_maintenance, maintenanceContainer, false);

            TextView dateTextView = maintenanceItemView.findViewById(R.id.dateTextView);
            TextView descriptionTextView = maintenanceItemView.findViewById(R.id.descriptionTextView);
            ImageButton deleteButton = maintenanceItemView.findViewById(R.id.deleteButton);

            dateTextView.setText(maintenance.date);
            descriptionTextView.setText(maintenance.description);

            deleteButton.setOnClickListener(v -> showConfirmDialog(maintenance));

            maintenanceContainer.addView(maintenanceItemView);
        }
    }

    // Show confirmation dialog for deleting a maintenance record
    private void showConfirmDialog(Maintenance maintenance) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm_deletion))
                .setMessage(getString(R.string.confirm_deletion_message))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    homeViewModel.deleteMaintenance(maintenance);
                    Toast.makeText(ManageCarActivity.this, getString(R.string.maintenance_record_deleted), Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }
}
