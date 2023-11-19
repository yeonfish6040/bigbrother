package com.yeonfish.bigbrother.ui.updatePos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.yeonfish.bigbrother.R;
import com.yeonfish.bigbrother.databinding.FragmentUpdateposBinding;
import com.yeonfish.bigbrother.util.sql.SQLQuery;
import com.yeonfish.bigbrother.util.sql.SQLResults;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UpdatePosFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

    private LatLng loc;

    private FragmentUpdateposBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        UpdatePosViewModel updatePosViewModel =
                new ViewModelProvider(this).get(UpdatePosViewModel.class);

        binding = FragmentUpdateposBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textUpdatepos;
        updatePosViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onMapReady(mMap);
            }
        });

        return root;
    }

    @Override
    public void onMapReady(GoogleMap googleMap)  {
        new Thread(() -> {
            SQLQuery sqlQuery = new SQLQuery("lyj.kr", "3306", "android", "f60ed56a9c8275894022fe5a7a1625c33bdb55b729bb4e38962af4d1613eda25", "android");
            if (!sqlQuery.cStatus()) {
                toastOnThread(getActivity(), "Fail1", Toast.LENGTH_LONG);
                return;
            }
            SQLResults results = null;
            try {
                results = sqlQuery.query("SELECT * FROM `MyPos` ORDER BY `time` DESC");
            } catch (Exception e) {
                toastOnThread(getActivity(), "Fail2", Toast.LENGTH_LONG);
                throw new RuntimeException(e);
            }
            toastOnThread(getActivity(), "Success", Toast.LENGTH_SHORT);
            Log.i("SQL RESULT", results.getJSON());

            Handler handler = new Handler(Looper.getMainLooper());
            SQLResults finalResults = results;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    List<List<String>> result = finalResults.getList();
                    result.remove(0);
                    String nowAddr ="현재 위치를 확인 할 수 없습니다.";
                    List<Address> address;
                    Long time = Long.parseLong(result.get(0).get(1));
                    Double lat = Double.parseDouble(result.get(0).get(2));
                    Double lng = Double.parseDouble(result.get(0).get(3));

                    Geocoder geocoder = new Geocoder(getActivity(), Locale.KOREA);
                    try {
                        address = geocoder.getFromLocation(lat, lng, 1);
                        if (address != null && address.size() > 0) {
                            nowAddr = address.get(0).getAddressLine(0).toString();
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    binding.currentLocation.setText(nowAddr);

                    mMap = googleMap;
                    mMap.clear();
                    loc = new LatLng(lat, lng);
                    mMap.addMarker(new MarkerOptions()
                            .position(loc)
                            .title(new SimpleDateFormat("MM-dd_HH:mm:ss").format(new Date(time)))
                            .icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.marker), 96, 96, false))));
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(loc, 18.0F, mMap.getCameraPosition().tilt, mMap.getCameraPosition().bearing)), 1000, null);

                    result.remove(0);
                    result.forEach((e) -> {
                        PolylineOptions options = new PolylineOptions()
                                .color(getContext().getColor(R.color.skyblue))
                                .width(2.0f)
                                .add(loc);
                        loc = new LatLng(Double.parseDouble(e.get(2)), Double.parseDouble(e.get(3)));
                        options.add(loc);
                        mMap.addMarker(new MarkerOptions()
                                .position(loc)
                                .title(new SimpleDateFormat("MM-dd_HH:mm:ss").format(new Date(Long.parseLong(e.get(1)))))
                                .icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.marker), 48, 48, false))));
                        mMap.addPolyline(options);
                    });
                }
            }, 0);

        }).start();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    protected void toastOnThread(Context context, String val, int duration) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, val, duration).show();
            }
        }, 0);
    }
}