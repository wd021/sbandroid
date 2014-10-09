package com.shipbob.models;

public class ShippingInformationTable {

    private String imageFileName;
    private String shipOption;
    private String destinationAddress;
    private String contactName;
    private String insertDate;
    private int shipInformationTableId;
    private int addressId;
    // Empty constructor
    public ShippingInformationTable() {

    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public String getShipOption() {
        return shipOption;
    }

    public void setShipOption(String shipOption) {
        this.shipOption = shipOption;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(String insertDate) {
        this.insertDate = insertDate;
    }

    public int getId() {
        return shipInformationTableId;
    }

    public void setShipInformationTableId(int shipInformationTableId) {
        this.shipInformationTableId = shipInformationTableId;
    }

    public int getShipInformationTableId() {
        return shipInformationTableId;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }
}