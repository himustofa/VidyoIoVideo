package com.quickstart.vidyoiovideo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vidyo.VidyoClient.Connector.ConnectorPkg;
import com.vidyo.VidyoClient.Connector.Connector;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements Connector.IConnect {

    private static final String TAG="MainActivity";
    private Connector vc;
    private FrameLayout videoFrame;
    private String mToken;

    //Runtime Permissions
    private String[] PERMISSIONS = { android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO };
    private PermissionUtility mPermissions;
    private MyNetworkReceiver mNetworkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNetworkReceiver = new MyNetworkReceiver(this);
        mPermissions = new PermissionUtility(this, PERMISSIONS); //Runtime permissions

        ConnectorPkg.setApplicationUIContext(this);
        ConnectorPkg.initialize();
        videoFrame = (FrameLayout)findViewById(R.id.videoFrame);

        getVidyoToken();

        //Runtime Permissions
        if(mPermissions.arePermissionsEnabled()){
            vidyoStart();
            Log.d(TAG, "Permission granted 1");
        } else {
            mPermissions.requestMultiplePermissions();
        }




    }

    //Runtime permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(mPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            vidyoStart();
            Log.d(TAG, "Permission granted 2");
        }
    }

    //===============================================| onStart(), onPause(), onResume(), onStop()
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void vidyoStart() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                vc = new Connector(videoFrame, Connector.ConnectorViewStyle.VIDYO_CONNECTORVIEWSTYLE_Default, 15, "warning info@VidyoClient info@VidyoConnector", "", 0);
                vc.showViewAt(videoFrame, 0, 0, videoFrame.getWidth(), videoFrame.getHeight());
            }
        }, 3000);
    }

    public void Connect(View v) {
        if (mToken != null) {
            vc.connect("prod.vidyo.io", mToken, "DemoUser", "DemoRoom", this);
        }
    }

    public void Disconnect(View v) {
        vc.disconnect();
    }

    public void onSuccess() {
        Log.d(TAG, "onSuccess");
    }

    public void onFailure(Connector.ConnectorFailReason reason) {
        Log.d(TAG, "ConnectorFailReason "+reason.name());
    }

    public void onDisconnected(Connector.ConnectorDisconnectReason reason) {
        Log.d(TAG, "ConnectorDisconnectReason "+reason.name());
    }

    public void getVidyoToken() {
        String url = "https://us-central1-vidyoio.cloudfunctions.net/getVidyoToken";
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    mToken = jsonObject.getString("token");
                    Log.d(TAG, mToken);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.getMessage());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
