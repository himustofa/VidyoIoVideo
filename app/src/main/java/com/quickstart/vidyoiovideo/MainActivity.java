package com.quickstart.vidyoiovideo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.vidyo.VidyoClient.Connector.ConnectorPkg;
import com.vidyo.VidyoClient.Connector.Connector;

public class MainActivity extends AppCompatActivity implements Connector.IConnect {

    private static final String TAG="MainActivity";
    private Connector vc;
    private FrameLayout videoFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectorPkg.setApplicationUIContext(this);
        ConnectorPkg.initialize();
        videoFrame = (FrameLayout)findViewById(R.id.videoFrame);
    }

    public void Start(View v) {
        vc = new Connector(videoFrame, Connector.ConnectorViewStyle.VIDYO_CONNECTORVIEWSTYLE_Default, 15, "warning info@VidyoClient info@VidyoConnector", "", 0);
        vc.showViewAt(videoFrame, 0, 0, videoFrame.getWidth(), videoFrame.getHeight());
    }

    public void Connect(View v) {
        String token = "cHJvdmlzaW9uAERlbW9Vc2VyQDAzOWQxNC52aWR5by5pbwA2Mzc0MDI1ODA0NwAAODQ4YmI3OWJkNTRmNWU0YzVhZDY4M2I1ZTQwYWU5M2FjOGExOTcxNGMyYTNkMDRjZmIxYjY3OGYwZmRhOWE2ZGJiYTIxM2Q4ZDAxYWI3OTE4MTJiZmQxYWMxODI5YmZk";
        vc.connect("prod.vidyo.io", token, "DemoUser", "DemoRoom", this);
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
}
