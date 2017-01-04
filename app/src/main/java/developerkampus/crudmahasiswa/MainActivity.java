package developerkampus.crudmahasiswa;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import developerkampus.crudmahasiswa.helper.Koneksi;

public class MainActivity extends AppCompatActivity {

    public static String MAHASISWA_ID   = "";
    public static String TGL_LAHIR      = "";
    public static String PRODI_ID       = "";
    public static String STATUS_PRODI   = "";

    Context context;

    int closeapp = 2;

    private String jsonResult;
    private String url = Koneksi.server + "read.php";
    public static ListView list_mahasiswa;

    ListAdapter simpleAdapter;
    public static ArrayList<HashMap<String, String>> mhs = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list_mahasiswa = (ListView) findViewById(R.id.list_mahasiswa);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        accessWebService();

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOnline()==true){
                    startActivity(new Intent(ReadActivity.this, CreateActivity.class));
                } else {
                    Snack("No internet connection");
                }
            }
        });*/
    }

    private void Msg(View view){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Delete");
        builder1.setMessage("Do you really want to delete?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                       // Delete();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
/*
    public void MenuDialog(){
        final View view = null;

        List<String> menu = new ArrayList<String>();
        menu.add("Update");
        menu.add("Delete");
        final CharSequence[] menus = menu.toArray(new String[menu.size()]);
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setItems(menus, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                String selectedText = menus[item].toString();
                if (selectedText.equals("Update")) {
                    startActivity(new Intent(ReadActivity.this, UpdateActivity.class));
                } else if (selectedText.equals("Delete")) {
                    Msg(view);
                }
            }
        });
        AlertDialog alertDialogObject = dialogBuilder.create();
        alertDialogObject.show();
    }*/
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            Refresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/
    // build hash set for list view
    public void ListDrawer() {

        try {
            JSONObject jsonResponse = new JSONObject(jsonResult);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("result");

            for (int i = 0; i < jsonMainNode.length(); i++) {

                JSONObject jsonChildNode    = jsonMainNode.getJSONObject(i);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("mahasiswa_id",     jsonChildNode.optString("mahasiswa_id"));
                map.put("nim",     jsonChildNode.optString("nim"));
                map.put("nama",     jsonChildNode.optString("nama"));
                map.put("tgl_lahir",    jsonChildNode.optString("tgl_lahir"));
                map.put("prodi_nama",    jsonChildNode.optString("prodi_nama"));
                map.put("alamat",    jsonChildNode.optString("alamat"));

                mhs.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        simpleAdapter = new SimpleAdapter(this, mhs, R.layout.list_mahasiswa,
                new String[] { "mahasiswa_id", "nim", "nama", "tgl_lahir", "prodi_nama", "alamat"},
                new int[] {R.id.text_mahasiswa_id, R.id.text_nim, R.id.text_nama,
                        R.id.text_tgl_lahir, R.id.text_prodi_nama, R.id.text_alamat});

        list_mahasiswa.setAdapter(simpleAdapter);

        list_mahasiswa.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (isOnline() == true){
                    MAHASISWA_ID = ((TextView) view.findViewById(R.id.text_mahasiswa_id)).getText().toString();
  //                  MenuDialog();
                }
            }
        });
    }
    // Async Task to access the web
    private class JsonReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(params[0]);
            try {
                HttpResponse response = httpclient.execute(httppost);
                jsonResult = inputStreamToString(
                        response.getEntity().getContent()).toString();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(String result) {
            ListDrawer();
        }
    }

    private StringBuilder inputStreamToString(InputStream is) {
        String rLine = "";
        StringBuilder answer = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        try {
            while ((rLine = rd.readLine()) != null) {
                answer.append(rLine);
            }
        }

        catch (IOException e) {
            // e.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    "Error..." + e.toString(), Toast.LENGTH_LONG).show();
        }
        return answer;
    }

    public void accessWebService() {
        JsonReadTask task = new JsonReadTask();
        // passes values for the urls string array
        task.execute(new String[]{url});
    }

    private void Snack(String content){
        Snackbar snackbar = Snackbar.make(list_mahasiswa, content, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        snackbar.show();
    }
/*
    private void Delete(){new
            Delete(this).execute();
        Refresh();
    }
*/
    public  void Refresh(){
        mhs.clear();
        list_mahasiswa.setAdapter(null);
        accessWebService();
        Snack("Memuat ulang data...");
    }

    @Override
    public void onBackPressed() {
        closeapp -= 1;
        switch (closeapp) {
            case 1 : Toast.makeText(getApplication(), "Sekali lagi untuk menutup aplikasi", Toast.LENGTH_LONG).show(); break;
            case 0 : finish(); break;
        }
    }

    // Check internet connection
    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}
