package com.example.sebastian.eatup;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Sebastian on 13-01-2017.
 */

public class BackgroundTask extends AsyncTask<String,Void,String>{
    Context ctx;
    BackgroundTask(Context ctx){
        this.ctx=ctx;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        String reg_url = "http://10.0.2.2/android_connect/register.php";
        String login_url = "http://10.0.2.2/android_connect/retrieve.php";
        String sendmessage_url = "https://eatup.000webhostapp.com/sendmessage.php";
        String orderNoti_url = "https://eatup.000webhostapp.com/ordernoti.php";
        String confNoti_url = "https://eatup.000webhostapp.com/confnoti.php";



        String method = params[0];
        if(method.equals("register")){
            String user_id = params[1];
            String user_token = params[2];

            try {
                URL url = new URL(reg_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));

                String data = URLEncoder.encode("user","UTF-8")+"="+URLEncoder.encode(user_id,"UTF-8")+"&"+URLEncoder.encode("token","UTF-8")+"="+URLEncoder.encode(user_token,"UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getErrorStream();
                try {
                    if (IS!= null) IS.close();
                } catch (IOException e) {
                    Log.e("READER.CLOSE()", e.toString());
                }

                return "Registration success";
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if(method.equals("tagsNoti")) {
            ArrayList<String> tags = new ArrayList<String>(Arrays.asList(params[1].substring(1, params[1].length() - 1).split(", ")));

            Gson gson = new Gson();
            Type listOfTestObject = new TypeToken<List<String>>(){}.getType();
            String s = gson.toJson(tags, listOfTestObject);
            Log.d("Hvaderdatamedtype",s);

            try {
                URL url = new URL(sendmessage_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                //String data = URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(tags.get(0), "UTF-8") + "&" + URLEncoder.encode("tag", "UTF-8") + "=" + URLEncoder.encode(tags.get(1), "UTF-8");
                String data="json="+s;
                Log.d("Hvaderdata",data);

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getErrorStream();
                try {
                    if (IS != null) IS.close();
                } catch (IOException e) {
                    Log.e("READER.CLOSE()", e.toString());
                }

                return "Registration success";
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(method.equals("tagsNotiOne")) {
            ArrayList<String> tags = new ArrayList<String>();
            tags.add(params[1]);

            Gson gson = new Gson();
            Type listOfTestObject = new TypeToken<List<String>>(){}.getType();
            String s = gson.toJson(tags, listOfTestObject);
            Log.d("Hvaderdatamedtype",s);

            try {
                URL url = new URL(sendmessage_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                //String data = URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(tags.get(0), "UTF-8") + "&" + URLEncoder.encode("tag", "UTF-8") + "=" + URLEncoder.encode(tags.get(1), "UTF-8");
                String data="json="+s;
                Log.d("Hvaderdata",data);

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getErrorStream();
                try {
                    if (IS != null) IS.close();
                } catch (IOException e) {
                    Log.e("READER.CLOSE()", e.toString());
                }

                return "Registration success";
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(method.equals("orderNoti")) {
            String user_id = params[1];
            String user_token = params[2];
            Log.d("datadata", user_id+ " "+user_token);

            try {
                URL url = new URL(orderNoti_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));

                String data = URLEncoder.encode("user","UTF-8")+"="+URLEncoder.encode(user_id,"UTF-8")+"&"+URLEncoder.encode("token","UTF-8")+"="+URLEncoder.encode(user_token,"UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getErrorStream();
                try {
                    if (IS!= null) IS.close();
                } catch (IOException e) {
                    Log.e("READER.CLOSE()", e.toString());
                }

                return "Registration success";
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(method.equals("confNoti")) {
            String user_id = params[1];
            String user_token = params[2];
            String user_address = params[3];
            Log.d("datadata", user_id+ " "+user_token+" "+user_address);

            try {
                URL url = new URL(confNoti_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));

                String data = URLEncoder.encode("user","UTF-8")+"="+URLEncoder.encode(user_id,"UTF-8")+"&"+URLEncoder.encode("token","UTF-8")+"="+URLEncoder.encode(user_token,"UTF-8")+"&"+URLEncoder.encode("address","UTF-8")+"="+URLEncoder.encode(user_address,"UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getErrorStream();
                try {
                    if (IS!= null) IS.close();
                } catch (IOException e) {
                    Log.e("READER.CLOSE()", e.toString());
                }

                return "Registration success";
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {
    }
}
