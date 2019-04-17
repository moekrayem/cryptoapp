package com.thekrayem.cryptoapp.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.spongycastle.crypto.PBEParametersGenerator;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.jcajce.PBKDF2Key;
import org.spongycastle.jcajce.util.MessageDigestUtils;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import customlib.org.apache.commons.codec.digest.DigestUtils;

public class HelperMethods {

    /**
     * Adds items to the ActionBar, Listeners must be set manually by overriding the
     * onOptionsItemSelected(MenuItem) method in the calling activity. The ID's start from 0.
     * @param menu The menu to which the items are added
     * @param items Objects with odd indexes are Strings representing the name of the icon. Those with even indexes are the ID's of the buttons' drawables
     */
    public static void addToActionBar(Menu menu, Object... items){
        // this is used to set the listener in the calling activity
        int id=0;
        // add items depending on the objects passed as parameters
        // i+=2 because every two objects are information for one item
        for (int i = 0; i < items.length; i += 2) {
            if (items[i] != null) {
                // add the button to the menu
                MenuItem mi = menu.add(0, id, id, (String) items[i]);
                // set its icon
                mi.setIcon((Integer) items[i + 1]);
                // showed if there is room, else it is added to the overlay menu
                mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            }
            id++;
        }
    }

    // HACK WARNING, get rid of this
    public static String getRealPathFromURI_API19(Context context, Uri uri){
        try{
            String filePath = "";
            String wholeID = DocumentsContract.getDocumentId(uri);
            String id = wholeID.split(":")[1];
            String[] column = { MediaStore.Images.Media.DATA };
            String sel = MediaStore.Images.Media._ID + "=?";
            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    column, sel, new String[]{ id }, null);
            if(cursor == null){
                return null;
            }
            int columnIndex = cursor.getColumnIndex(column[0]);
            if (cursor.moveToFirst())
                filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    // TODO: 05/07/2017 YOU HAVE TO CHECK THIS AT SOME POINT. IS THE KEY DERIVATION CORRECT?
    /**
     * @param plain byte array to be encrypted
     * @param key AES key to be used for encryption, FFS DO NOT GENERATE THIS USING A PRNG, USE A PBKDF
     * @param initVector 16 bytes initialisation vector to be used for encryption
     */
    public static byte[] encrypt(byte[] plain, byte[] key, byte[] initVector)
            throws InvalidKeyException
            ,InvalidAlgorithmParameterException
            ,NoSuchAlgorithmException
            ,NoSuchPaddingException
            ,IllegalBlockSizeException
            ,BadPaddingException {
        if(initVector.length != 16){
            throw new IllegalArgumentException("Incorrect IV length, must be 128 bits");
        }
        SecretKey secret = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, secret,new IvParameterSpec(initVector));
        return cipher.doFinal(plain);
    }

    public static byte[] getEncryptionKeyFromPassword(char[] password, byte[] salt,int iterations,int keyLength){
        if(salt.length != 8){
            throw new IllegalArgumentException("Incorrect salt length, must be 64 bits");
        }
        PKCS5S2ParametersGenerator generator = new PKCS5S2ParametersGenerator(new SHA256Digest());
        generator.init(PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(password), salt, iterations);
        KeyParameter key = (KeyParameter)generator.generateDerivedMacParameters(keyLength);
        return key.getKey();
    }


    public static byte[] decrypt(byte[] encrypted,byte[] key, byte[] initVector)
            throws InvalidKeyException
            ,InvalidAlgorithmParameterException
            ,NoSuchAlgorithmException
            ,NoSuchPaddingException
            ,IllegalBlockSizeException
            ,BadPaddingException {
        if(initVector.length != 16){
            throw new IllegalArgumentException("Incorrect IV length, must be 128 bits");
        }
        SecretKey secret = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(initVector));
        return cipher.doFinal(encrypted);
    }



    public static AlertDialog showViewDialog (Context context, String title, String PositiveButtonTitle, String NegativeButtonTitle, View view, DialogInterface.OnClickListener listener){

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setView(view)
                .setTitle(title)
                .setCancelable(true);
        if(PositiveButtonTitle != null) {
            builder.setPositiveButton(PositiveButtonTitle, listener);
        }
        if(NegativeButtonTitle != null) {
            builder.setNegativeButton(NegativeButtonTitle, listener);
        }
        return builder.show();
    }

    public static byte[] getSha256(byte[] plain) throws NoSuchAlgorithmException{
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(plain);
        return md.digest();
    }


    public static byte[] xor(byte[] a, byte[] b) throws IllegalArgumentException {
        if(a.length != b.length){
            throw new IllegalArgumentException("The two xored arrays must have the same length.");
        }
        byte[] result = new byte[a.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) (((int) a[i]) ^ ((int) b[i]));
        }
        return result;
    }

    public static Bitmap encodeAsBitmap(String data)throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bm = writer.encode(data, BarcodeFormat.QR_CODE,1000, 1000);
        Bitmap imageBitmap = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888);

        for (int i = 0; i < imageBitmap.getWidth(); i++) {
            for (int j = 0; j < imageBitmap.getWidth(); j++) {
                imageBitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK: Color.WHITE);
            }
        }
        return imageBitmap;
    }

    public static boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public static boolean passwordIsNotShit(String password){
        char ch;
        boolean capitalFlag = false;
        boolean lowerCaseFlag = false;
        boolean numberFlag = false;
        for(int i=0;i < password.length();i++) {
            ch = password.charAt(i);
            if( Character.isDigit(ch)) {
                numberFlag = true;
            } else if (Character.isUpperCase(ch)) {
                capitalFlag = true;
            } else if (Character.isLowerCase(ch)) {
                lowerCaseFlag = true;
            }
            if(numberFlag && capitalFlag && lowerCaseFlag)
                return true;
        }
        return false;
    }

    public static boolean checkFileName(String name){
        return name.trim().length() > 0;
    }


    public static String getSizeLabelFromByte(long size){
        return getSizeLabelFromAny(size,0);
    }

    private static String getSizeLabelFromAny(long size, int unit){
        if(size < 1024 || unit > 4){
            return size + " " + getUnitLabel(unit);
        }
        return getSizeLabelFromAny(size/1024,unit + 1);
    }

    private static String getUnitLabel(int unit){
        switch (unit){
            case 0:
                return "B";
            case 1:
                return "KB";
            case 2:
                return "MB";
            case 3:
                return "GB";
            case 4:
                return "TB";
            default:
                // does not happen, yet I am returning XX instead of null because of paranoia
                return "XX";
        }
    }
}
