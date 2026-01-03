package ie.homesavers.warehousemanagement.ui.general;

import ie.homesavers.warehousemanagement.webservices.general.ResponseGeneralList;

public interface IGeneralItem {
    void onGeneralItemUpdateClicked(ResponseGeneralList responseGeneralList);
    void onGeneralItemDeleteClicked(ResponseGeneralList responseGeneralList);
}
