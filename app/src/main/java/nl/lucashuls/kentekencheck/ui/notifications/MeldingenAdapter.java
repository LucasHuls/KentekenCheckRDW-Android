package nl.lucashuls.kentekencheck.ui.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import nl.lucashuls.kentekencheck.R;
import nl.lucashuls.kentekencheck.data.entities.Kenteken;

public class MeldingenAdapter extends RecyclerView.Adapter<MeldingenAdapter.MeldingenViewHolder> {

    private List<Kenteken> kentekens;

    @NonNull
    @Override
    public MeldingenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meldingen, parent, false);
        return new MeldingenViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MeldingenViewHolder holder, int position) {
        if (kentekens != null) {
            Kenteken current = kentekens.get(position);
            if (current.tag.isEmpty()) {
                holder.kentekenItemView.setText(current.kenteken);
            } else {
                holder.kentekenItemView.setText(current.kenteken + " - " + current.tag);
            }

            long daysLeft = calculateDaysLeft(current.vervaldatum_apk_dt);
            holder.daysLeftItemView.setText("APK verloopt over " + daysLeft + " dagen");

            // Load the logo with error handling
            String logoUrl = current.logoUrl;
            Glide.with(holder.logoView.getContext())
                    .load(logoUrl)
                    .error(R.drawable.sharp_car_crash_24) // Placeholder image in case of an error
                    .into(holder.logoView);

            long ownedDays = calculateDaysOwned(current.AanschafDate);
            holder.ownedDaysItemView.setText("Kenteken in je bezit voor " + ownedDays + " dagen");
        }
    }


    // Calculate the days between the current date and the date the car was bought
    private long calculateDaysOwned(String AanschafDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(AanschafDate);
            assert date != null;
            long diffInMillies = System.currentTimeMillis() - date.getTime();
            return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Calculate the days between the current date and the APK expiration date
    private long calculateDaysLeft(String vervaldatum_apk_dt) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        try {
            Date date = sdf.parse(vervaldatum_apk_dt);
            assert date != null;
            long diffInMillies = date.getTime() - System.currentTimeMillis();
            return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Set the kentekens and notify the adapter
    void setKentekens(List<Kenteken> kentekens) {
        this.kentekens = kentekens;
        notifyDataSetChanged();
    }

    // Get the amount of kentekens in the list
    @Override
    public int getItemCount() {
        if (kentekens != null)
            return kentekens.size();
        else return 0;
    }

    static class MeldingenViewHolder extends RecyclerView.ViewHolder {
        private final TextView kentekenItemView;
        private final TextView daysLeftItemView;
        private final ImageView logoView;
        private final TextView ownedDaysItemView;

        private MeldingenViewHolder(View itemView) {
            super(itemView);
            kentekenItemView = itemView.findViewById(R.id.textViewKenteken);
            daysLeftItemView = itemView.findViewById(R.id.textViewDaysLeft);
            logoView = itemView.findViewById(R.id.imageViewLogo);
            ownedDaysItemView = itemView.findViewById(R.id.textViewOwnedDays);
        }
    }
}
