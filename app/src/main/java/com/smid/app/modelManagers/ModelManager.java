package com.smid.app.modelManagers;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import com.smid.app.Adapters.ModelType;
import com.smid.app.GD_CONSTS;
import com.smid.app.Helper;
import com.smid.app.model.ModelBase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Allows - fetching all items from storage and persisting concrete item in storage.
 * This class provides external storage persistance as it's easier to debug than internal storage ;)
 *
 * Interfejs sie klaryfikuje: GetItems, Persist
 * TODO: dodaj delete.
 * Created by marek on 08.04.16.
 */
public abstract class ModelManager<T extends ModelBase> {
    ModelType modelType = ModelType.Gesture;
    /***
     * Name of directory from app's data where data related to managed model will be persisted.
     */
    protected abstract String getModelDirectory();

    /**
     * Prefix of filename storing data of one model instance.
     */
    protected abstract String getFilenamePattern();

    protected abstract String SerializeModel(T model);

    protected abstract  T DeserializeModel(String representation, String fullFilePath);

    /**
     * Context of device related operations.
     */
    Context context;


    public ModelManager(Context context) {
        this.context = context;
    }

    public List<T> GetItems() {
        List<T> ret = new ArrayList<T>();

        File[] files = GetAppDataPath().listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().matches("^" + getFilenamePattern() + "\\d+$");
            }
        });

        for (File file : files) {
            String fContent = GetFileContent(file);

            try {
                T g = DeserializeModel(fContent, file.getAbsolutePath());// new Gesture(fContent);

                ret.add(g);
            }
            catch(Exception e){

            }
        }

        return ret;
    }

    public T GetItemById(long id) {
        for(T item : GetItems()) {
            if(item.getId() == id) {
                return item;
            }
        }

        return null;
    }
//
//    private List<Gesture> GetListGestures() {
//        List<Gesture> ret = new ArrayList<Gesture>();
//        ret.add(new Gesture());
//        ret.add(new Gesture());
//        ret.add(new Gesture());
//        return ret;
//    }

    public void Persist(T model) throws GestureNameAlreadyExistsException, NullGestureNameException, ExternalServiceWithSameComponentAlreadyExists, NeedsPaymentException {
        if(model.getId() < 0) {

            long maxId = 0;

            List<T> items = GetItems();

            if(items.size() > 0) {
                maxId = Collections.max(items, new Comparator<T>() {
                    @TargetApi(Build.VERSION_CODES.KITKAT)
                    @Override
                    public int compare(T lhs, T rhs) {
                        return Long.compare(lhs.getId(), rhs.getId());
                    }
                }).getId();
            }

            model.setId(maxId + 1);
        } // TODO: in key/value preferences you can store latest minimal possible id for given type of element.
          // TODO: i potem to inicjalizuj w restart'cie

        String repr = SerializeModel(model);

        repr += "\n\n";

        File file = GetFullFileName(model);

        try {
            if(!file.exists()) {
                file.createNewFile();

                MediaScannerConnection.scanFile( // ensure
                        context,
                        new String[]{file.getAbsolutePath()},
                        null,
                        null);
            }

            FileOutputStream fileOutputStream = null;
            CipherOutputStream cipherOutputStream = null;
            OutputStreamWriter outputStreamWriter = null;

            try {
                if (GD_CONSTS.ENCRYPT_FILES) {
                    fileOutputStream = new FileOutputStream(file);

                    cipherOutputStream = new CipherOutputStream(fileOutputStream, createEncryptionCipher());
                    cipherOutputStream.write(repr.getBytes(Charset.forName("UTF-8")));

                    outputStreamWriter = new OutputStreamWriter(cipherOutputStream);

                    repr = Base64.encodeToString(repr.getBytes(Charset.forName("UTF-8")), 0);
                } else {
                    outputStreamWriter = new FileWriter(file);
                }

                String encryptedText = encryptString(repr);

                outputStreamWriter.write(encryptedText);
            } catch (Exception ex) {
                Helper.logException(context, ex);
            } finally {
//                Helper.closeQuietly(context, fileOutputStream);
//                Helper.closeQuietly(context, cipherOutputStream);
                Helper.closeQuietly(context, outputStreamWriter);
            }

        } catch (IOException e1) {
            Helper.logException(context, e1);
        }
    }

    /**
     * @return Returns file in which specified model will should be presisted.
     */
    private File GetFullFileName(T modelInstance) {
        String modelDataPath = GetAppDataPath().getPath();// + File.separator + modelDirectory;

        String modelFileName = GetFileName(modelInstance);

        return new File(modelDataPath, modelFileName);
    }

    private String GetFileName(T modelInstance) {
        return this.getFilenamePattern() + Long.toString(modelInstance.getId());
    }

    /***
     * @return Path of directory where all configuration should be saved.
     */
    private File GetAppDataPath() {
        File f =context.getExternalFilesDir(null);

        Log.d("ModelManger", "File path: " + f.getAbsolutePath());

        return f;
    }

    /**
     * Generates directory name where data associated with specified model will be persisted.
     * @return
     */
    private String GetSpecificModelFolderName () {
        return this.getModelDirectory();
    }

    private String GetFileContent(File file) {
        StringBuilder text = new StringBuilder();

        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        CipherInputStream cipherInputStream = null;
        BufferedReader br = null;

        try {
            fileInputStream = new FileInputStream(file);

            if(GD_CONSTS.ENCRYPT_FILES) {
                Cipher cipher = null;
                cipher = createDecryptionCipher();


                cipherInputStream = new CipherInputStream(fileInputStream, cipher);

                inputStreamReader = new InputStreamReader(cipherInputStream);
            } else {
                inputStreamReader = new FileReader(file);
            }

            br = new BufferedReader(new FileReader(file));

            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }

            br.close();
        }
        catch (Exception e) {
            Helper.logException(context, e);
        } finally {
//            Helper.closeQuietly(context, fileInputStream);
//            Helper.closeQuietly(context, inputStreamReader);
//            Helper.closeQuietly(context, cipherInputStream);
            Helper.closeQuietly(context, br);
        }

        String decryptedText = decryptString(text.toString());

        return decryptedText;
    }



    protected int getIdFromFullFilePath(String filename) {
        String[] parts = filename.split("/");

        return Integer.parseInt(parts[parts.length - 1].substring(getFilenamePattern().length()));
    }

    public void Delete(T model) {
        try {
            File file = GetFullFileName(model);
            file.delete();
        } catch(Exception e) {
            Log.d("error", "error");
        }
    }

    protected Cipher createEncryptionCipher() throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance(GD_CONSTS.ENCRYPTION_ALGORITHM);
        SecretKeySpec sks = new SecretKeySpec(GD_CONSTS.ENCRYPTION_KEY, GD_CONSTS.ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, sks);

        return cipher;
    }

    protected Cipher createDecryptionCipher() throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance(GD_CONSTS.ENCRYPTION_ALGORITHM);
        SecretKeySpec sks = new SecretKeySpec(GD_CONSTS.ENCRYPTION_KEY, GD_CONSTS.ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, sks);

        return cipher;
    }


    protected String encryptString(String original) {
        if(!GD_CONSTS.DO_ENCRYPT_FILES) {
            return original;
        }
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(GD_CONSTS.ENCRYPTION_IV);
            SecretKeySpec secretKeySpec = new SecretKeySpec(GD_CONSTS.ENCRYPTION_KEY, GD_CONSTS.ENCRYPTION_ALGORITHM_SHORT_NAME);

            Cipher cipher = Cipher.getInstance(GD_CONSTS.ENCRYPTION_ALGORITHM);

            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] originalBytes = original.getBytes(Charset.forName("UTF-8"));

            byte[] encrypted = new byte[cipher.getOutputSize(originalBytes.length)];

            int enc_len = cipher.update(originalBytes, 0, originalBytes.length, encrypted, 0);
            enc_len += cipher.doFinal(encrypted, enc_len);

            return Base64.encodeToString(encrypted, Base64.DEFAULT);
        } catch (Exception e) {
            Helper.logException(context, e);
            return null;
        }
    }

    protected String decryptString(String encrypted) {
        if(!GD_CONSTS.DO_ENCRYPT_FILES) {
            return encrypted;
        }
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(GD_CONSTS.ENCRYPTION_IV);
            SecretKeySpec secretKeySpec = new SecretKeySpec(GD_CONSTS.ENCRYPTION_KEY, GD_CONSTS.ENCRYPTION_ALGORITHM_SHORT_NAME);

            Cipher cipher = Cipher.getInstance(GD_CONSTS.ENCRYPTION_ALGORITHM);

            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] encryptedBytes = Base64.decode(encrypted, Base64.DEFAULT);


            byte[] decrypted = new byte[cipher.getOutputSize(encryptedBytes.length)];

            int dec_len = cipher.update(encryptedBytes, 0, encryptedBytes.length, decrypted, 0);
            dec_len += cipher.doFinal(decrypted, dec_len);

            return new String(decrypted, "UTF-8");
        } catch (Exception e) {
            Helper.logException(context, e);
            return null;
        }
    }
}
