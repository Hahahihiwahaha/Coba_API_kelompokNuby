package com.example.coba_api;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    EditText etAsal, etTujuan, etBerat;
    Spinner spExpedition;
    Button btnCek;
    TextView tvHasil;

    String API_KEY = "PYrJJvQwRDbG0orbq1fqQa9JAncbq090SDMrkvm7eBrJQRqaBa";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etAsal = findViewById(R.id.etAsal);
        etTujuan = findViewById(R.id.etTujuan);
        etBerat = findViewById(R.id.etBerat);
        spExpedition = findViewById(R.id.spExpedition);
        btnCek = findViewById(R.id.btnCek);
        tvHasil = findViewById(R.id.tvHasil);

        String[] expeditions = {"JNE", "JNECargo", "SiCepat", "JT"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                expeditions
        );
        spExpedition.setAdapter(adapter);

        btnCek.setOnClickListener(v -> cekOngkir());
    }

    private void cekOngkir() {

        String asal = etAsal.getText().toString();
        String tujuan = etTujuan.getText().toString();
        String beratStr = etBerat.getText().toString();
        String expedition = spExpedition.getSelectedItem().toString();

        if (asal.isEmpty() || tujuan.isEmpty() || beratStr.isEmpty()) {
            tvHasil.setText("Semua field harus diisi!");
            return;
        }

        int berat = Integer.parseInt(beratStr);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://use.api.co.id/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService api = retrofit.create(ApiService.class);

        Call<ResponseBody> call = api.getShippingCost(
                API_KEY,
                asal,
                tujuan,
                berat,
                expedition
        );

        tvHasil.setText("Loading...");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {

                try {

                    if (!response.isSuccessful()) {
                        tvHasil.setText(response.errorBody().string());
                        return;
                    }

                    String result = response.body().string();
                    JSONObject json = new JSONObject(result);

                    JSONObject data = json.getJSONObject("data");

                    //String originCode = data.getString("origin_village_code");
                    //String destinationCode = data.getString("destination_village_code");

                    /*String originName = data.getString("origin_village_code");
                    String destinationName = data.getString("origin_village_code");

                    JSONArray costs = data.getJSONArray("price");
                    JSONObject cost = costs.getJSONObject(0);

                    String expeditionName = cost.getString("expedition");
                    int price = cost.getInt("price");

                    tvHasil.setText(
                            "Kota Asal: " + originName +
                                    "\nKota Tujuan: " + destinationName +
                                    "\nEkspedisi: " + expeditionName +
                                    "\nBerat: " + berat + " kg" +
                                    "\nBiaya: Rp " + price
                    );*/

                    JSONArray couriers = data.getJSONArray("couriers");
                    String ekspe = spExpedition.getSelectedItem().toString();
                    //contoh 3172051003 3204282004
                    for (int i = 0; i < couriers.length(); i++) {

                        JSONObject courier = couriers.getJSONObject(i);

                        if (ekspe.equalsIgnoreCase(courier.getString("courier_code"))) {
                            System.out.println("Harga: " + courier.getInt("price"));

                            tvHasil.setText("Harga: " + courier.getInt("price"));
                            break; // berhenti kalau sudah ketemu
                        }
                        else{
                            tvHasil.setText("Harga: N/A");
                        }
                    }

                } catch (Exception e) {
                    tvHasil.setText("Parsing Error: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                tvHasil.setText("Failure: " + t.getMessage());
            }
        });
    }
}