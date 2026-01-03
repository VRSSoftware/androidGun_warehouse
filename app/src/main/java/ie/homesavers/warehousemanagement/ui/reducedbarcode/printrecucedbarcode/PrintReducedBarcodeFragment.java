package ie.homesavers.warehousemanagement.ui.reducedbarcode.printrecucedbarcode;


import androidx.appcompat.widget.AppCompatTextView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;
//import ie.homesavers.warehousemanagement.R;
import ie.homesavers.warehousemanagement.R;
import ie.homesavers.warehousemanagement.ui.dialog.warehouse.PrinterSizeListener;
import ie.homesavers.warehousemanagement.ui.util.Constants;
import ie.homesavers.warehousemanagement.ui.util.IBackPageMovement;
import ie.homesavers.warehousemanagement.ui.util.ListenerZebraPrinter;
import ie.homesavers.warehousemanagement.utils.AppClassConstant;
import ie.homesavers.warehousemanagement.utils.AppPreference;
import ie.homesavers.warehousemanagement.utils.AppSetting;
import ie.homesavers.warehousemanagement.webservices.reducedbarcode.SaveReducedBarcodeModel;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.PrinterLanguage;
import com.zebra.sdk.printer.SGD;
import com.zebra.sdk.printer.ZebraPrinter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
public class PrintReducedBarcodeFragment extends Fragment implements
        View.OnClickListener,
        ListenerZebraPrinter,
        PrinterSizeListener,
        INewReducedBarcode,
        IBackPageMovement

{
    public static final String TAG="Print Tag Prince";
    Connection mConnection=null;
    ZebraPrinter mZebraPrinter=null;
    ReducedBarPrinterTask mZebraPrinterTask=null;
    AppCompatTextView appCompatTextViewSelectPrinterSize;

    private ProgressDialog progressDialog;

    ArrayList listPrintSizes=new ArrayList();
    public ArrayList<SaveReducedBarcodeModel> listPrintPriceTag=new ArrayList<>();
    NewSaveReducedAdapter adapter;
    List<SaveReducedBarcodeModel> saveReducedBarcodeModels;
    RecyclerView listReducedBarcode;
    NavController navController;
   // private PrintReducedBarcodeViewModel mViewModel;

    public static PrintReducedBarcodeFragment newInstance() {
        return new PrintReducedBarcodeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.print_reduced_barcode_fragment, container, false);


        listPrintSizes.add(Constants.PRINTER_SIZES_SMALL);
        saveReducedBarcodeModels=AppPreference.getNewReducedStockDataPreferences(getContext());
        listReducedBarcode= view.findViewById(R.id.list_item_reduced_barcode);

        UpdateGeneralList(saveReducedBarcodeModels);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//    mViewModel = new ViewModelProvider(this).get(PrintReducedBarcodeViewModel.class);
// TODO: Use the ViewModel...................................................................
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
    }

    private void UpdateGeneralList(List<SaveReducedBarcodeModel> saveReducedBarcodeModels) {
        if (saveReducedBarcodeModels!=null) {
            adapter = new NewSaveReducedAdapter(saveReducedBarcodeModels,this);
            listReducedBarcode.setAdapter(adapter);
            listReducedBarcode.setLayoutManager(new LinearLayoutManager(getActivity()));
        }

    }
    @Override
    public void onClick(View v) {
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void initZPConnection(String BLUETOOTH_MAC_ADDRESS) {
        mZebraPrinterTask = new ReducedBarPrinterTask(this, BLUETOOTH_MAC_ADDRESS);
        mZebraPrinterTask.initializeZPConnection(BLUETOOTH_MAC_ADDRESS);
    }

    @Override
    public void onZPConnection(Connection connection) {
        mConnection=connection;
        if(mConnection!=null){
            initZebraPrinterConnection(connection);
        }
    }

    @Override
    public void initZebraPrinterConnection(Connection connection) {
        mZebraPrinterTask.initializeZebraPrinterConnection(connection);
    }

    @Override
    public void onZebraPrinterConnection(ZebraPrinter zebraPrinter) {
        mZebraPrinter=zebraPrinter;
    }

    private void sendPrint(String textData) {
        try {
            if(mZebraPrinter!=null) {
                boolean isReady = mZebraPrinterTask.checkPrinterStatus(mZebraPrinter);

                if (isReady) {
                    Log.i(TAG, "Printer the Data");
                    PrinterLanguage printerLanguage = mZebraPrinter.getPrinterControlLanguage();
//                    Toast.makeText(getContext(),printerLanguage.name(),Toast.LENGTH_LONG).show();
//                    PrinterNameTemp.setText(printerLanguage.name());
                    Log.i("Printer Language",printerLanguage.name());
                    SGD.SET("device.languages", "zpl", mConnection);

                    mConnection.write(textData.getBytes());

//                connection.write(createZplProductPrint().getBytes());
//                sendZplReceipt(connection);
//                String[] cars = {"Volvo", "BMW", "Ford", "Mazda"};
//                zebraPrinter.printStoredFormat("Volvo",cars);

                } else {
                    if(mZebraPrinter!=null){
                        mZebraPrinterTask.showPrinterStatus(getContext(), mZebraPrinter);
                    }
                }
            }else{
                Log.i(TAG,"Printer not Connected");
                Toast.makeText(getActivity().getApplicationContext(),"Check Printer Settings",Toast.LENGTH_LONG).show();
            }
        } catch (ConnectionException e) {
            e.printStackTrace();
            Log.i(TAG,this.getString(R.string.print_failed) + " " + this.getString(R.string.no_printer));
        } finally {
            try {
                if(mConnection!=null){
                    mConnection.close();}
            } catch (ConnectionException e) {
                e.printStackTrace();
            }
        }
    }

    public void onPrintTagClicked(SaveReducedBarcodeModel saveReducedBarcodeModel) {
        AppSetting appSetting=AppPreference.getSettingDataPreferences(getContext());
        String testMacAdd=String.valueOf(appSetting.getSettingPrinterMACAdd());
        if (testMacAdd!=null){
//        if(!appSetting.getSettingPrinterMACAdd().isEmpty()){
            if (!hasBluetoothPermission()) {
                requestBluetoothPermission();
                Toast.makeText(
                        getContext(),
                        "Bluetooth permission required to print",
                        Toast.LENGTH_SHORT
                ).show();
                return; // VERY IMPORTANT
            }

            initZPConnection(appSetting.getSettingPrinterMACAdd());
            String printLabel=null;
            if(mZebraPrinter!=null) {
                try {
                    mZebraPrinter.sendCommand("! U1 set var \"device.languages\" \"zpl\"\r\n");
                    Thread.sleep(0);
                } catch (ConnectionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (saveReducedBarcodeModels!=null) {
                    printLabel = mZebraPrinterTask.getPrintVerticalZPLLabel(saveReducedBarcodeModel);
                }
                if (printLabel != null) {
                    sendPrint(printLabel);
                  //  saveReducedBarcodeModels.clear();
                }
            }
        }else{
            Toast.makeText(getContext(),"Set Printer Address",Toast.LENGTH_LONG).show();
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClicked(String labelSize) {
        appCompatTextViewSelectPrinterSize.setText(labelSize);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onNewReducedBarcode(SaveReducedBarcodeModel saveReducedBarcodeModel) {

        if (saveReducedBarcodeModels.size()>0){
            //for(int i=0;i<Integer.parseInt(saveReducedBarcodeModels.get(0).getNoOfstickerstoprint());i++){
            onPrintTagClicked(saveReducedBarcodeModels.get(0));//}
            onButtonPrintReducedBarcode();
          //  saveReducedBarcodeModels.clear();
        }

    }

    @Override
    public void onBackClicked(View view) { }

    public ProgressDialog createProgressDialog(Context mContext) {
        ProgressDialog dialog = new ProgressDialog(mContext);
        try {
            dialog.show();
        } catch (WindowManager.BadTokenException e) {

        }
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.blue(100)));
        dialog.setContentView(R.layout.dialog_progress_layout);
        return dialog;
    }

    public void onButtonPrintReducedBarcode() {

        navController.navigate(R.id.action_printReducedBarcodeFragment_to_nav_reduce_barcode);
//        if(AppClassConstant.classPresent()){
//            navController.navigate(R.id.action_printReducedBarcodeFragment_to_saveReducedBarcodeFragment);
//        }else{
//            navController.navigate(R.id.action_printReducedBarcodeFragment_to_saveReducedBarcodeFragmentAnd);
//        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 999) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(
                        getContext(),
                        "Permission granted. Press Print again.",
                        Toast.LENGTH_SHORT
                ).show();
            } else {
                Toast.makeText(
                        getContext(),
                        "Bluetooth permission denied",
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }
    private boolean hasBluetoothPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return true;
        }

        return ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.BLUETOOTH_SCAN
        ) == PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissions(
                    new String[]{
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_CONNECT
                    },
                    999
            );
        }
    }


}
