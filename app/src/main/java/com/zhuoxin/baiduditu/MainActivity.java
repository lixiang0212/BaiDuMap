package com.zhuoxin.baiduditu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

public class MainActivity extends AppCompatActivity {
    private MapView mapView;
    private BaiduMap bm;
    public LocationClient client = null;
    private boolean first = true;
    private GeoCoder geoCoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化百度SDK
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.map_view);
        bm = mapView.getMap();
        //可以设置我的当前位置
        bm.setMyLocationEnabled(true);
        //设置显示缩放级别
        mapView.showZoomControls(true);
        mapView.showScaleControl(true);
        //设置缩放级别
        bm.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(18).build()));

        geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

            }
        });
        geoCoder.geocode(new GeoCodeOption().city("北京").address("天安门"));




        initData();


    }

    private void initData() {
        //获取当前位置连接
        client = new LocationClient(getApplicationContext());
        //监听
        client.registerLocationListener(listener);
        //设置返回地址的类型
        initLocation();
        client.start();
        client.requestLocation();
    }
    private BDLocationListener listener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            //根据返回类型设置地图位置
            if (null != bdLocation && bdLocation.getLocType() != BDLocation.TypeServerError) {
                float a = bdLocation.getRadius();//半径
                double b = bdLocation.getLatitude(); //维度
                double c = bdLocation.getLongitude();  //经度
                //设置数据
                MyLocationData.Builder build = new MyLocationData.Builder();
                build.accuracy(a);
                build.direction(1000);
                build.latitude(b);
                build.longitude(c);
                build.direction(100f);
                MyLocationData data = build.build();
                bm.setMyLocationData(data);   //把数据设置到View上

                if (first) {first = false;
                    MapStatus.Builder builder = new MapStatus.Builder();
                    LatLng ll = new LatLng(b, c);
                    builder.target(ll);
                    MapStatus ms = builder.build();
                    MapStatusUpdate msu = MapStatusUpdateFactory.newMapStatus(ms);
                    bm.animateMapStatus(msu);
                 //   bm.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(16).build()));
                }
            }else {
              Log.i("AAA","错误类型"+bdLocation.getLocType());
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    };
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();  //本地连接设置
        option.setOpenGps(true); //开启GPS
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        option.setCoorType("bd09ll");
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setAddrType("all");
        option.setScanSpan(3000);
        //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setLocationNotify(true);
        option.setIsNeedAddress(true);   //可选，设置是否需要地址信息，默认不需要
        //可选，设置是否需要设备方向结果
        option.setNeedDeviceDirect(true);
        //可选，默认false，设置是否收集CRASH信息，默认收集
        option.SetIgnoreCacheException(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附
        option.setIsNeedLocationDescribe(true);
        client.setLocOption(option);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        client.stop();
        bm.setMyLocationEnabled(false);
        mapView.onDestroy();
        mapView =null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
}
