package nl.lucashuls.kentekencheck.ui.result;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import nl.lucashuls.kentekencheck.R;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {

    private final JSONArray results;

    public ResultAdapter(JSONArray results) {
        this.results = results;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            JSONObject jsonObject = results.getJSONObject(position);


            holder.textViewTitle.setText(jsonObject.optString("merk", "N/A"));
            holder.textViewType.setText(jsonObject.optString("handelsbenaming", "N/A"));
            // Set Summary
            String summary = generateSummary(jsonObject);
            holder.textViewSummary.setText(summary);

            // Set Basic Info
            holder.textViewKenteken.setText("Kenteken: " + jsonObject.optString("kenteken", "N/A"));
            holder.textViewMerk.setText("Merk: " + jsonObject.optString("merk", "N/A"));

            // Set Model Info
            holder.textViewModel.setText("Model: " + jsonObject.optString("handelsbenaming", "N/A"));
            holder.textViewKleur.setText("Kleur: " + jsonObject.optString("eerste_kleur", "N/A"));
            holder.textViewBouwjaar.setText("Bouwjaar: " + formatDate(jsonObject.optString("datum_eerste_toelating", "N/A")));
            holder.textViewNieuwprijs.setText("Nieuwprijs: " + (jsonObject.optInt("catalogusprijs", 0) == 0 ? "N/A" : "€" + jsonObject.getInt("catalogusprijs")));

            // Set Dimensions Info
            holder.textViewVoertuigLengte.setText("Voertuig Lengte: " + jsonObject.optString("lengte", "N/A") + "cm");
            holder.textViewVoertuigBreedte.setText("Voertuig Breedte: " + jsonObject.optString("breedte", "N/A") + "cm");
            holder.textViewVoertuigHoogte.setText("Voertuig Hoogte: " + jsonObject.optString("hoogte_voertuig", "N/A") + "cm");

            // Set Mass Info
            holder.textViewMassaRijklaar.setText("Massa Rijklaar: " + jsonObject.optString("massa_rijklaar", "N/A") + "kg");
            holder.textViewToegestaneMaxMassaVoertuig.setText("Toegestane max. massa voertuig: " + jsonObject.optString("toegestane_maximum_massa_voertuig", "N/A") + "kg");
            holder.textViewMaximaleLaadvermogen.setText("Maximale laadvermogen: " + jsonObject.optString("massa_ledig_voertuig", "N/A") + "kg");

            // Set Towing Info
            holder.textViewAanhangerOngeremd.setText("Aanhanger ongeremd: " + jsonObject.optString("maximum_massa_trekken_ongeremd", "N/A") + "kg");
            holder.textViewAanhangerGeremd.setText("Aanhanger geremd: " + jsonObject.optString("maximum_trekken_massa_geremd", "N/A") + "kg");

            // Set APK and Legal Info
            holder.textViewVervaldatumAPK.setText("Vervaldatum APK: " + formatDate(jsonObject.optString("vervaldatum_apk", "N/A")));
            holder.textViewWokMelding.setText("WOK melding: " + jsonObject.optString("wacht_op_keuren", "N/A"));
            holder.textViewDatumEersteToelating.setText("Datum eerste toelating: " + formatDate(jsonObject.optString("datum_eerste_toelating", "N/A")));
            holder.textViewDatumEersteTenaamstellingInNederland.setText("Datum eerste tenaamstelling in Nederland: " + formatDate(jsonObject.optString("datum_eerste_tenaamstelling_in_nederland", "N/A")));
            holder.textViewBrutoBPM.setText("Bruto BPM: €" + jsonObject.optString("bruto_bpm", "N/A"));

            // Set Other Info
            holder.textViewInrichting.setText("Inrichting: " + jsonObject.optString("inrichting", "N/A"));
            holder.textViewAantalZitplaatsen.setText("Aantal zitplaatsen: " + jsonObject.optString("aantal_zitplaatsen", "N/A"));
            holder.textViewWAMVerzekerd.setText("WAM verzekerd: " + jsonObject.optString("wam_verzekerd", "N/A"));
            holder.textViewAantalDeuren.setText("Aantal deuren: " + jsonObject.optString("aantal_deuren", "N/A"));
            holder.textViewAantalWielen.setText("Aantal wielen: " + jsonObject.optString("aantal_wielen", "N/A"));
            holder.textViewPlaatsChassisnummer.setText("Plaats chassisnummer: " + jsonObject.optString("plaats_chassisnummer", "N/A"));
            holder.textViewZuinigheidsclassificatie.setText("Zuinigheidsclassificatie: " + jsonObject.optString("zuinigheidsclassificatie", "N/A"));
            holder.textViewAantalCilinders.setText("Aantal cilinders: " + jsonObject.optString("aantal_cilinders", "N/A"));
            holder.textViewCilinderinhoud.setText("Cilinderinhoud: " + jsonObject.optString("cilinderinhoud", "N/A") + "cc");
            holder.textViewTellerstandoordeel.setText("Tellerstand oordeel: " + jsonObject.optString("tellerstandoordeel", "N/A"));
            holder.textViewTenaamstellenMogelijk.setText("Tenaamstellen mogelijk: " + jsonObject.optString("tenaamstellen_mogelijk", "N/A"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return results.length();
    }

    // Return the item at the specified position
    public JSONObject getItem(int position) {
        try {
            return results.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Format date from yyyyMMdd to dd-MM-yyyy
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

    // Generate summary from the JSON object data for the vehicle info
    private String generateSummary(JSONObject jsonObject) throws JSONException {
        String nieuwprijs = jsonObject.optInt("catalogusprijs", 0) == 0 ? "N/A" : "€" + jsonObject.getInt("catalogusprijs");
        String snelheid = jsonObject.optString("maximale_constructiesnelheid", "N/A");
        String cillinderinhoud = jsonObject.optString("cilinderinhoud", "N/A");
        String zuinigheidslabel = jsonObject.optString("zuinigheidsclassificatie", "N/A");
        String merk = jsonObject.optString("merk", "N/A");
        String inrichring = jsonObject.optString("inrichting", "N/A");
        String eerstekleur = jsonObject.optString("eerste_kleur", "N/A");
        String brutoBPM = jsonObject.optString("bruto_bpm", "N/A");

        String datumEersteToelating = formatDate(jsonObject.optString("datum_eerste_toelating", "N/A"));
        String datumEersteTenaamstellingInNederland = formatDate(jsonObject.optString("datum_eerste_tenaamstelling_in_nederland", "N/A"));

        String dutchStatus = datumEersteToelating.equals(datumEersteTenaamstellingInNederland) ?
                "een origineel Nederlandse auto." :
                "geïmporteerd op " + datumEersteTenaamstellingInNederland + ".";

        return "Deze " + merk + " had destijds een nieuwprijs vanaf " + nieuwprijs + " en is " + dutchStatus +
                " De auto heeft een topsnelheid van " + snelheid + " km/h met een cilinderinhoud van " + cillinderinhoud + "cc. " +
                "Deze " + inrichring + " is " + eerstekleur + " van kleur en heeft een bruto BPM van €" + brutoBPM + ". " +
                "De " + merk + " heeft een zuinigheidslabel " + zuinigheidslabel + ".";
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewSummary, textViewKenteken, textViewMerk, textViewModel, textViewKleur, textViewBouwjaar, textViewNieuwprijs,
                textViewVoertuigLengte, textViewVoertuigBreedte, textViewVoertuigHoogte, textViewMassaRijklaar, textViewToegestaneMaxMassaVoertuig,
                textViewMaximaleLaadvermogen, textViewAanhangerOngeremd, textViewAanhangerGeremd, textViewVervaldatumAPK, textViewWokMelding,
                textViewDatumEersteToelating, textViewDatumEersteTenaamstellingInNederland, textViewBrutoBPM, textViewInrichting, textViewAantalZitplaatsen,
                textViewWAMVerzekerd, textViewAantalDeuren, textViewAantalWielen, textViewPlaatsChassisnummer, textViewZuinigheidsclassificatie,
                textViewAantalCilinders, textViewCilinderinhoud, textViewTellerstandoordeel, textViewTenaamstellenMogelijk, textViewTitle, textViewType;

        public ViewHolder(View view) {
            super(view);
            textViewTitle = view.findViewById(R.id.textViewTitle);
            textViewType = view.findViewById(R.id.textViewType);
            textViewSummary = view.findViewById(R.id.textViewSummary);
            textViewKenteken = view.findViewById(R.id.textViewKenteken);
            textViewMerk = view.findViewById(R.id.textViewMerk);
            textViewModel = view.findViewById(R.id.textViewModel);
            textViewKleur = view.findViewById(R.id.textViewKleur);
            textViewBouwjaar = view.findViewById(R.id.textViewBouwjaar);
            textViewNieuwprijs = view.findViewById(R.id.textViewNieuwprijs);
            textViewVoertuigLengte = view.findViewById(R.id.textViewVoertuigLengte);
            textViewVoertuigBreedte = view.findViewById(R.id.textViewVoertuigBreedte);
            textViewVoertuigHoogte = view.findViewById(R.id.textViewVoertuigHoogte);
            textViewMassaRijklaar = view.findViewById(R.id.textViewMassaRijklaar);
            textViewToegestaneMaxMassaVoertuig = view.findViewById(R.id.textViewToegestaneMaxMassaVoertuig);
            textViewMaximaleLaadvermogen = view.findViewById(R.id.textViewMaximaleLaadvermogen);
            textViewAanhangerOngeremd = view.findViewById(R.id.textViewAanhangerOngeremd);
            textViewAanhangerGeremd = view.findViewById(R.id.textViewAanhangerGeremd);
            textViewVervaldatumAPK = view.findViewById(R.id.textViewVervaldatumAPK);
            textViewWokMelding = view.findViewById(R.id.textViewWokMelding);
            textViewDatumEersteToelating = view.findViewById(R.id.textViewDatumEersteToelating);
            textViewDatumEersteTenaamstellingInNederland = view.findViewById(R.id.textViewDatumEersteTenaamstellingInNederland);
            textViewBrutoBPM = view.findViewById(R.id.textViewBrutoBPM);
            textViewInrichting = view.findViewById(R.id.textViewInrichting);
            textViewAantalZitplaatsen = view.findViewById(R.id.textViewAantalZitplaatsen);
            textViewWAMVerzekerd = view.findViewById(R.id.textViewWAMVerzekerd);
            textViewAantalDeuren = view.findViewById(R.id.textViewAantalDeuren);
            textViewAantalWielen = view.findViewById(R.id.textViewAantalWielen);
            textViewPlaatsChassisnummer = view.findViewById(R.id.textViewPlaatsChassisnummer);
            textViewZuinigheidsclassificatie = view.findViewById(R.id.textViewZuinigheidsclassificatie);
            textViewAantalCilinders = view.findViewById(R.id.textViewAantalCilinders);
            textViewCilinderinhoud = view.findViewById(R.id.textViewCilinderinhoud);
            textViewTellerstandoordeel = view.findViewById(R.id.textViewTellerstandoordeel);
            textViewTenaamstellenMogelijk = view.findViewById(R.id.textViewTenaamstellenMogelijk);
        }
    }
}
