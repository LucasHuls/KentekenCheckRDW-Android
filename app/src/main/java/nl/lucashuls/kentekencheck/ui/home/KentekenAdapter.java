package nl.lucashuls.kentekencheck.ui.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;

import java.util.List;

import nl.lucashuls.kentekencheck.R;
import nl.lucashuls.kentekencheck.data.entities.Kenteken;
import nl.lucashuls.kentekencheck.ui.result.ResultActivity;
import nl.lucashuls.kentekencheck.utils.RDWApiUtils;

public class KentekenAdapter extends RecyclerView.Adapter<KentekenAdapter.KentekenViewHolder> {

    private final LayoutInflater inflater;
    private List<Kenteken> kentekens;

    KentekenAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public KentekenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_kenteken, parent, false);
        return new KentekenViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull KentekenViewHolder holder, int position) {
        if (kentekens != null) {
            Kenteken current = kentekens.get(position);
            String kentekenDisplay = current.kenteken;
            // Add tag to kenteken if it exists and is not empty (e.g. "AB-12-CD - Tag")
            if (current.tag != null && !current.tag.isEmpty()) {
                kentekenDisplay += " - " + current.tag;
            }
            holder.kentekenItemView.setText(kentekenDisplay);
            holder.merkItemView.setText(current.merk);
            holder.modelItemView.setText(current.model);

            String logoUrl = current.logoUrl;
            Glide.with(holder.logoView.getContext())
                    .load(logoUrl)
                    .error(R.drawable.sharp_car_crash_24)
                    .into(holder.logoView);
            holder.itemView.setOnClickListener(v -> {
                Context context = holder.itemView.getContext();

                // Fetch data from RDW API and start ResultActivity with the data if successful
                RDWApiUtils.fetchKentekenData(context, current.kenteken, new RDWApiUtils.RDWApiCallback() {
                    @Override
                    public void onSuccess(JSONArray data) {
                        if (data.length() == 0) {
                            Toast.makeText(context, "No data found for the given kenteken", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(context, ResultActivity.class);
                            intent.putExtra("resultData", data.toString());
                            context.startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }
    }


    // Update the list of kentekens and notify the adapter
    void setKentekens(List<Kenteken> kentekens) {
        this.kentekens = kentekens;
        notifyDataSetChanged();
    }

    // Get the number of kentekens in the list
    @Override
    public int getItemCount() {
        if (kentekens != null)
            return kentekens.size();
        else return 0;
    }

    static class KentekenViewHolder extends RecyclerView.ViewHolder {
        private final TextView kentekenItemView;
        private final TextView merkItemView;
        private final TextView modelItemView;
        private final ImageView logoView;

        private KentekenViewHolder(View itemView) {
            super(itemView);
            kentekenItemView = itemView.findViewById(R.id.textViewKenteken);
            merkItemView = itemView.findViewById(R.id.textViewMerk);
            modelItemView = itemView.findViewById(R.id.textViewModel);
            logoView = itemView.findViewById(R.id.imageViewLogo);
        }
    }
}
