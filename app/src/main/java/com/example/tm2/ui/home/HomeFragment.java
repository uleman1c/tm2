package com.example.tm2.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.tm2.DB;
import com.example.tm2.R;
import com.example.tm2.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private String appId, id;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        Bundle bundle = getArguments();

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        DB db = new DB(getContext());
        db.open();
        appId = db.getConstant("appId");
        db.close();

        if (bundle != null){

            id = bundle.getString("id");
            binding.tvFio.setText(bundle.getString("name"));

            bundle.putString("appId", appId);
        }

        binding.btnVisits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);

                navController.navigate(R.id.visitListFragment, bundle);


            }
        });

//        binding.tvId.setText(appId);
//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}