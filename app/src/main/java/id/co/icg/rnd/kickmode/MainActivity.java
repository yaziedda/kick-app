package id.co.icg.rnd.kickmode;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import im.delight.android.location.SimpleLocation;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.tv_android_version_model)
    TextView tvAndroidVersionModel;
    @BindView(R.id.tv_ip)
    TextView tvIp;
    @BindView(R.id.tv_refuse_all)
    TextView tvRefuseAll;
    @BindView(R.id.title_session)
    TextView titleSession;
    @BindView(R.id.my_session)
    LinearLayout mySession;
    @BindView(R.id.recylcer_view)
    RecyclerView recylcerView;
    @BindView(R.id.bt_login)
    Button btLogin;
    private SimpleLocation simpleLocation;
    List<LoginModel> list = new ArrayList<>();
    RVAdapter rvAdapter;
    LoginModel loginModelSession = new LoginModel();
    String MY_NUMBER = "YOUR_PHONE_NUMBER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("session-log").child(MY_NUMBER);
        simpleLocation = new SimpleLocation(this);
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE)
                .check();

        rvAdapter = new RVAdapter(list, new RVAdapter.OnItemClickListener() {
            @Override
            public void onClick(final LoginModel model) {
                onClicked(model);
            }
        });

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(loginModelSession != null){
                    if(loginModelSession.isActive()){
                        loginModelSession.setActive(false);
                        myRef.child(loginModelSession.getId()).setValue(loginModelSession);
                    }else{
                        loginModelSession.setActive(true);
                        myRef.child(loginModelSession.getId()).setValue(loginModelSession);
                    }
                }
            }
        });
    }

    private void initListenerRealtimeDB() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    LoginModel loginModel = postSnapshot.getValue(LoginModel.class);
                    if (!loginModel.getId().equals(loginModelSession.getId())) {
                        if (loginModel.isActive()) {
                            list.add(loginModel);
                        }
                    } else {
                        if (!loginModel.isActive()) {
                            loginModelSession.setActive(false);
                        } else {
                            loginModelSession.setActive(true);
                        }
                    }
                    System.out.println(new Gson().toJson(loginModel));
                }
                if (!loginModelSession.isActive()) {
                    // kicked from session
                    mySession.setVisibility(View.GONE);
                    titleSession.setText("You kicked");
                    recylcerView.setVisibility(View.GONE);
                    btLogin.setText("LOGIN");
                } else {
                    mySession.setVisibility(View.VISIBLE);
                    titleSession.setText("Sesi Saat Ini");
                    recylcerView.setVisibility(View.VISIBLE);
                    btLogin.setText("LOGOUT");
                }
                rvAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initAdapter() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recylcerView.setLayoutManager(layoutManager);
        recylcerView.setAdapter(rvAdapter);
    }

    private void onClicked(final LoginModel model) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setMessage("Are you sure to refuse "+model.getId()+" in "+model.getAndroidOs()+" "+model.getAndroidType());
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        model.setActive(false);
                        myRef.child(model.getId()).setValue(model);
                    }
                });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    void setMySessionUI() {
        tvAndroidVersionModel.setText(loginModelSession.getAndroidOs() + " " + loginModelSession.getAndroidType());
        tvIp.setText(loginModelSession.getLatestIP() + "\n" + loginModelSession.getId());
        tvVersion.setText("KickApp version " + loginModelSession.getAppVersion());
    }

    private void setLoginSession(final LoginModel loginModel, final boolean loginClicked) {
        String deviceName = Build.MODEL;
        String manufactur = Build.MANUFACTURER;
        String version = "Android " + Build.VERSION.RELEASE;
        String IP = getIP();
        String versionApp = versionApp();
        final String imei = imei();
        loginModel.setAndroidOs(version);
        loginModel.setAndroidType(deviceName);
        loginModel.setAppVersion(versionApp);
        loginModel.setLatestIP(IP);
        loginModel.setId(imei);

        myRef.child(imei).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LoginModel model = dataSnapshot.getValue(LoginModel.class);
                if (model != null) {
                    loginModel.setId(model.getId());
                    loginModel.setAndroidOs(model.getAndroidOs());
                    loginModel.setActive(model.isActive());
                    loginModel.setLatestIP(model.getLatestIP());
                    loginModel.setAppVersion(model.getAppVersion());
                    loginModel.setLocation(model.getLocation());
                    if(loginModel.isActive()){
                        btLogin.setText("LOGOUT");
                    }else {
                        btLogin.setText("LOGIN");
                    }
                } else {
                    if(loginClicked) {
                        loginModel.setActive(true);
                        myRef.child(imei).setValue(loginModel);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            if (!simpleLocation.hasLocationEnabled()) {
                SimpleLocation.openSettings(getApplicationContext());
            } else {
                setLoginSession(loginModelSession, false);
                setMySessionUI();
                initAdapter();
                initListenerRealtimeDB();
            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {

        }
    };

    private String imei() {
        TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String deviceid = mTelephonyManager.getDeviceId();
        return deviceid;
    }

    private String versionApp() {
        PackageManager manager = getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(
                    getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getIP() {
        WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        return Formatter.formatIpAddress(ip);
//        return "Testing";
    }

    public void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubThoroughfare();

            Log.v("IGA", "Address" + add);
            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            // TennisAppActivity.showDialog(add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    @OnClick(R.id.tv_refuse_all)
    public void onViewClicked() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setMessage("Are you sure to refuse all?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        for (LoginModel loginModel:list){
                            loginModel.setActive(false);
                            myRef.child(loginModel.getId()).setValue(loginModel);
                        }
                    }
                });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }
}
