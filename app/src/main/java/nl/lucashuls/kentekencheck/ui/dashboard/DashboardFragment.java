package nl.lucashuls.kentekencheck.ui.dashboard;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import nl.lucashuls.kentekencheck.R;
import nl.lucashuls.kentekencheck.ui.latestkenteken.LatestKentekensActivity;
import nl.lucashuls.kentekencheck.ui.result.ResultActivity;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private EditText editTextKenteken;
    private boolean resultHandled;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        editTextKenteken = root.findViewById(R.id.edit_text_kenteken);
        Button buttonSearch = root.findViewById(R.id.button_search);
        ImageButton buttonPaste = root.findViewById(R.id.button_paste);
        Button buttonLatestKentekens = root.findViewById(R.id.button_latest_kentekens);

        // Get text from clipboard and paste it into the editText
        buttonPaste.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null && clipboard.getPrimaryClip() != null) {
                ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                if (item != null) {
                    editTextKenteken.setText(item.getText().toString());
                }
            }
        });

        // Search for the kenteken based on the input
        buttonSearch.setOnClickListener(v -> {
            String kenteken = editTextKenteken.getText().toString();
            if (kenteken.isEmpty()) {
                editTextKenteken.setError(getString(R.string.kenteken_cannot_be_empty));
                return;
            }
            kenteken = kenteken.replace("-", "").toUpperCase();
            resultHandled = false;
            dashboardViewModel.searchKenteken(kenteken);
        });

        // Open the latest kentekens activity
        buttonLatestKentekens.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LatestKentekensActivity.class);
            startActivity(intent);
        });

        // Opens the result activity when the result is received
        dashboardViewModel.getResult().observe(getViewLifecycleOwner(), result -> {
            if (!resultHandled) {
                resultHandled = true;
                if (result.startsWith("Error")) {
                    Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getActivity(), ResultActivity.class);
                    intent.putExtra("resultData", result);
                    startActivity(intent);
                }
            }
        });

        return root;
    }
}
