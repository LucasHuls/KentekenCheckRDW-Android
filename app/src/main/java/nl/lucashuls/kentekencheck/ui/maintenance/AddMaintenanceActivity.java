package nl.lucashuls.kentekencheck.ui.maintenance;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import nl.lucashuls.kentekencheck.R;
import nl.lucashuls.kentekencheck.data.entities.Maintenance;
import nl.lucashuls.kentekencheck.ui.home.HomeViewModel;
import nl.lucashuls.kentekencheck.utils.DatePickerUtil;

public class AddMaintenanceActivity extends AppCompatActivity {

    private EditText editTextDescription;
    private Button buttonDate;
    private HomeViewModel homeViewModel;
    private String kenteken;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_maintenance);

        editTextDescription = findViewById(R.id.editTextDescription);
        buttonDate = findViewById(R.id.buttonDate);
        Button buttonAdd = findViewById(R.id.buttonAdd);

        kenteken = getIntent().getStringExtra("kenteken");

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Initialize the date button with the current date
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        buttonDate.setText(currentDate);

        buttonDate.setOnClickListener(v -> DatePickerUtil.showDatePickerDialog(AddMaintenanceActivity.this, buttonDate.getText().toString(), date -> buttonDate.setText(date)));

        // Add the maintenance record to the database when the add button is clicked and all fields are filled
        buttonAdd.setOnClickListener(v -> {
            String description = editTextDescription.getText().toString();
            String date = buttonDate.getText().toString();

            if (description.isEmpty() || date.isEmpty()) {
                Toast.makeText(AddMaintenanceActivity.this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
                return;
            }

            Maintenance maintenance = new Maintenance(kenteken, description, date);
            homeViewModel.insertMaintenance(maintenance);
            Toast.makeText(AddMaintenanceActivity.this, getString(R.string.maintenance_record_added), Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
