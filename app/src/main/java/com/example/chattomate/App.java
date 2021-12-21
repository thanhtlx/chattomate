package com.example.chattomate;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.example.chattomate.call.util.QBResRequestExecutor;
import com.example.chattomate.database.AppPreferenceManager;
import com.example.chattomate.interfaces.APICallBack;
import com.example.chattomate.service.API;
import com.example.chattomate.socket.SocketController;
import com.example.chattomate.socket.SocketService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.quickblox.auth.session.QBSettings;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static com.example.chattomate.service.FCMService.R_FB_T_URL;

public class App  extends Application {
    private static App instance;
    private static SocketService socket;
    private static final String APPLICATION_ID = "94399";
    private static final String AUTH_KEY = "uZF6qPmTtMAyvse";
    private static final String AUTH_SECRET = "CzG9O9wqD2KRtPq";
    private static final String ACCOUNT_KEY = "xvm8kgb2G2hgGZ93y3DG";

    public static final String USER_DEFAULT_PASSWORD = "chattomate-qb-secert";
    private QBResRequestExecutor qbResRequestExecutor;
    static Calendar calendar = Calendar.getInstance();

    public static String getTimeStamp(String dateStr) {
        String today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String timestamp = "";

        today = today.length() < 2 ? "0" + today : today;

        try {
            Date date = format.parse(dateStr);
            SimpleDateFormat todayFormat = new SimpleDateFormat("dd");

            String dateToday = todayFormat.format(date);
            format = dateToday.equals(today) ? new SimpleDateFormat("hh:mm a") : new SimpleDateFormat("dd LLL, hh:mm a");
            String date1 = format.format(date);
            timestamp = date1.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timestamp;
    }
    private static AppPreferenceManager manager;

    @Override
    public void onCreate() {
        super.onCreate();
        initApplication();
        manager = new AppPreferenceManager(instance.getApplicationContext());
        if (manager.getUser() != null)
        socket = new SocketService(instance);
        Log.d("DEBUG"," start application");
        initFCM();
        initCredentials();
    }
    private void initCredentials() {
        QBSettings.getInstance().init(getApplicationContext(), APPLICATION_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);
    }
    public synchronized QBResRequestExecutor getQbResRequestExecutor() {
        return qbResRequestExecutor == null
                ? qbResRequestExecutor = new QBResRequestExecutor()
                : qbResRequestExecutor;
    }

    public  static void  initSocket() {
        socket = new SocketService(instance);
        initFCM();
    }



    private static void initFCM() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        Log.d("DEBUG-new token", task.getResult());
                        if (!manager.getFBToken().equals(task.getResult()) && manager.getUser() != null) {
                            manager.setFBToken(task.getResult());
                            regisFBTK(task.getResult());
                            Log.d("DEBUG-new token", "r g t k");
                        }
                    }
                });
    }

    private static void regisFBTK(String token) {
        API api = new API(instance);
        JSONObject data = new JSONObject();
        try {
            data.put("fcm_token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Map<String, String> auth_token = new HashMap<>();
        auth_token.put("auth-token", manager.getToken(instance));
        api.Call(Request.Method.POST, R_FB_T_URL, data, auth_token, new APICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.d("DEBUG-new token", " s r g t k");
            }

            @Override
            public void onError(JSONObject result) {
                Log.d("DEBUG-new token", " f r g t k");
                Log.d("DEBUG-new token", String.valueOf(result));
            }
        });
    }

    private void initApplication() {
        instance = this;
    }

    public static App getInstance() {
        return instance;
    }

    public  SocketService getSocket(){
        return  socket;
    }
}
