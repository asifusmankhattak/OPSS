package pbs.sme.survey.DB;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import pbs.sme.survey.helper.DialogHelper;
import pbs.sme.survey.model.Constants;
import pk.gov.pbs.utils.UXToolkit;

public class DatabaseBackup {

    public static void backupDatabase(Context context, Activity activity, String databaseName, UXToolkit mUXToolkit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            String packageName = context.getPackageName();
            String databasePath = "/data/data/" + packageName + "/databases/" + databaseName;

            try {
                File backupDB = new File(Environment.getExternalStoragePublicDirectory(Constants.SHARED_DIRECTORY) +File.separator+ databaseName);
                if (!backupDB.getParentFile().exists()) {
                    backupDB.getParentFile().mkdirs();
                }

                File currentDB = new File(databasePath);
                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

                mUXToolkit.showToast("Database backup successful!");
            } catch (IOException e) {
                mUXToolkit.showToast("Database backup Failed!");
            }
        }
        else if(activity.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);

        }
        else{
            final DialogHelper d=new DialogHelper();
            d.SingleClickDialogError(context,"ALLOW PERMISSION","Allow Storage Permissions in Settings", "Goto Setting", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.hideSingleError();
                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                    intent.setData(uri);
                    activity.startActivityForResult(intent, 1000);

                }
            });
        }
    }

    public static void restoreDatabase(Context context, Activity activity, String databaseName, UXToolkit mUXToolkit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            try {
                String packageName = context.getPackageName();
                String databasePath = "/data/data/" + packageName + "/databases/" + databaseName;
                File dbFile = new File(databasePath);
                File backupFile = new File(Environment.getExternalStoragePublicDirectory(Constants.SHARED_DIRECTORY) + File.separator+ databaseName);

                // Check if the backup exists
                if (backupFile.exists()) {
                    // Create a new database file at the target location
                    copyFile(backupFile, dbFile);
                    mUXToolkit.showToast("Database restored successful!");
                }

            } catch (IOException e) {
                mUXToolkit.showToast("Database restored Failed!");
            }
        }
        else if(activity.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
        }
        else{
            final DialogHelper d=new DialogHelper();
            d.SingleClickDialogError(context,"ALLOW PERMISSION","Allow Storage Permissions in Settings", "Goto Setting", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.hideSingleError();
                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                    intent.setData(uri);
                    activity.startActivityForResult(intent, 1000);

                }
            });
        }
    }

    private static void copyFile(File sourceFile, File destFile) throws IOException {
        try (InputStream inStream = new FileInputStream(sourceFile);
             OutputStream outStream = new FileOutputStream(destFile)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inStream.read(buffer)) > 0) {
                outStream.write(buffer, 0, length);
            }
        }
    }

}

