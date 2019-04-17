package com.thekrayem.cryptoapp.helper.password;


import static com.thekrayem.cryptoapp.helper.password.PasswordStrengthResult.FAIL;
import static com.thekrayem.cryptoapp.helper.password.PasswordStrengthResult.MEH;
import static com.thekrayem.cryptoapp.helper.password.PasswordStrengthResult.STRONG;
import static com.thekrayem.cryptoapp.helper.password.PasswordStrengthResult.WEAK;

public class PasswordChecker {


    public static PasswordStrengthResult checkPasswordStrength(String password){
        if(password == null || password.length() < 5){
            return FAIL;
        }

        // long pass phrases are considered strong regardless
        if(password.length() >= 20){
            return STRONG;
        }

        // count everything
        int digitCount = 0, specialCharCount = 0, upperCaseCount = 0, lowerCaseCount = 0;
        for(char current : password.toCharArray()){
            if(Character.isUpperCase(current)){
                upperCaseCount++;
            }else if(Character.isLowerCase(current)){
                lowerCaseCount++;
            }else if(Character.isDigit(current)){
                digitCount++;
            }else {
                specialCharCount++;
            }
        }
        // a password that contains all types of chars is strong over 10 chars
        if(upperCaseCount > 0 && lowerCaseCount > 0 && digitCount > 0 && specialCharCount > 0){
            return password.length() >= 10 ? STRONG : MEH;
        }

        // with both letters and digits/specials, the cutoffs are 15 and 10
        if((upperCaseCount + lowerCaseCount) > 0 && (digitCount + specialCharCount) > 0){
            if(password.length() >= 15){
                return STRONG;
            }else {
                return password.length() >= 10 ? MEH : WEAK;
            }
        }

        // what's left at this point are text only (or digits and chars only) from 5 to 19 chars
        return password.length() >= 10 ? MEH : WEAK;
    }
}