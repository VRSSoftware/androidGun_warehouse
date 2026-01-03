package ie.homesavers.warehousemanagement.ui.stocktransfer;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import ie.homesavers.warehousemanagement.R;
import ie.homesavers.warehousemanagement.R;

public class PickHeldStockTransferFragment extends Fragment {



    public static PickHeldStockTransferFragment newInstance() {
        return new PickHeldStockTransferFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pick_held_stock_transfer_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: Use the ViewModel
    }

}