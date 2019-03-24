package com.example.ivanriazantsev.nureschedule;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import api.Main;


public class SettingsFragment extends Fragment {

    View view;
    private Button donation15;
    private Button donation30;
    private Button donation50;
    private String donation15Price;
    private String donation30Price;
    private String donation50Price;
    BillingFlowParams billingFlowParams;

    public List<String> skuList = new ArrayList<>();

    public SettingsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        skuList.add("donation15");
        skuList.add("donation30");
        skuList.add("donation50");
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);

        MainActivity.billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(int responseCode) {
                if (responseCode == BillingClient.BillingResponse.OK) {

                }
            }

            @Override
            public void onBillingServiceDisconnected() {

            }
        });

        donation15 = view.findViewById(R.id.donation15);
        donation30 = view.findViewById(R.id.donation30);
        donation50 = view.findViewById(R.id.donation50);

        return view;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };
}
