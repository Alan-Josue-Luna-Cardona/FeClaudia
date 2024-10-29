  package gt.edu.umg.feclaudia;

  import androidx.annotation.NonNull;
  import androidx.appcompat.app.AppCompatActivity;
  import androidx.core.app.ActivityCompat;
  import androidx.core.content.ContextCompat;
  import android.Manifest;
  import android.annotation.SuppressLint;
  import android.content.Intent;
  import android.content.pm.PackageManager;
  import android.graphics.Bitmap;
  import android.location.Location;
  import android.os.Bundle;
  import android.provider.MediaStore;
  import android.view.View;
  import android.widget.Button;
  import android.widget.Toast;
  import com.google.android.gms.location.FusedLocationProviderClient;
  import com.google.android.gms.location.LocationServices;
  import com.google.android.gms.maps.GoogleMap;
  import com.google.android.gms.maps.OnMapReadyCallback;
  import com.google.android.gms.maps.SupportMapFragment;
  import com.google.android.gms.maps.model.LatLng;
  import com.google.android.gms.maps.model.MarkerOptions;
  import java.io.ByteArrayOutputStream;
  import java.util.Date;

  public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
      private static final int CAMERA_PERMISSION_CODE = 100;
      private static final int LOCATION_PERMISSION_CODE = 101;
      private static final int CAMERA_REQUEST_CODE = 102;

      private GoogleMap mMap;
      private FusedLocationProviderClient fusedLocationClient;
      private DatabaseHelper dbHelper;
      private Button btnTakePhoto, btnShowData;

      @SuppressLint("MissingInflatedId")
      @Override
      protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_main);

          // Inicializar componentes
          btnTakePhoto = findViewById(R.id.btnTakePhoto);
          btnShowData = findViewById(R.id.btnShowData);

          // Inicializar base de datos
          dbHelper = new DatabaseHelper(this);

          // Inicializar cliente de ubicación
          fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

          // Configurar mapa
          SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                  .findFragmentById(R.id.map);
          mapFragment.getMapAsync(this);

          // Configurar botones
          btnTakePhoto.setOnClickListener(v -> checkPermissionsAndTakePhoto());
          btnShowData.setOnClickListener(v -> showDataActivity());
      }

      private void checkPermissionsAndTakePhoto() {
          if (checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE) &&
                  checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_PERMISSION_CODE)) {
              takePhoto();
          }
      }

      private boolean checkPermission(String permission, int requestCode) {
          if (ContextCompat.checkSelfPermission(this, permission)
                  != PackageManager.PERMISSION_GRANTED) {
              ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
              return false;
          }
          return true;
      }

      private void takePhoto() {
          Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
          startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
      }

      @Override
      protected void onActivityResult(int requestCode, int resultCode, Intent data) {
          super.onActivityResult(requestCode, resultCode, data);
          if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
              Bitmap photo = (Bitmap) data.getExtras().get("data");
              savePhotoWithLocation(photo);
          }
      }

      private void savePhotoWithLocation(Bitmap photo) {
          if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                  != PackageManager.PERMISSION_GRANTED) {
              return;
          }

          fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
              if (location != null) {
                  // Convertir Bitmap a bytes para guardar en la base de datos
                  ByteArrayOutputStream stream = new ByteArrayOutputStream();
                  photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
                  byte[] byteArray = stream.toByteArray();

                  // Guardar en la base de datos
                  PhotoLocation photoLocation = new PhotoLocation(
                          byteArray,
                          location.getLatitude(),
                          location.getLongitude(),
                          new Date().getTime()
                  );
                  dbHelper.addPhotoLocation(photoLocation);

                  // Añadir marcador al mapa
                  addMarkerToMap(new LatLng(location.getLatitude(), location.getLongitude()));

                  Toast.makeText(this, "Foto guardada con ubicación", Toast.LENGTH_SHORT).show();
              }
          });
      }

      private void addMarkerToMap(LatLng location) {
          if (mMap != null) {
              mMap.addMarker(new MarkerOptions().position(location));
          }
      }

      private void showDataActivity() {
          Intent intent = new Intent(this, DataListActivity.class);
          startActivity(intent);
      }

      @Override
      public void onMapReady(GoogleMap googleMap) {
          mMap = googleMap;
          if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                  == PackageManager.PERMISSION_GRANTED) {
              mMap.setMyLocationEnabled(true);
          }
      }

      @Override
      public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                             @NonNull int[] grantResults) {
          super.onRequestPermissionsResult(requestCode, permissions, grantResults);
          if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
              if (requestCode == CAMERA_PERMISSION_CODE || requestCode == LOCATION_PERMISSION_CODE) {
                  checkPermissionsAndTakePhoto();
              }
          }
      }
  }