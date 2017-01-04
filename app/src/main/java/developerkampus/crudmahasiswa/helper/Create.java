package developerkampus.crudmahasiswa.helper;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import developerkampus.crudmahasiswa.MainActivity;

/**
 * Created by Dodi Rivaldi on 02/01/2017.
 */

public class Create extends AsyncTask<String, Void, String > {

    MainActivity mainActivity = new MainActivity();

    private Context context;
    private String link = Koneksi.server;
    public Create(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {    }

    @Override
    protected String doInBackground(String... arg0) {
        String nim = arg0[0];
        String nama = arg0[1];
        String alamat = arg0[2];

        String data;
        BufferedReader bufferedReader;
        String result;
        try {
            data = "?nim"+ URLEncoder.encode(nim,"UTF-8");
            data += "&nama"+URLEncoder.encode(nama,"UTF-8");
            data += "&tgl_lahir"+URLEncoder.encode(mainActivity.TGL_LAHIR,"UTF-8");
            data += "&prodi_id"+URLEncoder.encode(mainActivity.PRODI_ID,"UTF-8");
            data += "&alamat"+URLEncoder.encode(alamat,"UTF-8");

            link = link+"create.php"+data;
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            result = bufferedReader.readLine();
            return result;
        }
        catch (Exception e){
            return new String("Exception "+e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(String s) {
        String jsonString = s;
        if (jsonString != null){
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                String query_result = jsonObject.getString("result");
                if (query_result.equals("berhasil")){
                    Toast.makeText(context,"Data mahasiswa berhasil disimpan",Toast.LENGTH_SHORT).show();
                    Toast.makeText(context,"Refresh untuk memuat ulang",Toast.LENGTH_SHORT).show();
                    ((Activity)(context)).finish();
                }

                else if (query_result.equals("gagal")){
                    Toast.makeText(context,query_result, Toast.LENGTH_SHORT).show();
                }

                else {
                    Log.e("result", query_result);
                }
            }

            catch (JSONException e){
                e.printStackTrace();
            }
        }

        else {
            Log.e("Error","Tidak berhasil mendapatkan data");
        }
    }
}
