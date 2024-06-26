package nl.lucashuls.kentekencheck.ui.result;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import nl.lucashuls.kentekencheck.R;
import nl.lucashuls.kentekencheck.data.entities.Kenteken;
import nl.lucashuls.kentekencheck.ui.home.HomeViewModel;
import nl.lucashuls.kentekencheck.utils.CarImagesLoaderUtils;
import nl.lucashuls.kentekencheck.utils.PDFUtil;

public class ResultActivity extends AppCompatActivity {

    private ResultAdapter resultAdapter;
    private ImageView imageViewLogo;
    private ImageView imageViewLogoTopBar;
    private HomeViewModel homeViewModel;
    private String currentKenteken;
    private Kenteken currentKentekenObject;
    private Button buttonFavorite;
    private Button buttonManageCar;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        RecyclerView recyclerViewResults = findViewById(R.id.recyclerViewResults);
        recyclerViewResults.setLayoutManager(new LinearLayoutManager(this));
        imageViewLogo = findViewById(R.id.imageViewLogo);
        imageViewLogoTopBar = findViewById(R.id.imageViewLogoTopBar);
        buttonFavorite = findViewById(R.id.buttonFavorite);
        buttonManageCar = findViewById(R.id.buttonManageCar);

        ImageButton buttonShare = findViewById(R.id.buttonShare);
        buttonShare.setOnClickListener(v -> sharePDF());

        // Get the JSON data from the Intent
        String jsonData = getIntent().getStringExtra("resultData");

        // Parse JSON data and set up the adapter
        try {
            JSONArray jsonArray;
            if (jsonData != null && jsonData.startsWith("[")) {
                jsonArray = new JSONArray(jsonData);
            } else {
                jsonArray = new JSONArray();
                jsonArray.put(new JSONObject(jsonData));
            }
            resultAdapter = new ResultAdapter(jsonArray);
            recyclerViewResults.setAdapter(resultAdapter);

            // Get the "kenteken", "merk", "type" and "bouwjaar" from the first result to load the logo and check favorites
            if (jsonArray.length() > 0) {
                JSONObject firstResult = jsonArray.getJSONObject(0);
                currentKenteken = firstResult.optString("kenteken", "N/A");
                String merk = firstResult.optString("merk", "N/A");
                String type = firstResult.optString("handelsbenaming", "N/A");
                String bouwjaar = firstResult.optString("datum_eerste_toelating", "N/A").substring(0, 4);
                CarImagesLoaderUtils.loadCarImage(this, merk, type, bouwjaar, imageViewLogoTopBar);
                CarImagesLoaderUtils.loadLogo(this, merk, imageViewLogo);

                // Check if the current kenteken is already a favorite
                MutableLiveData<Kenteken> liveData = new MutableLiveData<>();
                homeViewModel.getKentekenByKenteken(currentKenteken, liveData);
                liveData.observe(this, existingKenteken -> {
                    if (existingKenteken != null) {
                        currentKentekenObject = existingKenteken;
                        buttonFavorite.setText(R.string.delete);
                        buttonFavorite.setBackgroundTintList(getResources().getColorStateList(R.color.error));
                        buttonManageCar.setVisibility(View.VISIBLE);  // Show the button if it's a favorite
                    } else {
                        buttonFavorite.setText(R.string.add_favorite);
                        buttonFavorite.setBackgroundTintList(getResources().getColorStateList(R.color.accent_dark));
                        buttonManageCar.setVisibility(View.GONE);  // Hide the button if it's not a favorite
                    }
                });

                // Add or remove the current kenteken from favorites
                buttonFavorite.setOnClickListener(v -> {
                    if (currentKentekenObject != null) {
                        showConfirmDialog(currentKentekenObject);
                    } else {
                        Kenteken kenteken = new Kenteken(
                                currentKenteken,
                                firstResult.optString("merk", "N/A"),
                                firstResult.optString("handelsbenaming", "N/A"),
                                CarImagesLoaderUtils.getLogoUrl(firstResult.optString("merk", "N/A"))
                        );
                        kenteken.vervaldatum_apk_dt = firstResult.optString("vervaldatum_apk", "");
                        homeViewModel.insert(kenteken);
                        currentKentekenObject = kenteken;
                        buttonFavorite.setText(R.string.delete);
                        buttonFavorite.setBackgroundTintList(getResources().getColorStateList(R.color.error));
                        buttonManageCar.setVisibility(View.VISIBLE);  // Show the button as it's now a favorite
                        Toast.makeText(ResultActivity.this, R.string.kenteken_added_to_favorites, Toast.LENGTH_SHORT).show();
                    }
                });

                // Manage the current kenteken if it's a favorite
                buttonManageCar.setOnClickListener(v -> {
                    Intent intent = new Intent(ResultActivity.this, ManageCarActivity.class);
                    intent.putExtra("kenteken", currentKenteken);
                    startActivity(intent);
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Share the PDF with other apps on the device
    private void sharePDF() {
        StringBuilder content = new StringBuilder();
        String kenteken = "";

        // Loop through the results and add the data to the PDF content
        for (int i = 0; i < resultAdapter.getItemCount(); i++) {
            JSONObject jsonObject = resultAdapter.getItem(i);
            // Use the first kenteken as the filename
            if (i == 0) {
                kenteken = jsonObject.optString("kenteken", "N/A");
            }
            content.append("Kenteken: ").append(jsonObject.optString("kenteken", "N/A")).append("\n");
            content.append("Merk: ").append(jsonObject.optString("merk", "N/A")).append("\n");
            content.append("Model: ").append(jsonObject.optString("handelsbenaming", "N/A")).append("\n");
            content.append("Kleur: ").append(jsonObject.optString("eerste_kleur", "N/A")).append("\n");
            content.append("Bouwjaar: ").append(formatDate(jsonObject.optString("datum_eerste_toelating", "N/A"))).append("\n");
            content.append("Nieuwprijs: ").append(jsonObject.optInt("catalogusprijs", 0) == 0 ? "N/A" : "€" + jsonObject.optInt("catalogusprijs")).append("\n");
            content.append("Voertuig Lengte: ").append(jsonObject.optString("lengte", "N/A")).append(" cm\n");
            content.append("Voertuig Breedte: ").append(jsonObject.optString("breedte", "N/A")).append(" cm\n");
            content.append("Voertuig Hoogte: ").append(jsonObject.optString("hoogte_voertuig", "N/A")).append(" cm\n");
            content.append("Massa Rijklaar: ").append(jsonObject.optString("massa_rijklaar", "N/A")).append(" kg\n");
            content.append("Toegestane max. massa voertuig: ").append(jsonObject.optString("toegestane_maximum_massa_voertuig", "N/A")).append(" kg\n");
            content.append("Maximale laadvermogen: ").append(jsonObject.optString("massa_ledig_voertuig", "N/A")).append(" kg\n");
            content.append("Aanhanger ongeremd: ").append(jsonObject.optString("maximum_massa_trekken_ongeremd", "N/A")).append(" kg\n");
            content.append("Aanhanger geremd: ").append(jsonObject.optString("maximum_trekken_massa_geremd", "N/A")).append(" kg\n");
            content.append("Vervaldatum APK: ").append(formatDate(jsonObject.optString("vervaldatum_apk", "N/A"))).append("\n");
            content.append("WOK melding: ").append(jsonObject.optString("wacht_op_keuren", "N/A")).append("\n");
            content.append("Datum eerste toelating: ").append(formatDate(jsonObject.optString("datum_eerste_toelating", "N/A"))).append("\n");
            content.append("Datum eerste tenaamstelling in Nederland: ").append(formatDate(jsonObject.optString("datum_eerste_tenaamstelling_in_nederland", "N/A"))).append("\n");
            content.append("Bruto BPM: ").append(jsonObject.optString("bruto_bpm", "N/A")).append("\n");
            content.append("Inrichting: ").append(jsonObject.optString("inrichting", "N/A")).append("\n");
            content.append("Aantal zitplaatsen: ").append(jsonObject.optString("aantal_zitplaatsen", "N/A")).append("\n");
            content.append("WAM verzekerd: ").append(jsonObject.optString("wam_verzekerd", "N/A")).append("\n");
            content.append("Aantal deuren: ").append(jsonObject.optString("aantal_deuren", "N/A")).append("\n");
            content.append("Aantal wielen: ").append(jsonObject.optString("aantal_wielen", "N/A")).append("\n");
            content.append("Plaats chassisnummer: ").append(jsonObject.optString("plaats_chassisnummer", "N/A")).append("\n");
            content.append("Zuinigheidsclassificatie: ").append(jsonObject.optString("zuinigheidslabel", "N/A")).append("\n");
            content.append("Aantal cilinders: ").append(jsonObject.optString("aantal_cilinders", "N/A")).append("\n");
            content.append("Cilinderinhoud: ").append(jsonObject.optString("cilinderinhoud", "N/A")).append(" cc\n");
            content.append("\n");
        }

        // Create the PDF file and share it
        try {
            File pdfFile = PDFUtil.createPDF(this, kenteken, content.toString());
            Uri pdfUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", pdfFile);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, pdfUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_pdf_using)));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.failed_to_create_pdf, Toast.LENGTH_SHORT).show();
        }
    }

    // Format the date from "yyyyMMdd" to "dd-MM-yyyy"
    private String formatDate(String dateStr) {
        if (dateStr.equals("N/A")) {
            return dateStr;
        }
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateStr;
        }
    }

    // Show a confirmation dialog before removing the favorite
    private void showConfirmDialog(Kenteken existingKenteken) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm)
                .setMessage(R.string.remove_favorite_confirmation)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    homeViewModel.delete(existingKenteken);
                    currentKentekenObject = null;
                    buttonFavorite.setText(R.string.add_favorite);
                    buttonFavorite.setBackgroundTintList(getResources().getColorStateList(R.color.accent_dark));
                    buttonManageCar.setVisibility(View.GONE);  // Hide the button as it's no longer a favorite
                    Toast.makeText(ResultActivity.this, R.string.kenteken_removed_from_favorites, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }
}
