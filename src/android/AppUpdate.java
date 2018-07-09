package com.cordova.plugin.app.update;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;


import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


//Native interface class as defined in plugin.xml
public class AppUpdate extends CordovaPlugin {

    private CallbackContext updateCallback;

    /*
     * Communicate with  js interface and accept methods and parameters;
     * @param action: called method by name as string from js interface;
     * @param args: json array contains argument passed to action;
     * @param callbackContext: callback function success and error;
     * @return: boolean as true if action found and called and false else;
     */
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        switch (action) {
            case "checkForUpdate":
                checkForUpdate(args.getString(0), callbackContext);
                return true;
        }
        return super.execute(action, args, callbackContext);
    }


    /*
     * Interface for js to get location update using google play service;
     * @param: interval => an integer refer to interval of getting location update;
     * @param: fastestUpdateInterval =>  the fastest rate in milliseconds at which your app can handle location updates;
     * @return: plugin result not a success callback to remain the listener;
     */
    private void checkForUpdate(String url, CallbackContext callbackContext) throws JSONException {
        new CheckForUpdate().execute(url);
        // Save instance of callback
        updateCallback = callbackContext;

    }


    private class CheckForUpdate extends AsyncTask<String,Void,Boolean> {

        private int versionCode;
        private boolean updateFlag;

        @Override
        protected Boolean doInBackground(String... args) {

            try {
                URL url = new URL(args[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                br.close();
                Log.i("Version",sb.toString());
                JSONObject jsonObject = new JSONObject(sb.toString());
                versionCode = jsonObject.getInt("version");
                updateFlag = jsonObject.getBoolean("update-flag");
                return checkVersionCode(versionCode);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPreExecute() {
            //push state
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
                if(updateFlag){
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, true);
                    pluginResult.setKeepCallback(false); // keep callback
                    updateCallback.sendPluginResult(pluginResult);
                    PackageManager pm = cordova.getActivity().getPackageManager();
                    try {
                        PackageInfo pi = pm.getPackageInfo(cordova.getActivity().getPackageName(), 0);
                        final String appPackageName = pi.packageName; // getPackageName() from Context or Activity object
                        try {
                            cordova.getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            cordova.getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    //popup for ask of update
                    showUpdateDialog();
                }

            } else {
                //No update available
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, false);
                pluginResult.setKeepCallback(false); // keep callback
                updateCallback.sendPluginResult(pluginResult);
            }
        }
    }

    private void showUpdateDialog(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(cordova.getActivity());
        builder.setTitle("Application Update");
        builder.setMessage("New update is available click ok to install new update");

        final PackageManager pm = cordova.getActivity().getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(cordova.getActivity().getPackageName(), 0);
            builder.setIcon(pm.getApplicationIcon(pi.packageName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, true);
                pluginResult.setKeepCallback(false); // keep callback
                updateCallback.sendPluginResult(pluginResult);
                PackageInfo pi = null;
                try {
                    pi = pm.getPackageInfo(cordova.getActivity().getPackageName(), 0);
                    final String appPackageName = pi.packageName; // getPackageName() from Context or Activity object
                    try {
                        cordova.getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        cordova.getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, false);
                pluginResult.setKeepCallback(false); // keep callback
                updateCallback.sendPluginResult(pluginResult);

                //Complete task
                dialog.dismiss();

            }
        });

        builder.setCancelable(false);

        builder.show();

    }

    private boolean checkVersionCode(int code){
        PackageManager pm = cordova.getActivity().getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(cordova.getActivity().getPackageName(), 0);
            if(code > pi.versionCode){
                return true;
            } else {
                //No update is available
                return false;
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }



}


