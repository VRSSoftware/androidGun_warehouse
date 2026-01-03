package ie.homesavers.warehousemanagement.ui.logout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import ie.homesavers.warehousemanagement.MainActivity;
//import ie.homesavers.warehousemanagement.R;
import ie.homesavers.warehousemanagement.R;
import ie.homesavers.warehousemanagement.utils.AppPreference;

public class LogoutFragment extends Fragment {



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_logout, container, false);
        final TextView textView = root.findViewById(R.id.text_logout);
        AppPreference.clearLoginDataPreferences(getContext());
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent=new Intent(getActivity(),MainActivity.class);
        startActivity(intent);
        getActivity().finish();

    }
}