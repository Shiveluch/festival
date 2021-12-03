package com.shiveluch.festival;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Badgeable;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import static com.shiveluch.festival.Refactor.refact;
import static com.shiveluch.festival.Refactor.defact;
import static com.shiveluch.festival.Refactor.stringToKey;

import javax.crypto.SecretKey;

public class MainActivity extends AppCompatActivity {
    public final static String DOMAIN="http://a0568345.xsph.ru/fest/";
    private Drawer.Result drawerResult = null;
    TextView textheader, titlet;
    Toolbar toolbar;
    Context context;
    Activity activity;
    SecretKey api;
    ListView locationslist, locationinfo, eventlist, eventinfo;
    LinearLayout locationLL, eventsLL;
    Button createnewlocation, createnewevent;
    String getLocations="http://a0568345.xsph.ru/fest/getlocations.php";
    String getEvents="http://a0568345.xsph.ru/fest/getevents.php";
    String getLocationInfo, getEventInfo;
    String locationID, eventID;
    ImageView uploadmap;
    String [] tempPoligon=new String[4];
    String mapImageRes;
    String currentevent="";
    String titleinfo;


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        // Закрываем Navigation Drawer по нажатию системной кнопки "Назад" если он открыт
        if (drawerResult.isDrawerOpen()) {
            drawerResult.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       toolbar = (Toolbar) findViewById(R.id.toolbar);
       context=getApplicationContext();
       activity=this;
        api = stringToKey(getResources().getString(R.string.api));
       NavPanel();
       initVisualElements();
        getJSON(getLocations);
    }

    private void initVisualElements() {
        locationslist=findViewById(R.id.location);
        locationinfo = findViewById(R.id.locationinfo);
        eventlist=findViewById(R.id.event);
        eventinfo = findViewById(R.id.eventinfo);
        locationLL=findViewById(R.id.locationLL);
        eventsLL=findViewById(R.id.eventLL);
        createnewlocation=findViewById(R.id.createnewloaction);
        createnewevent=findViewById(R.id.createnewevent);
        locationLL.setVisibility(View.GONE);
        eventsLL.setVisibility(View.GONE);
        textheader=findViewById(R.id.textheader);
        textheader.setText("ОТКРЫТЬ МЕНЮ");
        titlet=findViewById(R.id.titlet);



eventinfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        TextView isField=view.findViewById(R.id.field);
        TextView isName=view.findViewById(R.id.name);
        TextView isInfo=view.findViewById(R.id.info);
        String getField=isField.getText().toString();
        String getName=isName.getText().toString();
        String getInfo=isInfo.getText().toString();
        if (position==5) showUploadMapDialog(mapImageRes);
        if (position!=5 && position!=3)
        {
            showChangeEventInfoDialog(getField, getName, getInfo);
        }

    }
});


        eventlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView isID=view.findViewById(R.id.locationid);
                eventID=isID.getText().toString();
                TextView isEvent=view.findViewById(R.id.locationname);
                currentevent=isEvent.getText().toString();
                titlet.setText(currentevent);
                getEventInfo="http://a0568345.xsph.ru/fest/geteventinfo.php/get.php?nom="+eventID;
                getJSON(getEventInfo);
            }
        });

        createnewlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new uploadAsyncTask().execute("addlocation","","","","");
                getJSON(getLocations);
            }
        });

        createnewevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new uploadAsyncTask().execute("addevent","","","","");
                getJSON(getEvents);
            }
        });

        locationinfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView isField=view.findViewById(R.id.field);
                TextView isName=view.findViewById(R.id.name);
                TextView isInfo=view.findViewById(R.id.info);
                String getField=isField.getText().toString();
                String getName=isName.getText().toString();
                String getInfo=isInfo.getText().toString();

                showDialog(getField, getName, getInfo );
            }
        });

        locationslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView isID=view.findViewById(R.id.locationid);
                locationID=isID.getText().toString();
                TextView isLocation=view.findViewById(R.id.locationname);
                String locationName=isLocation.getText().toString();
                getLocationInfo = "http://a0568345.xsph.ru/fest/getlocationinfo.php/get.php?nom="+locationID;
                getJSON(getLocationInfo);
                titlet.setText(locationName);
            }
        });

locationslist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        TextView isID=view.findViewById(R.id.locationid);
        String getID=isID.getText().toString();
        new uploadAsyncTask().execute("dellocation",getID,"","","");
        getJSON(getLocations);
        return false;

    }
});
    }

    private void NavPanel()
    {


        Drawer.Result build = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_free_play).withIcon(FontAwesome.Icon.faw_map_marker),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_home).withIcon(getResources().getDrawable( R.drawable.header)).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_locations).withIcon(FontAwesome.Icon.faw_compass),
                        new SectionDrawerItem().withName(R.string.drawer_item_settings),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_help).withIcon(FontAwesome.Icon.faw_question),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_contact).withIcon(FontAwesome.Icon.faw_phone).withIdentifier(1)
                )
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
//                        if (drawerView!=null) {
//                            InputMethodManager inputMethodManager = (InputMethodManager) MainActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
//                            if (inputMethodManager != null) {
//                                inputMethodManager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), 0);
//                            }
//                        }
                    }
                    @Override
                    public void onDrawerClosed(View drawerView) {
                    }
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    // Обработка клика
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem instanceof Nameable) {
                            if (position==1)
                            {
                                locationLL.setVisibility(View.VISIBLE);
                                eventsLL.setVisibility(View.GONE);
                                getJSON(getLocations);
                                Log.d ("data", getLocations);
                                textheader.setText("МЕСТА ПРОВЕДЕНИЯ");


                            }

                            if (position==2)
                            {
                                locationLL.setVisibility(View.GONE);
                                eventsLL.setVisibility(View.VISIBLE);
                                getJSON(getEvents);
                                textheader.setText("МЕРОПРИЯТИЯ");
                            }


                           // Toast.makeText(MainActivity.this, MainActivity.this.getString(((Nameable) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                        }
//                        if (drawerItem instanceof Badgeable) {
//                            Badgeable badgeable = (Badgeable) drawerItem;
//                            if (badgeable.getBadge() != null) {
//                                // учтите, не делайте так, если ваш бейдж содержит символ "+"
//                                try {
//                                    int badge = Integer.valueOf(badgeable.getBadge());
//                                    if (badge > 0) {
//                                        drawerResult.updateBadge(String.valueOf(badge - 1), position);
//                                    }
//                                } catch (Exception e) {
//                                    Log.d("test", "Не нажимайте на бейдж, содержащий плюс! :)");
//                                }
//                            }
//                        }
                    }
                })
                .withOnDrawerItemLongClickListener(new Drawer.OnDrawerItemLongClickListener() {
                    @Override
                    // Обработка длинного клика, например, только для SecondaryDrawerItem
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem instanceof SecondaryDrawerItem) {
                            Toast.makeText(MainActivity.this, MainActivity.this.getString(((SecondaryDrawerItem) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                })
                .build();


    }

    private void sendToast(String message)
    {
        Toast.makeText(this, message,Toast.LENGTH_LONG).show();
    }

    private class uploadAsyncTask extends AsyncTask<String, Integer, Double> {
        @Override
        protected Double doInBackground(String... params) {
            postData(params[0], params[1], params[2], params[3], params[4]);
            return null;
        }

        protected void onPostExecute(Double result) {
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        public void postData(String isUri, String post1, String post2, String post3, String post4) {
            HttpClient httpclient = new DefaultHttpClient();
            String getUri = String.format(DOMAIN+ "%s.php", isUri);
            HttpPost httppost = new HttpPost(getUri);
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("post1", post1));
                nameValuePairs.add(new BasicNameValuePair("post2", post2));
                nameValuePairs.add(new BasicNameValuePair("post3", post3));
                nameValuePairs.add(new BasicNameValuePair("post4", post4));
//                nameValuePairs.add(new BasicNameValuePair("event",event ));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                HttpResponse response = httpclient.execute(httppost);
                sOut(getUri+", "+post1+", "+post2+", "+post3);


//                if (getUri.contains("updaterole") && post4.equals("update"))
//                {
//                    new uploadAsyncTask().execute("updateplayerfromrole", getCharID, post2,"","");
//                }




            } catch (ClientProtocolException e) {

            } catch (IOException e) {
            }

        }
    }
    public void sOut(String message)
    {
        System.out.println(message);
    }

    public void getJSON(final String urlWebService) {
        class GetJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("dataJSON", "s is: "+s);

                if (s != null) {
                    try {

                        if (urlWebService == getLocations) {
                            loadLocations(s);

                        }

                        if (urlWebService == getEvents) {
                            loadEvents(s);

                        }
                        if (urlWebService == getLocationInfo) {
                            loadLocationInfo(s);

                        }

                        if (urlWebService == getEventInfo) {
                            loadEventInfo(s);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }


            @Override
            protected String doInBackground(Void... voids) {


                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }

                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }

            }
        }


        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }

    private void loadLocationInfo(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        PoligonInfoAdapter poligonInfoAdapter;
        if (jsonArray.length() == 0) { Log.d ("data", "No data");
            return;
        }
        ArrayList<PoligonInfo> poligonInfo = new ArrayList<PoligonInfo>();

        if (jsonArray.length() > 0) {
            JSONObject obj = jsonArray.getJSONObject(0);

            Log.d ("data", "Is data");
            poligonInfo.add(new PoligonInfo("name", "Название полигона", obj.getString("field1")));
            poligonInfo.add(new PoligonInfo("description", "Описание", obj.getString("field2")));
            poligonInfo.add(new PoligonInfo("link", "Ссылка", obj.getString("field3")));
            }
            poligonInfoAdapter = new PoligonInfoAdapter(this, poligonInfo, this);
            locationinfo.setAdapter(poligonInfoAdapter);



    }

    private void loadEventInfo(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        PoligonInfoAdapter poligonInfoAdapter;
        if (jsonArray.length() == 0) { Log.d ("data", "No data");
            return;
        }
        ArrayList<PoligonInfo> poligonInfo = new ArrayList<PoligonInfo>();

        if (jsonArray.length() > 0) {
            JSONObject obj = jsonArray.getJSONObject(0);

            Log.d ("data", "Is data");
            poligonInfo.add(new PoligonInfo("name", "Название мероприятия", obj.getString("field1")));
            poligonInfo.add(new PoligonInfo("time", "Дата и время начала", obj.getString("field2")));
            poligonInfo.add(new PoligonInfo("description", "Описание", obj.getString("field3")));
            poligonInfo.add(new PoligonInfo("poligon", "Место проведения:", obj.getString("field5")));
            tempPoligon[0] = obj.getString("field4");
            tempPoligon[1] = obj.getString("field5");
            tempPoligon[2] = obj.getString("field6");
            tempPoligon[3] = obj.getString("field7");
            poligonInfo.add(new PoligonInfo("contacts", "Контакт организатора: ", DTS(obj.getString("field8"))));
            poligonInfo.add(new PoligonInfo("image", "КАРТА ", ""));

            mapImageRes=obj.getString("field9");

        }
        poligonInfoAdapter = new PoligonInfoAdapter(this, poligonInfo, this);
        eventinfo.setAdapter(poligonInfoAdapter);



    }


    private void loadLocations(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        Log.d("Array", "Array is: "+jsonArray);
        PoligonsAdapter poligonsAdapter;
        if (jsonArray.length() == 0) { Log.d ("data", "No data");
            return;
        }
        String[] poligon_s = new String[jsonArray.length()];
        String[] id_s = new String[jsonArray.length()];

        ArrayList<Poligons> poligons = new ArrayList<Poligons>();

        if (jsonArray.length() > 0) {
           Log.d ("data", "Is data");
            for (int i = 0; i < jsonArray.length(); i++) {
//
                JSONObject obj = jsonArray.getJSONObject(i);
                poligon_s[i] = obj.getString("field2");
                id_s[i] = obj.getString("field1");
                poligons.add(new Poligons(poligon_s[i],id_s[i] ));
                Log.d("Poligons", poligon_s[i]);
            }
            poligonsAdapter = new PoligonsAdapter(this, poligons, this);
            locationslist.setAdapter(poligonsAdapter);
        }


    }


    private void loadEvents(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        PoligonsAdapter poligonsAdapter;
        if (jsonArray.length() == 0) { Log.d ("data", "No data");
            return;
        }
        String[] event_s = new String[jsonArray.length()];
        String[] id_s = new String[jsonArray.length()];

        ArrayList<Poligons> poligons = new ArrayList<Poligons>();

        if (jsonArray.length() > 0) {
            Log.d ("data", "Is data");
            for (int i = 0; i < jsonArray.length(); i++) {
//
                JSONObject obj = jsonArray.getJSONObject(i);
                event_s[i] = obj.getString("field2");
                id_s[i] = obj.getString("field1");
                poligons.add(new Poligons(event_s[i],id_s[i] ));
            }
            poligonsAdapter = new PoligonsAdapter(this, poligons, this);
            eventlist.setAdapter(poligonsAdapter);
        }


    }


    AlertDialog.Builder alert;
    AlertDialog dialog;
    Uri uri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_CODE && resultCode==RESULT_OK && data!=null && data.getData()!=null)
            uri = data.getData();
        try {
            Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            uploadmap.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    int REQUEST_CODE=1;

    private void showUploadMapDialog(String link )
    {

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
        {
            alert=new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        }
        else
        {
            alert=new AlertDialog.Builder(this);
        }
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.uploadmapdialog,null);
        LinearLayout approve, dismiss, upload;
        uploadmap = view.findViewById(R.id.uploadmap);
        Picasso.with(context).load(link).into(uploadmap);
        dismiss=view.findViewById(R.id.dismiss);
        approve=view.findViewById(R.id.approve);
        upload=view.findViewById(R.id.upload);

        alert.setView(view);
        alert.setCancelable(false);
        // mess.setText("Удалить сообщение?");
        dialog = alert.create();

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select picture"),REQUEST_CODE);

            }
        });



        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {dialog.dismiss();}
        });

        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new uploadAsyncTask().execute("updatelocation", field, changedata.getText().toString(),locationID,"" );
//                getLocationInfo = "http://a0568345.xsph.ru/fest/getlocationinfo.php/get.php?nom="+locationID;
//                getJSON(getLocationInfo);
//                getJSON(getLocations);
               dialog.dismiss();


            }
        });

        dialog.show();
    }


    private void showDialog(String field, String name, String info)
    {

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
        {
            alert=new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        }
        else
        {
            alert=new AlertDialog.Builder(this);
        }
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.changedatadialog,null);
        LinearLayout approve, dismiss;
        EditText changedata=view.findViewById(R.id.changeinfo);
        changedata.setText(info);
        dismiss=view.findViewById(R.id.dismiss);
        approve=view.findViewById(R.id.approve);

        alert.setView(view);
        alert.setCancelable(false);
        // mess.setText("Удалить сообщение?");
        dialog = alert.create();

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {dialog.dismiss();}
        });

        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new uploadAsyncTask().execute("updatelocation", field, changedata.getText().toString(),locationID,"" );
                getLocationInfo = "http://a0568345.xsph.ru/fest/getlocationinfo.php/get.php?nom="+locationID;
                getJSON(getLocationInfo);
                getJSON(getLocations);
                dialog.dismiss();


            }
        });

        dialog.show();
    }

    private void showChangeEventInfoDialog(String field, String name, String info)
    {

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
        {
            alert=new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        }
        else
        {
            alert=new AlertDialog.Builder(this);
        }
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.changedatadialog,null);
        LinearLayout approve, dismiss;
        EditText changedata=view.findViewById(R.id.changeinfo);
        changedata.setText(info);
        dismiss=view.findViewById(R.id.dismiss);
        approve=view.findViewById(R.id.approve);

        alert.setView(view);
        alert.setCancelable(false);
        // mess.setText("Удалить сообщение?");
        dialog = alert.create();

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {dialog.dismiss();}
        });

        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SecretKey api = stringToKey(getResources().getString(R.string.api));
                try {

                    new uploadAsyncTask().execute("updateevent", field, "#"+RTS(changedata.getText().toString()),eventID,"" );
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getEventInfo = "http://a0568345.xsph.ru/fest/geteventinfo.php/get.php?nom="+eventID;
                getJSON(getEventInfo);
                sOut(getEventInfo);
                getJSON(getEvents);
                dialog.dismiss();


            }
        });

        dialog.show();
    }

    private String  RTS (String s) throws Exception {   byte[] encrypted = new byte[0];
        encrypted = refact(s,api);
        String rts = Base64.encodeToString(encrypted, Base64.DEFAULT);
        return rts;
    }

    private String  DTS (String s)
    {
        String dts = null;
        try {
            dts = defact(Base64.decode(s, Base64.DEFAULT), api);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dts;
    }

}