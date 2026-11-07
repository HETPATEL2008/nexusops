package com.hetpatel.nexusops.common.validation;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import com.hetpatel.nexusops.common.exception.BusinessException;

import org.springframework.stereotype.Component;

@Component
public class PhoneNumberValidator {

    private final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    public NormalizedPhoneNumber validateAndNormalize(
            String countryCode,
            String phoneNumber,
            String fieldName
    ) {

        if (countryCode == null || countryCode.isBlank()
                || phoneNumber == null || phoneNumber.isBlank()) {
            throw new BusinessException(
                    fieldName + " country code and phone number are required."
            );
        }

        String normalizedCountryCode = countryCode.trim();

        if (!normalizedCountryCode.matches("\\+\\d{1,3}")) {
            throw new BusinessException(
                    fieldName + " country code must be in the format +[country code]."
            );
        }

        String fullPhoneNumber = normalizedCountryCode + phoneNumber.trim();

        try {
            var parsedNumber = phoneNumberUtil.parse(fullPhoneNumber, null);

            if (!phoneNumberUtil.isValidNumber(parsedNumber)) {
                throw new BusinessException(
                        fieldName + " is not a valid phone number for the provided country code."
                );
            }

            int suppliedCountryCode = Integer.parseInt(
                    normalizedCountryCode.substring(1)
            );

            if (parsedNumber.getCountryCode() != suppliedCountryCode) {
                throw new BusinessException(
                        fieldName + " does not match the provided country code."
                );
            }

            String normalizedCallingCode =
                    "+" + parsedNumber.getCountryCode();

            String normalizedNationalNumber =
                    phoneNumberUtil.getNationalSignificantNumber(parsedNumber);

            return new NormalizedPhoneNumber(
                    normalizedCallingCode,
                    normalizedNationalNumber
            );

        } catch (NumberParseException exception) {
            throw new BusinessException(
                    fieldName + " is not a valid phone number.",
                    exception
            );
        }
    }

    public record NormalizedPhoneNumber(
            String countryCode,
            String phoneNumber
    ) {
    }
}
