package com.projects.dawid.gattclient;

import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;

import java.util.UUID;

/**
 * Stores information about names of BLE Attributes taken from
 * https://www.bluetooth.com/specifications/gatt
 */
class GATTUUIDTranslator {
    private static final String UNKNOWN = "UNKNOWN";
    private static final String TAG = "UUID_TRANSLATOR";
    private SparseArray<String> mUUIDMap = new SparseArray<>();

    /**
     * Default constructor.
     */
    GATTUUIDTranslator() {
        fillMap();
    }

    /**
     * Translated BLE Attribute UUID to descriptive string.
     *
     * @param bluetoothUUID BLE Attribute UUID to be translated
     * @return string with user-friendly name of UUID
     */
    @NonNull
    public String standardUUID(@NonNull UUID bluetoothUUID) {

        int standardUUID = prepareStandardUUIDValue(bluetoothUUID);

        if (mUUIDMap.get(standardUUID) != null) {
            return mUUIDMap.get(standardUUID);
        }

        return UNKNOWN;
    }

    private void fillMap() {
        fillServicesInfo();
        fillCharacteristicInfo();
        fillDeclaration();
        fillDescriptors();
    }

    private void fillDescriptors() {
        mUUIDMap.put(0x2905, "Characteristic Aggregate Format");
        mUUIDMap.put(0x2900, "Characteristic Extended Properties");
        mUUIDMap.put(0x2904, "Characteristic Presentation Format");
        mUUIDMap.put(0x2901, "Characteristic User Description");
        mUUIDMap.put(0x2902, "Client Characteristic Configuration");
        mUUIDMap.put(0x290B, "Environmental Sensing Configuration");
        mUUIDMap.put(0x290C, "Environmental Sensing Measurement");
        mUUIDMap.put(0x290D, "Environmental Sensing Trigger Setting");
        mUUIDMap.put(0x2907, "External Report Reference");
        mUUIDMap.put(0x2909, "Number of Digitals");
        mUUIDMap.put(0x2908, "Report Reference");
        mUUIDMap.put(0x2903, "Server Characteristic Configuration");
        mUUIDMap.put(0x290E, "Time Trigger Setting");
        mUUIDMap.put(0x2906, "Valid Range");
        mUUIDMap.put(0x290A, "Value Trigger Setting");
    }

    private void fillDeclaration() {
        mUUIDMap.put(0x2803, "GATT Characteristic Declaration");
        mUUIDMap.put(0x2802, "GATT Include Declaration");
        mUUIDMap.put(0x2800, "GATT Primary Service Declaration");
        mUUIDMap.put(0x2801, "GATT Secondary Service Declaration");
    }

    private void fillCharacteristicInfo() {
        mUUIDMap.put(0x2A84, "Aerobic Heart Rate Upper Limit");
        mUUIDMap.put(0x2A7F, "Aerobic Threshold");
        mUUIDMap.put(0x2A80, "Age");
        mUUIDMap.put(0x2A5A, "Aggregate");
        mUUIDMap.put(0x2A43, "Alert Category ID");
        mUUIDMap.put(0x2A42, "Alert Category ID Bit Mask");
        mUUIDMap.put(0x2A06, "Alert Level");
        mUUIDMap.put(0x2A44, "Alert Notification Control Point");
        mUUIDMap.put(0x2A3F, "Alert Status");
        mUUIDMap.put(0x2AB3, "Altitude");
        mUUIDMap.put(0x2A81, "Anaerobic Heart Rate Lower Limit");
        mUUIDMap.put(0x2A82, "Anaerobic Heart Rate Upper Limit");
        mUUIDMap.put(0x2A83, "Anaerobic Threshold");
        mUUIDMap.put(0x2A58, "Analog");
        mUUIDMap.put(0x2A73, "Apparent Wind Direction");
        mUUIDMap.put(0x2A72, "Apparent Wind Speed");
        mUUIDMap.put(0x2A01, "Appearance");
        mUUIDMap.put(0x2AA3, "Barometric Pressure Trend");
        mUUIDMap.put(0x2A19, "Battery Level");
        mUUIDMap.put(0x2A49, "Blood Pressure Feature");
        mUUIDMap.put(0x2A35, "Blood Pressure Measurement");
        mUUIDMap.put(0x2A9B, "Body Composition Feature");
        mUUIDMap.put(0x2A9C, "Body Composition Measurement");
        mUUIDMap.put(0x2A38, "Body Sensor Location");
        mUUIDMap.put(0x2AA4, "Bond Management Control Point");
        mUUIDMap.put(0x2AA5, "Bond Management Feature");
        mUUIDMap.put(0x2A22, "Boot Keyboard Input Report");
        mUUIDMap.put(0x2A32, "Boot Keyboard Output Report");
        mUUIDMap.put(0x2A33, "Boot Mouse Input Report");
        mUUIDMap.put(0x2AA6, "Central Address Resolution");
        mUUIDMap.put(0x2AA8, "CGM Feature");
        mUUIDMap.put(0x2AA7, "CGM Measurement");
        mUUIDMap.put(0x2AAB, "CGM Session Run Time");
        mUUIDMap.put(0x2AAA, "CGM Session Start Time");
        mUUIDMap.put(0x2AAC, "CGM Specific Ops Control Point");
        mUUIDMap.put(0x2AA9, "CGM Status");
        mUUIDMap.put(0x2A5C, "CSC Feature");
        mUUIDMap.put(0x2A5B, "CSC Measurement");
        mUUIDMap.put(0x2A2B, "Current Time");
        mUUIDMap.put(0x2A66, "Cycling Power Control Point");
        mUUIDMap.put(0x2A65, "Cycling Power Feature");
        mUUIDMap.put(0x2A63, "Cycling Power Measurement");
        mUUIDMap.put(0x2A64, "Cycling Power Vector");
        mUUIDMap.put(0x2A99, "Database Change Increment");
        mUUIDMap.put(0x2A85, "Date of Birth");
        mUUIDMap.put(0x2A86, "Date of Threshold Assessment");
        mUUIDMap.put(0x2A08, "Date Time");
        mUUIDMap.put(0x2A0A, "Day Date Time");
        mUUIDMap.put(0x2A09, "Day of Week");
        mUUIDMap.put(0x2A7D, "Descriptor Value Changed");
        mUUIDMap.put(0x2A00, "Device Name");
        mUUIDMap.put(0x2A7B, "Dew Point");
        mUUIDMap.put(0x2A56, "Digital");
        mUUIDMap.put(0x2A0D, "DST Offset");
        mUUIDMap.put(0x2A6C, "Elevation");
        mUUIDMap.put(0x2A87, "Email Address");
        mUUIDMap.put(0x2A0C, "Exact Time 256");
        mUUIDMap.put(0x2A88, "Fat Burn Heart Rate Lower Limit");
        mUUIDMap.put(0x2A89, "Fat Burn Heart Rate Upper Limit");
        mUUIDMap.put(0x2A26, "Firmware Revision String");
        mUUIDMap.put(0x2A8A, "First Name");
        mUUIDMap.put(0xEE1D, "Fitness Machine Control Point");
        mUUIDMap.put(0x2A8B, "Five Zone Heart Rate Limits");
        mUUIDMap.put(0x2AB2, "Floor Number");
        mUUIDMap.put(0x2A8C, "Gender");
        mUUIDMap.put(0x2A51, "Glucose Feature");
        mUUIDMap.put(0x2A18, "Glucose Measurement");
        mUUIDMap.put(0x2A34, "Glucose Measurement Context");
        mUUIDMap.put(0x2A74, "Gust Factor");
        mUUIDMap.put(0x2A27, "Hardware Revision String");
        mUUIDMap.put(0x2A39, "Heart Rate Control Point");
        mUUIDMap.put(0x2A8D, "Heart Rate Max");
        mUUIDMap.put(0x2A37, "Heart Rate Measurement");
        mUUIDMap.put(0x2A7A, "Heat Index");
        mUUIDMap.put(0x2A8E, "Height");
        mUUIDMap.put(0x2A4C, "HID Control Point");
        mUUIDMap.put(0x2A4A, "HID Information");
        mUUIDMap.put(0x2A8F, "Hip Circumference");
        mUUIDMap.put(0x2ABA, "HTTP Control Point");
        mUUIDMap.put(0x2AB9, "HTTP Entity Body");
        mUUIDMap.put(0x2AB7, "HTTP Headers");
        mUUIDMap.put(0x2AB8, "HTTP Status Code");
        mUUIDMap.put(0x2ABB, "HTTPS Security");
        mUUIDMap.put(0x2A6F, "Humidity");
        mUUIDMap.put(0x2A2A, "IEEE 11073-20601 Regulatory Certification Data List");
        mUUIDMap.put(0x2AAD, "Indoor Positioning Configuration");
        mUUIDMap.put(0x2A36, "Intermediate Cuff Pressure");
        mUUIDMap.put(0x2A1E, "Intermediate Temperature");
        mUUIDMap.put(0x2A77, "Irradiance");
        mUUIDMap.put(0x2AA2, "Language");
        mUUIDMap.put(0x2A90, "Last Name");
        mUUIDMap.put(0x2AAE, "Latitude");
        mUUIDMap.put(0x2A6B, "LN Control Point");
        mUUIDMap.put(0x2A6A, "LN Feature");
        mUUIDMap.put(0x2AB1, "Local East Coordinate");
        mUUIDMap.put(0x2AB0, "Local North Coordinate");
        mUUIDMap.put(0x2A0F, "Local Time Information");
        mUUIDMap.put(0x2A67, "Location and Speed");
        mUUIDMap.put(0x2AB5, "Location Name");
        mUUIDMap.put(0x2AAF, "Longitude");
        mUUIDMap.put(0x2A2C, "Magnetic Declination");
        mUUIDMap.put(0x2AA0, "Magnetic Flux Density - 2D");
        mUUIDMap.put(0x2AA1, "Magnetic Flux Density - 3D");
        mUUIDMap.put(0x2A29, "Manufacturer Name String");
        mUUIDMap.put(0x2A91, "Maximum Recommended Heart Rate");
        mUUIDMap.put(0x2A21, "Measurement Interval");
        mUUIDMap.put(0x2A24, "Model Number String");
        mUUIDMap.put(0x2A68, "Navigation");
        mUUIDMap.put(0x2A46, "New Alert");
        mUUIDMap.put(0x2AC5, "Object Action Control Point");
        mUUIDMap.put(0x2AC8, "Object Changed");
        mUUIDMap.put(0x2AC1, "Object First-Created");
        mUUIDMap.put(0x2AC3, "Object ID");
        mUUIDMap.put(0x2AC2, "Object Last-Modified");
        mUUIDMap.put(0x2AC6, "Object List Control Point");
        mUUIDMap.put(0x2AC7, "Object List Filter");
        mUUIDMap.put(0x2ABE, "Object Name");
        mUUIDMap.put(0x2AC4, "Object Properties");
        mUUIDMap.put(0x2AC0, "Object Size");
        mUUIDMap.put(0x2ABF, "Object Type");
        mUUIDMap.put(0x2ABD, "OTS Feature");
        mUUIDMap.put(0x2A04, "Peripheral Preferred Connection Parameters");
        mUUIDMap.put(0x2A02, "Peripheral Privacy Flag");
        mUUIDMap.put(0x2A5F, "PLX Continuous Measurement");
        mUUIDMap.put(0x2A60, "PLX Features");
        mUUIDMap.put(0x2A5E, "PLX Spot-Check Measurement");
        mUUIDMap.put(0x2A50, "PnP ID");
        mUUIDMap.put(0x2A75, "Pollen Concentration");
        mUUIDMap.put(0x2A69, "Position Quality");
        mUUIDMap.put(0x2A6D, "Pressure");
        mUUIDMap.put(0x2A4E, "Protocol Mode");
        mUUIDMap.put(0x2A78, "Rainfall");
        mUUIDMap.put(0x2A03, "Reconnection Address");
        mUUIDMap.put(0x2A52, "Record Access Control Point");
        mUUIDMap.put(0x2A14, "Reference Time Information");
        mUUIDMap.put(0x2A4D, "Report");
        mUUIDMap.put(0x2A4B, "Report Map");
        mUUIDMap.put(0x2A92, "Resting Heart Rate");
        mUUIDMap.put(0x2A40, "Ringer Control Point");
        mUUIDMap.put(0x2A41, "Ringer Setting");
        mUUIDMap.put(0x2A54, "RSC Feature");
        mUUIDMap.put(0x2A53, "RSC Measurement");
        mUUIDMap.put(0x2A55, "SC Control Point");
        mUUIDMap.put(0x2A4F, "Scan Interval Window");
        mUUIDMap.put(0x2A31, "Scan Refresh");
        mUUIDMap.put(0x2A5D, "Sensor Location");
        mUUIDMap.put(0x2A25, "Serial Number String");
        mUUIDMap.put(0x2A05, "Service Changed");
        mUUIDMap.put(0x2A28, "Software Revision String");
        mUUIDMap.put(0x2A93, "Sport Type for Aerobic and Anaerobic Thresholds");
        mUUIDMap.put(0x2A47, "Supported New Alert Category");
        mUUIDMap.put(0x2A48, "Supported Unread Alert Category");
        mUUIDMap.put(0x2A23, "System ID");
        mUUIDMap.put(0x2ABC, "TDS Control Point");
        mUUIDMap.put(0x2A6E, "Temperature");
        mUUIDMap.put(0x2A1C, "Temperature Measurement");
        mUUIDMap.put(0x2A1D, "Temperature Type");
        mUUIDMap.put(0x2A94, "Three Zone Heart Rate Limits");
        mUUIDMap.put(0x2A12, "Time Accuracy");
        mUUIDMap.put(0x2A13, "Time Source");
        mUUIDMap.put(0x2A16, "Time Update Control Point");
        mUUIDMap.put(0x2A17, "Time Update State");
        mUUIDMap.put(0x2A11, "Time with DST");
        mUUIDMap.put(0x2A0E, "Time Zone");
        mUUIDMap.put(0x2A71, "True Wind Direction");
        mUUIDMap.put(0x2A70, "True Wind Speed");
        mUUIDMap.put(0x2A95, "Two Zone Heart Rate Limit");
        mUUIDMap.put(0x2A07, "Tx Power Level");
        mUUIDMap.put(0x2AB4, "Uncertainty");
        mUUIDMap.put(0x2A45, "Unread Alert Status");
        mUUIDMap.put(0x2AB6, "URI");
        mUUIDMap.put(0x2A9F, "User Control Point");
        mUUIDMap.put(0x2A9A, "User Index");
        mUUIDMap.put(0x2A76, "UV Index");
        mUUIDMap.put(0x2A96, "VO2 Max");
        mUUIDMap.put(0x2A97, "Waist Circumference");
        mUUIDMap.put(0x2A98, "Weight");
        mUUIDMap.put(0x2A9D, "Weight Measurement");
        mUUIDMap.put(0x2A9E, "Weight Scale Feature");
        mUUIDMap.put(0x2A79, "Wind Chill");
    }

    private void fillServicesInfo() {
        mUUIDMap.put(0x1811, "Alert Notification Service");
        mUUIDMap.put(0x1815, "Automation IO");
        mUUIDMap.put(0x180F, "Battery Service");
        mUUIDMap.put(0x1810, "Blood Pressure");
        mUUIDMap.put(0x181B, "Body Composition");
        mUUIDMap.put(0x181E, "Bond Management");
        mUUIDMap.put(0x181F, "Continuous Glucose Monitoring");
        mUUIDMap.put(0x1805, "Current Time Service");
        mUUIDMap.put(0x1818, "Cycling Power");
        mUUIDMap.put(0x1816, "Cycling Speed and Cadence");
        mUUIDMap.put(0x180A, "Device Information");
        mUUIDMap.put(0x181A, "Environmental Sensing");
        mUUIDMap.put(0x1800, "Generic Access");
        mUUIDMap.put(0x1801, "Generic Attribute");
        mUUIDMap.put(0x1808, "Glucose");
        mUUIDMap.put(0x1809, "Health Thermometer");
        mUUIDMap.put(0x180D, "Heart Rate");
        mUUIDMap.put(0x1823, "HTTP Proxy");
        mUUIDMap.put(0x1812, "Human Interface Device");
        mUUIDMap.put(0x1802, "Immediate Alert");
        mUUIDMap.put(0x1821, "Indoor Positioning");
        mUUIDMap.put(0x1820, "Internet Protocol Support");
        mUUIDMap.put(0x1803, "Link Loss");
        mUUIDMap.put(0x1819, "Location and Navigation");
        mUUIDMap.put(0x1807, "Next DST Change Service");
        mUUIDMap.put(0x1825, "Object Transfer");
        mUUIDMap.put(0x180E, "Phone Alert Status Service");
        mUUIDMap.put(0x1822, "Pulse Oximeter");
        mUUIDMap.put(0x1806, "Reference Time Update Service");
        mUUIDMap.put(0x1814, "Running Speed and Cadence");
        mUUIDMap.put(0x1813, "Scan Parameters");
        mUUIDMap.put(0x1824, "Transport Discovery");
        mUUIDMap.put(0x1804, "Tx Power");
        mUUIDMap.put(0x181C, "User Data");
        mUUIDMap.put(0x181D, "Weight Scale");
    }

    private int prepareStandardUUIDValue(@NonNull UUID bluetoothUUID) {
        String lowerUUIDNumber = take16BitPartOfUUID(bluetoothUUID);
        Log.d(TAG, "UUID parsed = " + lowerUUIDNumber);
        return Integer.parseInt(lowerUUIDNumber, 16);
    }

    private String take16BitPartOfUUID(@NonNull UUID bluetoothUUID) {
        return bluetoothUUID.toString().split("-")[0];
    }

}
