package ie.homesavers.warehousemanagement.ui.printpricetag;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import ie.homesavers.warehousemanagement.R;
import ie.homesavers.warehousemanagement.R;
import ie.homesavers.warehousemanagement.ui.dialog.warehouse.PrinterSizeDialog;
import ie.homesavers.warehousemanagement.ui.dialog.warehouse.PrinterSizeListener;
import ie.homesavers.warehousemanagement.webservices.check_stock.CheckStock;
import ie.homesavers.warehousemanagement.webservices.login.UserModel;
import ie.homesavers.warehousemanagement.ui.util.Constants;
import ie.homesavers.warehousemanagement.ui.util.ListenerZebraPrinter;
import ie.homesavers.warehousemanagement.ui.util.ZebraPrinterTask;
import ie.homesavers.warehousemanagement.utils.AppPreference;
import ie.homesavers.warehousemanagement.utils.AppSetting;
import ie.homesavers.warehousemanagement.webservices.WebServiceClient;
import ie.homesavers.warehousemanagement.webservices.check_stock.PriceListModel;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.PrinterLanguage;
import com.zebra.sdk.printer.SGD;
import com.zebra.sdk.printer.ZebraPrinter;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrintTagFragmentAnd extends Fragment implements
        View.OnClickListener,
        ListenerZebraPrinter,
        PrinterSizeListener
{

    public static final String TAG="Print Tag Prince";
    private AppCompatEditText appCompatEditTextBarcode;
    private AppCompatButton appCompatButtonOK;
    private AppCompatButton appCompatButtonPPT;

    private RecyclerView rvItemPrintPriceTag;
    public ArrayList<PriceListModel> listPrintPriceTag=new ArrayList<>();
    private CheckStock checkStock=new CheckStock();
    private PrintPriceAdapter printPriceAdapter;
    private ProgressDialog progressDialog;

    Connection mConnection=null;
    ZebraPrinter mZebraPrinter=null;
    ZebraPrinterTask mZebraPrinterTask=null;
    PrinterSizeDialog printerSizeDialog;
    AppCompatTextView appCompatTextViewSelectPrinterSize;

    //private int scannerIndex = 0; // Keep the selected scanner
    private int defaultIndex = 0; // Keep the default scanner
    private int dataLength = 0;
    private String statusString = "";
    private boolean bSoftTriggerSelected = false;
    private boolean bDecoderSettingsChanged = false;
    private boolean bExtScannerDisconnected = false;
    private final Object lock = new Object();
    private String TAG_SCANNER="SCANNER Check Stock";
    private ArrayList listPrintSizes=new ArrayList();
    private AppCompatCheckBox compatCheckBoxEnterManually;
    private String labelSize;
    public static PrintTagFragmentAnd newInstance() {
        return new PrintTagFragmentAnd();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.print_tag_fragment, container, false);

        appCompatEditTextBarcode=view.findViewById(R.id.edit_print_price_tag_barcode);
        appCompatEditTextBarcode.setEnabled(true);
        appCompatTextViewSelectPrinterSize=view.findViewById(R.id.select_printer_sizes);
        appCompatTextViewSelectPrinterSize.setOnClickListener(this);

        appCompatButtonOK=view.findViewById(R.id.button_print_price_tag_ok);
        appCompatButtonOK.setOnClickListener(this);

        appCompatButtonPPT=view.findViewById(R.id.button_print_price_tag_PPT);
        appCompatButtonPPT.setOnClickListener(this);

        appCompatEditTextBarcode.requestFocus();

        compatCheckBoxEnterManually = view.findViewById(R.id.checkbox_labelEnter_Manually);
        compatCheckBoxEnterManually.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    appCompatEditTextBarcode.setEnabled(true);
                }
                else{
                    appCompatEditTextBarcode.setEnabled(false);
                }
            }
        });

        rvItemPrintPriceTag=view.findViewById(R.id.rv_item_print_price_tag);

        listPrintSizes.add(Constants.PRINTER_SIZES_SMALL);
        listPrintSizes.add(Constants.PRINTER_SIZES_LARGE);
        listPrintSizes.add(Constants.PRINTER_SIZE_LARGE_OFFER);
        listPrintSizes.add(Constants.PRINTER_SIZE_GARDENING_SEL);
        listPrintSizes.add(Constants.PRINTER_SIZES_SMALL_WAS_NOW);
        listPrintSizes.add(Constants.PRINTER_SIZES_LARGE_WAS_NOW);
        listPrintSizes.add(Constants.PRINTER_SIZES_WHOLESALE_LARGE);
        listPrintSizes.add(Constants.PRINTER_SIZES_WHOLESALE_SMALL);

        onItemClicked(listPrintSizes.get(0).toString());
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.button_print_price_tag_ok){
            if (!TextUtils.isEmpty(appCompatTextViewSelectPrinterSize.getText()))
            {
                if(!TextUtils.isEmpty(appCompatEditTextBarcode.getText())
                ) {
                    SearchPrintPriceTag();
                }else{
                    Toast.makeText(getActivity(),"Please Scan the Barcode",Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getActivity(),"Please Select Print Tag Report",Toast.LENGTH_SHORT).show();
            }
        }
        if(v.getId()==R.id.button_print_price_tag_PPT){
            if(listPrintPriceTag.size()>0){
                onPrintTagClicked(listPrintPriceTag.get(0));
                listPrintPriceTag.clear();
                printPriceAdapter.notifyDataSetChanged();
            }
        }
        if(v.getId()==R.id.select_printer_sizes){
            openSelectPrinterDialog();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    public void SearchPrintPriceTag(){
        progressDialog = createProgressDialog(getContext());
        UserModel userModel= AppPreference.getLoginDataPreferences(getContext());
        AppSetting appSetting=AppPreference.getSettingDataPreferences(getContext());
        checkStock.setBarcode(appCompatEditTextBarcode.getText().toString());
        checkStock.setCoBr_Id(userModel.getCobrId());

        Call<ArrayList<PriceListModel>> listCheckStock  =
                WebServiceClient
                        .checkStockService(appSetting.getSettingServerURL())
                        .listPriceList(checkStock);
        listCheckStock.enqueue(new Callback<ArrayList<PriceListModel>>() {
            @Override
            public void onResponse(Call<ArrayList<PriceListModel>> call,
                                   Response<ArrayList<PriceListModel>> response) {

                if (response.isSuccessful()) {
                    PriceListModel priceListModel = response.body().get(0);
                    String ss ="Print Price Tag 176mm*75mm For Big Offer Sel";
                    if (labelSize == ss) {
                        if (priceListModel.getUdWeekImported() != null
                                && !priceListModel.getUdWeekImported().isEmpty()) {
                            changeDataSet(response.body());
                        } else {
                            Toast.makeText(getContext(), "Offer not found in Item !", Toast.LENGTH_LONG).show();
                        }
                    }else {
                        changeDataSet(response.body());
                    }
                    progressDialog.dismiss();
                    appCompatEditTextBarcode.setText("");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<PriceListModel>> call, Throwable t) {
                Log.e("Error",t.getMessage());
                progressDialog.dismiss();
            }
        });
    }

    public void changeDataSet(ArrayList<PriceListModel> checkStockModels){
        listPrintPriceTag=checkStockModels;
        if(listPrintPriceTag.get(0).getItemId()!=null){
            printPriceAdapter=new PrintPriceAdapter(listPrintPriceTag,labelSize,getContext());
            rvItemPrintPriceTag.setAdapter(printPriceAdapter);
            rvItemPrintPriceTag.setLayoutManager(new LinearLayoutManager(getContext()));
            appCompatEditTextBarcode.setText("");
        }
        else {
            Toast.makeText(getContext(),"Item not found !",Toast.LENGTH_LONG).show();
        }
    }

//--------------------------------------------------------------------------------------------------

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

    //--------------------------------------------------------------------------------------------------
    @Override
    public void initZPConnection(String BLUETOOTH_MAC_ADDRESS) {
        if (BLUETOOTH_MAC_ADDRESS!=null || BLUETOOTH_MAC_ADDRESS!="") {
            mZebraPrinterTask =new ZebraPrinterTask(this,BLUETOOTH_MAC_ADDRESS);
            mZebraPrinterTask.initializeZPConnection(BLUETOOTH_MAC_ADDRESS);
        }else{
            Toast.makeText(getContext(),"Set Printer Address",Toast.LENGTH_LONG).show();
        }
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
                    Log.i("Printer Language",printerLanguage.name());
                    SGD.SET("device.languages", "zpl", mConnection);

                    mConnection.write(textData.getBytes());

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

    public void onPrintTagClicked(PriceListModel checkStockModel) {
        AppSetting appSetting=AppPreference.getSettingDataPreferences(getContext());
        String getMacAdd= String.valueOf(appSetting.getSettingPrinterMACAdd());
        double discount = Double.parseDouble(checkStockModel.getdRSRate());
        if (getMacAdd!=null){

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
                    Thread.sleep(500);
                } catch (ConnectionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (appCompatTextViewSelectPrinterSize.getText().toString().trim()
                        .equals(Constants.PRINTER_SIZES_LARGE.trim())) {
                    if (discount>0.0) {
                        printLabel = mZebraPrinterTask.getPrintHorizontalZPLLabel(checkStockModel);
                    }else {
                        printLabel = mZebraPrinterTask.getZeroDiscountPrintHorizontalZPLLabel(checkStockModel);
                    }
                }
                else if (appCompatTextViewSelectPrinterSize.getText().toString().trim()
                        .equals(Constants.PRINTER_SIZES_SMALL.trim())) {
                    if (discount>0.0) {
                        printLabel = mZebraPrinterTask.getPrintVerticalZPLLabel(checkStockModel);
                    }else {
                        printLabel=mZebraPrinterTask.getZeroDiscountPrintVerticalZPLLabel(checkStockModel);
                    }
                }
                else if (appCompatTextViewSelectPrinterSize.getText().toString().trim()
                        .equals(Constants.PRINTER_SIZE_LARGE_OFFER.trim())) {
                    if (checkStockModel.getUdWeekImported() != null){
                        printLabel = mZebraPrinterTask.getPrintHorizontalOfferZPLLabel(checkStockModel);
                    }else {
                        Toast.makeText(getContext(),"Barcode not in offer sale !",Toast.LENGTH_LONG).show();
                        // printLabel = mZebraPrinterTask.getPrintHorizontalNullOfferZPLLabel(checkStockModel);
                    }
                }
                else if (appCompatTextViewSelectPrinterSize.getText().toString().trim()
                        .equals(Constants.PRINTER_SIZE_GARDENING_SEL.trim())) {
                    printLabel = mZebraPrinterTask.getPrintHorizontalGardeningSel(checkStockModel);
                }else if (appCompatTextViewSelectPrinterSize.getText().toString().trim()
                        .equals(Constants.PRINTER_SIZES_LARGE_WAS_NOW.trim())) {
                    printLabel = mZebraPrinterTask.getPrintHorizontalWasNowZPLLabel(checkStockModel);
                } else if (appCompatTextViewSelectPrinterSize.getText().toString().trim()
                        .equals(Constants.PRINTER_SIZES_SMALL_WAS_NOW.trim())) {
                    printLabel = mZebraPrinterTask.getPrintVerticalWasNowZPLLabel(checkStockModel);
                } else if (appCompatTextViewSelectPrinterSize.getText().toString().trim()
                        .equals(Constants.PRINTER_SIZES_WHOLESALE_SMALL.trim())) {
                    printLabel = mZebraPrinterTask.getPrintWholesaleVerticalZPLLabel(checkStockModel);
                } else if (appCompatTextViewSelectPrinterSize.getText().toString().trim()
                        .equals(Constants.PRINTER_SIZES_WHOLESALE_LARGE.trim())) {
                    printLabel=mZebraPrinterTask.getPrintWholesaleHorizontalZPLLabel(checkStockModel);

                } else {
                    System.out.println("No Any comment");
                }
                if (printLabel != null) {
                    sendPrint(printLabel);
                }
            }
        }else{
            Toast.makeText(getContext(),"Set Printer MAC Address",Toast.LENGTH_LONG).show();
        }
    }

    void openSelectPrinterDialog(){
        printerSizeDialog=new PrinterSizeDialog(getContext(),listPrintSizes, this);
        printerSizeDialog.show();
    }

    @Override
    public void onItemClicked(String labelSize) {
        this.labelSize = labelSize;
        appCompatTextViewSelectPrinterSize.setText("");
        appCompatTextViewSelectPrinterSize.setText(labelSize);
        if(printerSizeDialog!=null){
            printerSizeDialog.dismiss();
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