package nl.lucashuls.kentekencheck.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import nl.lucashuls.kentekencheck.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private KentekenAdapter kentekenAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Set up the RecyclerView and the Adapter
        RecyclerView recyclerView = binding.recyclerViewKentekens;
        TextView emptyView = binding.emptyView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        kentekenAdapter = new KentekenAdapter(getContext());
        recyclerView.setAdapter(kentekenAdapter);

        // Observe the LiveData from the ViewModel and display the data in the RecyclerView
        homeViewModel.getAllKentekens().observe(getViewLifecycleOwner(), kentekens -> {
            kentekenAdapter.setKentekens(kentekens);
            if (kentekens.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}