package nl.lucashuls.kentekencheck.ui.latestkenteken;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import nl.lucashuls.kentekencheck.R;
import nl.lucashuls.kentekencheck.ui.result.ResultActivity;
import nl.lucashuls.kentekencheck.utils.CarImagesLoaderUtils;

public class LatestKentekensAdapter extends RecyclerView.Adapter<LatestKentekensAdapter.LatestKentekenViewHolder> {

    private final JSONArray latestKentekens;
    private final Context context;

    LatestKentekensAdapter(JSONArray latestKentekens, Context context) {
        this.latestKentekens = latestKentekens;
        this.context = context;
    }

    @NonNull
    @Override
    public LatestKentekenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kenteken, parent, false);
        return new LatestKentekenViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LatestKentekenViewHolder holder, int position) {
        try {
            JSONObject current = latestKentekens.getJSONObject(position);
            String kentekenDisplay = current.optString("kenteken", "N/A");
            holder.kentekenItemView.setText(kentekenDisplay);
            holder.merkItemView.setText(current.optString("merk", "N/A"));
            holder.modelItemView.setText(current.optString("handelsbenaming", "N/A"));

            CarImagesLoaderUtils.loadLogo(context, current.optString("merk", "N/A"), holder.logoView);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ResultActivity.class);
                intent.putExtra("resultData", current.toString());
                context.startActivity(intent);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return latestKentekens.length();
    }

    static class LatestKentekenViewHolder extends RecyclerView.ViewHolder {
        private final TextView kentekenItemView;
        private final TextView merkItemView;
        private final TextView modelItemView;
        private final ImageView logoView;

        private LatestKentekenViewHolder(View itemView) {
            super(itemView);
            kentekenItemView = itemView.findViewById(R.id.textViewKenteken);
            merkItemView = itemView.findViewById(R.id.textViewMerk);
            modelItemView = itemView.findViewById(R.id.textViewModel);
            logoView = itemView.findViewById(R.id.imageViewLogo);
        }
    }
}
