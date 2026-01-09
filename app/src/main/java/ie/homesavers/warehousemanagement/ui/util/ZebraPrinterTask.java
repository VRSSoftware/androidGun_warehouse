package ie.homesavers.warehousemanagement.ui.util;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

//import ie.homesavers.warehousemanagement.R;
import ie.homesavers.warehousemanagement.R;
import ie.homesavers.warehousemanagement.webservices.check_stock.PriceListModel;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.PrinterStatus;
import com.zebra.sdk.printer.SGD;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ZebraPrinterTask {
    //    Connect to zebra Printer----------------------------------------------------------------------
    static String TAG="Zebra Printer";
    static ListenerZebraPrinter listenerZebraPrinter=null;
    //    public ZebraPrinterTask(){}
    public ZebraPrinterTask(ListenerZebraPrinter listenerZebraPrinter,
                            String printerMacAddress){
        this.listenerZebraPrinter=listenerZebraPrinter;
//    initializeZPConnection(printerMacAddress);
    }

    public void initializeZPConnection(String PRINTER_MAC_ADDRESS){
        Connection connection=null;
        if(!TextUtils.isEmpty(PRINTER_MAC_ADDRESS)){
            connection=new BluetoothConnection(PRINTER_MAC_ADDRESS);
            try {
                connection.open();
                if(listenerZebraPrinter!=null){
                    listenerZebraPrinter.onZPConnection(connection);
                }
                Log.i(TAG,"Printer Connected");
            } catch (ConnectionException e) {
                Log.i(TAG,"Printer Not Connected");
                e.printStackTrace();
            }
        }
    }

    public void initializeZebraPrinterConnection(Connection connection){
        ZebraPrinter zebraPrinter=null;
        if(connection.isConnected()){
            try {
                zebraPrinter= ZebraPrinterFactory.getInstance(connection);
                Log.i(TAG,"Checking Printer language");
                Log.i(TAG, SGD.GET("device.languages",connection));
                listenerZebraPrinter.onZebraPrinterConnection(zebraPrinter);
//                    Perform Printer Task here
//                sendPrint(zebraPrinter,mConnection);
            } catch (ConnectionException e) {
                Log.i(TAG, "Zebra Printer Not Connected");
                e.printStackTrace();

            } catch (ZebraPrinterLanguageUnknownException e) {
                Log.i(TAG, "Unknown Printer Languages");
                e.printStackTrace();
            }
        }

    }

    // Sees if the printer is ready to print
    public  Boolean checkPrinterStatus(ZebraPrinter printer) {
        Log.i(TAG, "Check Printer Status");
        try {
            if(printer!=null){
                PrinterStatus printerStatus = printer.getCurrentStatus();
                if (printerStatus.isReadyToPrint) {
                    return true;
                }
            }

        } catch (ConnectionException e) {
            e.printStackTrace();
        }
        return false;
    }

    public  void showPrinterStatus(Context context, ZebraPrinter printer) {
        Log.i(TAG, "Checking Printer Status");

        try {
            PrinterStatus printerStatus = printer.getCurrentStatus();
            if (printerStatus.isReadyToPrint) {
                Log.i(TAG, context.getString(R.string.ready_to_print));
            } else if (printerStatus.isPaused) {
                Log.i(TAG, context.getString(R.string.print_failed) + " " + context.getString(R.string.printer_paused));
            } else if (printerStatus.isHeadOpen) {
                Log.i(TAG, context.getString(R.string.print_failed) + " " + context.getString(R.string.head_open));
            } else if (printerStatus.isPaperOut) {
                Log.i(TAG, context.getString(R.string.print_failed) + " " + context.getString(R.string.paper_out));
            } else {
                Log.i(TAG, context.getString(R.string.print_failed) + " " + context.getString(R.string.cannot_print));
            }
        } catch (ConnectionException e) {
            Log.i(TAG, context.getString(R.string.print_failed) + " " + context.getString(R.string.no_printer));
            e.printStackTrace();
        }
    }

// Takes the size of the pdf and the printer's maximum size and scales the file down

    public  String scalePrint (Connection connection) throws ConnectionException {
        int fileWidth = 0;
        String scale = "dither scale-to-fit";

        if (fileWidth != 0) {
            String printerModel = SGD.GET("device.host_identification",connection).substring(0,5);
            double scaleFactor;

            if (printerModel.equals("iMZ22")||printerModel.equals("QLn22")||printerModel.equals("ZD410")) {
                scaleFactor = 2.0/fileWidth*100;
            } else if (printerModel.equals("iMZ32")||printerModel.equals("QLn32")||printerModel.equals("ZQ510")) {
//                scaleFactor = 3.0/fileWidth*100;
                scaleFactor = 3.0/120;
            } else if (printerModel.equals("QLn42")||printerModel.equals("ZQ520")||
                    printerModel.equals("ZD420")||printerModel.equals("ZD500")||
                    printerModel.equals("ZT220")||printerModel.equals("ZT230")||
                    printerModel.equals("ZT410")) {
                scaleFactor = 4.0/fileWidth*100;
            } else if (printerModel.equals("ZT420")) {
                scaleFactor = 6.5/fileWidth*100;
            } else {
                scaleFactor = 100;
            }

            scale = "dither scale=" + (int) scaleFactor + "x" + (int) scaleFactor;
        }

        return scale;
    }

    //TODO------------- Small Zero discount Barcode Size Print--------------------------------------------------------

    public  String getZeroDiscountPrintVerticalZPLLabel(PriceListModel checkStockModel) {
        String pattern = "dd|MM|yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        String date = simpleDateFormat.format(new Date());
        String configLabel = null;

        if (checkStockModel!=null) {
           
// set height add ^PW400 "^POI^PW400^MNN^LL200^LH0,0" + "\r\n" +
            configLabel =
                    "^XA" +
                            "^CI28" +
                            "^PO^PW600" +
                            "^00^MNN^LL305^LH0,0" + "\r\n" +
                            "^FO15,60^A0N,40,30" +
                            "^FB 470,2"+
                            "^FD "+checkStockModel.getItemName()+"^FS"+

                            "^FO470,60^A0N,30,20" +
                            "^FD "+date+"^FS"+

                            "^FO530,100^A0N,40,30" +
                            "^FD " +checkStockModel.getGrading()+"^FS"+

                            "^FO15,160^A0N,30,20" +
                            "^FDCase Size:"+checkStockModel.getCasesizeQty()+" ^FS"+

                            "^FO250,250^A0N,30,20" +
                            "^FDDept: "+checkStockModel.getItemsubgrpName()+"^FS"+


                            // "^FO150,285"+
                            // "^FB275,1,0,R,0,0"+
                            // "^A0N,20,20"+
                            "^FO300,285"+
                            "^FB275,1,0,R,0"+
                            "^A0N,20,20"+
                            "^FD"  + checkStockModel.getUdSelPriceUnitLabel() + "^FS"+

                            "^FO15,210"+
                            "^A0,30,20" +
                            "^BY1\n" +
                            "^BCN,60,Y,N,N\n" +
                            "^FD"+checkStockModel.getBarcode()+"^FS\n" +
                            "^CI0,21,36 ^FO350,150^A0N,80,70^FD $"+checkStockModel.getMrp()+"^FS"+
                            "^XZ";



        }
        return configLabel;
    }

//TODO-------------Large Zero discount Barcode Size Print---------------------------------------------------------

    public  String getZeroDiscountPrintHorizontalZPLLabel(PriceListModel checkStockModel) {
        String pattern = "dd|MM|yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());
        System.out.println(date);
        String configLabel = null;

        if (checkStockModel!=null) {

            configLabel =
                    "^XA" +
                            "^PO^PW600" +
                            "^00^MNN^LL1400^LH0,0" + "\r\n" +
                            "^FO130,84"+
                            "^A0,30,20" +
                            "^BY2\n" +
                            "^BCR,100,Y,N,N\n" +
                            "^FD"+checkStockModel.getBarcode()+"^FS\n" +

                            "^FO330,84^A0R,100,100" +
                            "^FB 1000,2"+
                            "^FD"+checkStockModel.getItemName()+"^FS"+

                            "^FO440,1230^A0R,40,30" +
                            "^FD "+date+"^FS"+

                            "^FO380,1310^A0R,40,30" +
                            "^FD " +checkStockModel.getGrading()+"^FS"+

                            "^FO250,84^A0R,50,30" +
                            "^FDCase Size:"+checkStockModel.getCasesizeQty()+" ^FS"+

                            "^FO35,84^A0R,50,30" +
                            "^FDDept : "+checkStockModel.getItemsubgrpName()+"^FS"+

                            "^FO10,815"+
                            "^FB580,1,0,R,0,0"+
                            "^A0R,30,30"+
                           "^FD"+checkStockModel.getUdSelPriceUnitLabel()+"^FS"+

                            "^FO35,830^A0R,50,30" +
                            "^FDDept Number: "+checkStockModel.getItemsubgrpId()+"^FS"+

                            "^CI0,21,36 ^FO200,800 ^A0R,180,100^FD$"+checkStockModel.getMrp()+"^FS"+
                            "^XZ";



        }
        return configLabel;
    }


//TODO------------- Small Barcode Size Print--------------------------------------------------------

    public  String getPrintVerticalZPLLabel(PriceListModel checkStockModel) {
        String pattern = "dd|MM|yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        String date = simpleDateFormat.format(new Date());
        System.out.println(date);
        double a1= Double.parseDouble(checkStockModel.getMrp());
        double b= Double.parseDouble(checkStockModel.getdRSRate());
        String totalRate = String.valueOf(a1+b);

        String configLabel = null;

        if (checkStockModel!=null) {


// set height add ^PW400 "^POI^PW400^MNN^LL200^LH0,0" + "\r\n" +
            configLabel =


                    "^XA" +
                            "^PO^PW600" +
                            "^00^MNN^LL305^LH0,0" + "\r\n" +
                            "^FO15,60^A0N,40,30" +
                            "^FB 470,2"+
                            "^FD "+checkStockModel.getItemName()+" ^FS"+

                            "^FO470,60^A0N,30,20" +
                            "^FD "+date+"^FS"+

                            "^FO530,100^A0N,40,30" +
                            "^FD"+checkStockModel.getGrading()+"^FS"+

                            "^FO15,160^A0N,30,20" +
                            "^FDCase Size:"+checkStockModel.getCasesizeQty()+"^FS"+

                            "^CI0,21,36 ^FO350,240^A0N,20,20^FD+Deposit:$"+checkStockModel.getdRSRate()+"^FS"+

                            "^FO290,275^A0N,30,20" +
                            "^FDDept:"+checkStockModel.getItemsubgrpName()+"^FS"+


                            "^FO15,210"+
                            "^A0,30,20" +
                            "^BY1\n" +
                            "^BCN,60,Y,N,N\n" +
                            "^FD"+checkStockModel.getBarcode()+"^FS\n" +

                            "^CI0,21,36 ^FO350,170^A0N,60,50^FD $"+checkStockModel.getMrp()+"^FS"+

                            "^XZ";


        }
        return configLabel;
    }

//TODO-------------Large Barcode Size Print---------------------------------------------------------

    public  String getPrintHorizontalZPLLabel(PriceListModel checkStockModel) {
        String pattern = "dd|MM|yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());
        double a1= Double.parseDouble(checkStockModel.getMrp());
        double b= Double.parseDouble(checkStockModel.getdRSRate());
        String totalRate = String.valueOf(a1+b);
        System.out.println(date);
        String configLabel = null;

        if (checkStockModel!=null) {
// set height add ^PW400 "^POI^PW400^MNN^LL1400^LH0,0" + "\r\n" +

            configLabel =

                    "^XA" +
                            "^PO^PW600" +
                            "^00^MNN^LL1400^LH0,0" + "\r\n" +
                            "^FO130,84"+
                            "^A0,30,20" +
                            "^BY2\n" +
                            "^BCR,100,Y,N,N\n" +
                            "^FD"+checkStockModel.getBarcode()+"^FS\n" +

                            "^FO330,84^A0R,100,100" +
                            "^FB 1000,2"+
                            "^FD"+checkStockModel.getItemName()+"^FS"+

                            "^FO440,1230^A0R,40,30" +
                            "^FD "+date+"^FS"+

                            "^FO380,1310^A0R,40,30" +
                            "^FD"+checkStockModel.getGrading()+"^FS"+

                            "^FO250,84^A0R,50,30" +
                            "^FDCase Size:"+checkStockModel.getCasesizeQty()+"^FS"+

                            "^FO10,84^A0R,50,30" +
                            "^FDDept :"+checkStockModel.getItemsubgrpName()+"^FS"+

                            "^FO10,720^A0R,50,30" +
                            "^FDDept Number: "+checkStockModel.getItemsubgrpId()+"^FS"+

                            "^CI0,21,36 ^FO115,710 ^A0R,35,35^FD+Deposit:$"+checkStockModel.getdRSRate()+"^FS"+

                            "^CI0,21,36 ^FO180,900 ^A0R,130,100^FD$"+checkStockModel.getMrp()+"^FS"+

                            "^XZ";
        }
        return configLabel;
    }

//TODO------------PrintHorizontalOfferZPLLabel------------------------------------------------------

    public  String getPrintHorizontalOfferZPLLabel(PriceListModel checkStockModel) {
        String pattern = "dd|MM|yyyy";
        String udWeekImported=checkStockModel.getUdWeekImported();

        Log.i("udWeekImported",udWeekImported);

        String[] parts = udWeekImported.split("€");
        String part1 = parts[0]; // 004
        String part2 = parts[1]; // 034556

        String udWeekImportedBefore=part1;
        String udWeekImportedAfter=part2;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        String date = simpleDateFormat.format(new Date());
//        System.out.println(date);
        String configLabel =null;

        if (checkStockModel!=null) {

            configLabel =
                    "^XA" +
                            "^PO^PW600" +
                            "^00^MNN^LL1400^LH0,0" + "\r\n" +
                            "^FO130,84"+
                            "^A0,30,20" +
                            "^BY2\n" +
                            "^BCR,100,Y,N,N\n" +
                            "^FD"+checkStockModel.getBarcode()+"^FS\n" +

                            "^FO330,84^A0R,100,100" +
                            "^FB 1000,2"+
                            "^FD"+checkStockModel.getItemName()+"^FS"+

                            "^FO440,1230^A0R,40,30" +
                            "^FD "+date+"^FS"+

                            "^FO380,1310^A0R,40,30" +
                            "^FD " +checkStockModel.getGrading()+"^FS"+

                            "^FO250,84^A0R,50,30" +
                            "^FDCase Size:"+checkStockModel.getCasesizeQty()+" ^FS"+

                            "^FO20,84^A0R,50,30" +
                            "^FDDept : "+checkStockModel.getItemsubgrpName()+"^FS"+

                            "^FO20,850^A0R,50,30" +
                            "^FDDept Number: "+checkStockModel.getItemsubgrpId()+"^FS"+

                            "^CI0,21,36 ^FO180,580 ^A0R,150,80^FD"+udWeekImportedBefore+"$"+udWeekImportedAfter+"^FS"+

                            "^CI0,21,36 ^FO100,800 ^A0R,70,40^FD$"+checkStockModel.getMrp()+"^FS"+
                            "^XZ";
        }

        return configLabel;
    }

//   TODO------------PrintHorizontalGardeningSelLabel------------------------------------------------------

    public  String getPrintHorizontalGardeningSel (PriceListModel checkStockModel) {
        String pattern = "dd|MM|yyyy";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        String date = simpleDateFormat.format(new Date());
        System.out.println(date);
        String configLabelNull = null;

        if (checkStockModel!=null) {


// set height add ^PW400 "^POI^PW400^MNN^LL1400^LH0,0" + "\r\n" +

            configLabelNull =

                    "^XA" +
                            "^PO^PW800" +
                            "^00^MNN^LL1766^LH0,0" + "\r\n" +
                            "^FO180,84"+
                            "^A0,50,30" +
                            "^BY2\n" +
                            "^BCR,150,Y,N,N\n" +
                            "^FD"+checkStockModel.getBarcode()+"^FS\n" +

                            "^FO500,84^A0R,100,100" +
                            "^FB 1000,2"+
                            "^FD"+checkStockModel.getItemName()+"^FS"+

                            "^FO600,1350^A0R,70,60" +
                            "^FD "+date+"^FS"+

                            "^FO500,1450^A0R,70,60" +
                            "^FD " +checkStockModel.getGrading()+"^FS"+

                            "^FO350,84^A0R,70,50" +
                            "^FDCase Size:"+checkStockModel.getCasesizeQty()+" ^FS"+

                            "^FO20,700^A0R,70,50" +
                            "^FDDept : "+checkStockModel.getItemsubgrpName()+"^FS"+

                            "^CI0,21,36 ^FO150,1100 ^A0R,200,130^FD$"+checkStockModel.getMrp()+"^FS"+
                            "^XZ";


        }
        return configLabelNull;

    }

//TODO------------- Was Now Small Barcode Size Print--------------------------------------------------------

    public  String getPrintVerticalWasNowZPLLabel(PriceListModel checkStockModel) {
        String pattern = "dd|MM|yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        String date = simpleDateFormat.format(new Date());
        System.out.println(date);

        String configLabel = null;

        if (checkStockModel!=null) {


// set height add ^PW400 "^POI^PW400^MNN^LL200^LH0,0" + "\r\n" +
            configLabel =
                    "^XA" +
                            "^PO^PW600" +
                            "^00^MNN^LL305^LH0,0" + "\r\n" +
                            "^FO15,60^A0N,40,30" +
                            "^FB 470,2"+
                            "^FD "+checkStockModel.getItemName()+"^FS"+

                            "^FO470,60^A0N,30,20" +
                            "^FD "+date+"^FS"+

                            "^FO530,100^A0N,40,30" +
                            "^FD " +checkStockModel.getGrading()+"^FS"+

                            "^FO330,110^A0N,30,20" +
                            "^CI0,21,36^FD WAS: $"+checkStockModel.getWasSaleRate()+"^FS"+

                            "^FO15,160^A0N,30,20" +
                            "^FD Case Size:"+checkStockModel.getCasesizeQty()+" ^FS"+

                            "^FO250,250^A0N,30,20" +
                            "^FD Dept: "+checkStockModel.getItemsubgrpName()+"^FS"+


                            "^FO15,210"+
                            "^A0,30,20" +
                            "^BY1\n" +
                            "^BCN,60,Y,N,N\n" +
                            "^FD"+checkStockModel.getBarcode()+"^FS\n" +
                            "^CI0,21,36 ^FO260,170^A0N,60,50^FD NOW: $"+checkStockModel.getMrp()+"^FS"+
                            "^XZ";

        }
        return configLabel;
    }

//TODO-------------Was Now Large Barcode Size Print---------------------------------------------------------

    public  String getPrintHorizontalWasNowZPLLabel(PriceListModel checkStockModel) {
        String pattern = "dd|MM|yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());
        System.out.println(date);
        String configLabel = null;

        if (checkStockModel!=null) {
// set height add ^PW400 "^POI^PW400^MNN^LL1400^LH0,0" + "\r\n" +

            configLabel =
                    "^XA" +
                            "^PO^PW600" +
                            "^00^MNN^LL1400^LH0,0" + "\r\n" +
                            "^FO130,84"+
                            "^A0,30,20" +
                            "^BY2\n" +
                            "^BCR,100,Y,N,N\n" +
                            "^FD"+checkStockModel.getBarcode()+"^FS\n" +

                            "^FO330,84^A0R,100,100" +
                            "^FB 1000,2"+
                            "^FD"+checkStockModel.getItemName()+"^FS"+

                            "^FO440,1230^A0R,40,30" +
                            "^FD "+date+"^FS"+

                            "^FO380,1310^A0R,40,30" +
                            "^FD " +checkStockModel.getGrading()+"^FS"+

                            "^FO370,900^A0R,50,40" +
                            "^CI0,21,36^FD WAS: $"+checkStockModel.getWasSaleRate()+"^FS"+

                            "^FO250,84^A0R,50,30" +
                            "^FDCase Size:"+checkStockModel.getCasesizeQty()+" ^FS"+

                            "^FO20,84^A0R,50,30" +
                            "^FDDept : "+checkStockModel.getItemsubgrpName()+"^FS"+

                            "^FO20,850^A0R,50,30" +
                            "^FDDept Number: "+checkStockModel.getItemsubgrpId()+"^FS"+

                            "^CI0,21,36 ^FO120,720 ^A0R,150,90^FD NOW: $"+checkStockModel.getMrp()+"^FS"+
                            "^XZ";


//            //changed by sandesh 30-10-2025
//            configLabel =
//                    "^XA" +
//                            "^PO^PW600" +
//                            "^00^MNN^LL1400^LH0,0" + "\r\n" +
//
//                            // Barcode position changed: from ^FO130,84 → ^FO150,84
//                            "^FO150,84" +
//                            "^A0,30,20" +
//                            "^BY2\n" +
//                            "^BCR,100,Y,N,N\n" +
//                            "^FD" + checkStockModel.getBarcode() + "^FS\n" +
//
//                            // Item name position changed: ^FO330,84 → ^FO350,84
//                            "^FO350,84^A0R,100,100" +
//                            "^FB1000,2" +
//                            "^FD" + checkStockModel.getItemName() + "^FS" +
//
//                            "^FO440,1230^A0R,40,30" +
//                            "^FD " + date + "^FS" +
//
//                            "^FO380,1310^A0R,40,30" +
//                            "^FD " + checkStockModel.getGrading() + "^FS" +
//
//                            "^FO370,900^A0R,50,40" +
//                            "^CI0,21,36^FD WAS: $" + checkStockModel.getWasSaleRate() + "^FS" +
//
//                            // Case size position changed: ^FO250,84 → ^FO270,84
//                            "^FO270,84^A0R,50,30" +
//                            "^FDCase Size:" + checkStockModel.getCasesizeQty() + "^FS" +
//
//                            // Dept position changed: ^FO20,84 → ^FO50,84
//                            "^FO50,84^A0R,50,30" +
//                            "^FDDept : " + checkStockModel.getItemsubgrpName() + "^FS" +
//
//                            // New field added here
//                            "^FO50,1230^A0R,30,30" +
//                            "^FD$" + checkStockModel.getUdSelPriceUnitLabel()+"^FS" +
//
//                            // Dept Number position changed: ^FO20,850 → ^FO50,850
//                            "^FO50,850^A0R,50,30" +
//                            "^FDDept Number: " + checkStockModel.getItemsubgrpId() + "^FS" +
//                            "^CI0,21,36 ^FO120,720 ^A0R,150,90^FD NOW: $" + checkStockModel.getMrp() + "^FS" +
//                            "^XZ";


        }
        return configLabel;
    }

//TODO------------- Wholesale Small Barcode Size Print--------------------------------------------------------

    public  String getPrintWholesaleVerticalZPLLabel(PriceListModel checkStockModel) {
        String pattern = "dd|MM|yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        BigDecimal saleVatValue = new BigDecimal(Double.toString(calculationSaleAndVat(checkStockModel)));
        saleVatValue = saleVatValue.setScale(2, RoundingMode.HALF_UP);
        String date = simpleDateFormat.format(new Date());
        System.out.println(date);
        String configLabel = null;

        if (checkStockModel!=null) {


// set height add ^PW400 "^POI^PW400^MNN^LL200^LH0,0" + "\r\n" +
            configLabel =


//                    "^XA" +
//                            "^PO^PW600" +
//                            "^00^MNN^LL305^LH0,0" + "\r\n" +
//                            "^FO15,60^A0N,40,30" +
//                            "^FB 470,2"+
//                            "^FD "+checkStockModel.getItemName()+" ^FS"+
//
//                            "^FO470,60^A0N,30,20" +
//                            "^FD "+date+"^FS"+
//
//                            "^FO530,100^A0N,40,30" +
//                            "^FD"+checkStockModel.getGrading()+"^FS"+
//
//                            "^FO15,160^A0N,30,20" +
//                            "^FDCase Size:"+checkStockModel.getCasesizeQty()+"^FS"+
//
//                            "^CI0,21,36 ^FO350,240^A0N,20,20^FD+Deposit:$"+checkStockModel.getdRSRate()+"^FS"+
//
//                            "^FO290,275^A0N,30,20" +
//                            "^FDDept:"+checkStockModel.getItemsubgrpName()+"^FS"+
//
//                            "^FO15,210"+
//                            "^A0,30,20" +
//                            "^BY1\n" +
//                            "^BCN,60,Y,N,N\n" +
//                            "^FD"+checkStockModel.getBarcode()+"^FS\n" +
//
//                            "^CI0,21,36 ^FO350,170^A0N,60,50^FD $"+checkStockModel.getMrp()+"^FS"+
//
//                            "^XZ";

            "^XA" +
                    "^PO^PW600" +
                    "^00^MNN^LL305^LH0,0" + "\r\n" +
                    "^FO15,140^A0N,40,30" +
                    "^FB 450,3"+
                    "^FD"+checkStockModel.getItemName()+"^FS"+

//                    "^FO310,20^A0N,25,25" +
//                    "^FD"+checkStockModel.getStkKey()+"^FS"+//*888888888888*

//                    "^FO510,20^A0N,25,25" +
//                    "^FD 22R31 ^FS"+

                    "^FO470,60^A0N,30,20" +
                    "^FD "+date+"^FS"+

                    "^FO10,280^A0N,30,30" +
                    "^FD VAT: "+checkStockModel.getVatRate()+"%"+"^FS"+

                    "^CI0,21,36 ^FO260,280^A0N,30,30^FD $"+saleVatValue+"  "+"INCL VAT"+"^FS"+

                    "^FO15,60"+
                    "^A0,20,15" +
                    "^BY1\n" +
                    "^BCN,50,Y,N,N\n" +
                    "^FD"+checkStockModel.getBarcode()+"^FS\n" +

                    "^CI0,21,36 ^FO360,190^A0N,60,60^FD $"+checkStockModel.getSaleRate()+"^FS"+
                    "^XZ";


        }
        return configLabel;
    }

//TODO-------------Wholesale Large Barcode Size Print---------------------------------------------------------

    public  String getPrintWholesaleHorizontalZPLLabel(PriceListModel checkStockModel) {
        String pattern = "dd|MM|yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());
        BigDecimal saleVatValue = new BigDecimal(Double.toString(calculationSaleAndVat(checkStockModel)));
        saleVatValue = saleVatValue.setScale(2, RoundingMode.HALF_UP);
        String configLabel = null;

        if (checkStockModel!=null) {
// set height add ^PW400 "^POI^PW400^MNN^LL1400^LH0,0" + "\r\n" +

            configLabel =

//                    "^XA" +
//                            "^PO^PW600" +
//                            "^00^MNN^LL1400^LH0,0" + "\r\n" +
//                            "^FO130,84"+
//                            "^A0,30,20" +
//                            "^BY2\n" +
//                            "^BCR,100,Y,N,N\n" +
//                            "^FD"+checkStockModel.getBarcode()+"^FS\n" +
//
//                            "^FO330,84^A0R,100,100" +
//                            "^FB 1000,2"+
//                            "^FD"+checkStockModel.getItemName()+"^FS"+
//
//                            "^FO440,1230^A0R,40,30" +
//                            "^FD "+date+"^FS"+
//
//                            "^FO380,1310^A0R,40,30" +
//                            "^FD"+checkStockModel.getGrading()+"^FS"+
//
//                            "^FO250,84^A0R,50,40" +
//                            "^FDCase Size:"+checkStockModel.getCasesizeQty()+"^FS"+
//
//                            "^FO10,84^A0R,50,40" +
//                            "^FD VAT :"+checkStockModel.getVatRate()+" % "+"^FS"+
//
//                            "^FO10,700^A0R,50,40" +
//                            "^FD"+saleVatValue+" INCL    VAT "+"^FS"+
//
//                            "^FO10,820^A0R,50,30" +
//                            "^FD INCL VAT ^FS"+
//
//                            "^CI0,21,36 ^FO180,900 ^A0R,140,120^FD$"+checkStockModel.getSaleRate()+"^FS"+
//
//                            "^XZ";

                    "^XA" +
                            "^PO^PW600" +
                            "^00^MNN^LL1400^LH0,0" + "\r\n" +

                            "^FO450,84"+
                            "^A0,30,20" +
                            "^BY2\n" +
                            "^BCR,100,Y,N,N\n" +
                            "^FD"+checkStockModel.getBarcode()+"^FS\n" +

                            "^FO440,1230^A0R,40,30" +
                            "^FD "+date+"^FS"+

//                            "^FO530,720^A0R,40,30" +
//                            "^FD *0500013609S155*^FS"+

//                            "^FO530,1220^A0R,40,30" +
//                            "^FD 22R31^FS"+

                            "^FO200,84^A0R,100,100" +
                            "^FB 1300,2"+
                            "^FD"+checkStockModel.getItemName()+"^FS"+

//                            "^FO180,84^A0R,50,50" +
//                            "^FD 656322 ^FS"+

                            "^FO20,84^A0R,70,50" +
                            "^FD VAT  "+checkStockModel.getVatRate()+" %"+" ^FS"+

//                            "^FO20,710^A0R,80,50" +
//                            "^FD $"+checkStockModel.getSaleRate()+" ^FS"+

                            "^CI0,21,36 ^FO20,710 ^A0R,80,50^FD$"+saleVatValue+"  "+"INCL  VAT"+"^FS"+


                            "^CI0,21,36 ^FO130,910 ^A0R,150,120^FD$"+checkStockModel.getSaleRate()+"^FS"+

                            "^XZ";
        }
        return configLabel;
    }

    private double calculationSaleAndVat(PriceListModel priceListModel) {
        double saleRate = Double.parseDouble( priceListModel.getSaleRate());
        double vatRate = Double.parseDouble(priceListModel.getVatRate());
        return saleRate+((saleRate*vatRate)/100);
    }

}