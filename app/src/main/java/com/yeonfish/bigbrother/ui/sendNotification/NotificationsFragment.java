package com.yeonfish.bigbrother.ui.sendNotification;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.yeonfish.bigbrother.databinding.FragmentSendNotificationBinding;
import com.yeonfish.bigbrother.util.HttpUtil;
import com.yeonfish.bigbrother.util.sql.SQLQuery;
import com.yeonfish.bigbrother.util.sql.SQLResults;

import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicReference;

public class NotificationsFragment extends Fragment {

    private FragmentSendNotificationBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentSendNotificationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSendNotifications;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(() -> {
                    SQLResults results;
                    try {
                        SQLQuery sqlQuery = new SQLQuery("lyj.kr", "3306", "android", "f60ed56a9c8275894022fe5a7a1625c33bdb55b729bb4e38962af4d1613eda25", "android");
                        results = sqlQuery.query("SELECT * FROM `FCMToken` WHERE `id`=1 LIMIT 1");
                        String to = results.getList().get(1).get(1);
                        Log.d("SQL", to);

                        JSONObject frame = new JSONObject();
                        JSONObject data = new JSONObject();
                        JSONObject body = new JSONObject();
                        data.put("event", "notification");

                        body.put("title", binding.title.getText().toString());
                        body.put("text", binding.body.getText().toString());

                        data.put("body", body.toString());
                        frame.put("data", data);
                        frame.put("to", to);

                        Log.d("DATA", frame.toString());
                        HttpUtil http = HttpUtil.getInstance();
                        String[][] property = {{"Authorization", "key=AAAAuwcjd70:APA91bFFfREJKEafWpYJtLDLTmYVM6CQdyj12eM9aFANNSDl69KPR6jyYN6VW8ixJPxFgiS8VJdYtp-6p2QiV6DStWyRYlIrhrY-hAo4_iB0YUzs9i4mmax2Yq0xXPVpPEOoYetrLo4w"}};
                        http.post("https://fcm.googleapis.com/fcm/send", frame.toString(), property);
                    }catch (Exception e) { e.printStackTrace(); throw new RuntimeException(e.getLocalizedMessage()); }
                }).start();
            }
        });

        return root;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}