package com.example.microsoftproject;

import static com.example.microsoftproject.service.NetworkServiceClass.URL_BASE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.microsoftproject.adapter.ImageAdapter;
import com.example.microsoftproject.entity.ImageEntity;
import com.example.microsoftproject.service.NetworkServiceClass;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity {

    private Toolbar toolbar;
    RecyclerView recyclerView;
    ImageAdapter adapter;
    FloatingActionButton fab;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEditor;
    String phoneNo;
    List<ImageEntity> imageList;
    boolean flag;
    public static final String PHONE_KEY = "phoneNo";
    public static final String SHARED_PREF_NAME = "user_detail";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        myEditor = sharedPreferences.edit();

        imageList = new ArrayList<>();

        //fetch phoneNo
        if (getIntent().hasExtra(PHONE_KEY)) {
            phoneNo = getIntent().getStringExtra(PHONE_KEY);
            savePhoneNo();
        } else {
            phoneNo = getPhoneNo(PHONE_KEY);
        }

        getAllImage(phoneNo);

        flag = false;

        toolbar = findViewById(R.id.main_toolbar);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ImageAdapter(this, imageList);
        recyclerView.setAdapter(adapter);
        setSupportActionBar(toolbar);
        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NetworkServiceClass().uploadImage(getApplicationContext() , phoneNo , getStringImage(generateNoteOnSD(getApplicationContext() , "a_1.png")));
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.LogOut:
                Toast.makeText(this, "LogOut successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this , MainActivity.class);
                myEditor.putString(PHONE_KEY,null);
                myEditor.commit();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.appbar, menu);
        return true;
    }


    public void savePhoneNo () {
        myEditor.putString("phoneNo", phoneNo);
        myEditor.commit();
    }

    String getPhoneNo (String key){
        String phoneNo = sharedPreferences.getString(key, "");
        return phoneNo;
    }

    @Override
    public void onBackPressed () {


        if (flag == false) {
            flag = true;
            Toast.makeText(this, "Press again for exit", Toast.LENGTH_SHORT).show();
        } else super.onBackPressed();
    }

    public void getAllImage (String phoneNo){
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(URL_BASE + "/user/" + phoneNo + "/drive",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            imageList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject object = response.getJSONObject(i);
                                ImageEntity entity = new ImageEntity();
                                entity.setName(object.getString("name"));
                                entity.setSize(object.getLong("size"));

                                imageList.add(entity);
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("my", error.toString());
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    public File generateNoteOnSD(Context context, String sFileName) {
        File res = null;
        File root = new File(context.getFilesDir(),"mydir");
        if (!root.exists()) {
            root.mkdirs();
        }
        res = new File(root, sFileName);

        Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        return res;
    }

    public String getStringImage(File image){

        String filePath = image.getPath();
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);

        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG ,100,ba);
        byte[] imageByte = ba.toByteArray();
        String encode =  Base64.encodeToString(imageByte , Base64.NO_WRAP);

        return encode;
    }
}