package com.projects.dawid.gattclient;

/**
 * Helper class to get string value representation of Characteristic's value
 */

class CharacteristicValueRepresentation {
    static String translateToString(byte[] characteristicsValue) {
        if (characteristicsValue == null) {
            return "EMPTY";
        }

        String valueInString = "";
        for (byte currentValueByte : characteristicsValue) {
            valueInString = valueInString + " " + Integer.toHexString(currentValueByte);
        }

        return valueInString;
    }
}
