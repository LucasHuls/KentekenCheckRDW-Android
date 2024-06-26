package nl.lucashuls.kentekencheck.utils;

import android.view.View;
import android.view.ViewGroup;

import nl.lucashuls.kentekencheck.R;

public class LoadingSpinnerUtil {

    // Show a loading spinner on top of the root layout
    public static void showSpinner(ViewGroup rootLayout) {
        View spinner = rootLayout.findViewById(R.id.fullscreen_spinner);
        if (spinner != null) {
            spinner.setVisibility(View.VISIBLE);
        }
    }

    // Hide the loading spinner on top of the root layout
    public static void hideSpinner(ViewGroup rootLayout) {
        View spinner = rootLayout.findViewById(R.id.fullscreen_spinner);
        if (spinner != null) {
            spinner.setVisibility(View.GONE);
        }
    }
}
