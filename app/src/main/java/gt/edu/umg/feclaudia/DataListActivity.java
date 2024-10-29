package gt.edu.umg.feclaudia;


import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.SimpleAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DataListActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_list);

        dbHelper = new DatabaseHelper(this);
        listView = findViewById(R.id.listView);

        loadPhotoLocations();
    }

    private void loadPhotoLocations() {
        List<PhotoLocation> photoLocations = dbHelper.getAllPhotoLocations();
        PhotoLocationAdapter adapter = new PhotoLocationAdapter(photoLocations);
        listView.setAdapter(adapter);
    }

    private class PhotoLocationAdapter extends BaseAdapter {
        private List<PhotoLocation> photoLocations;
        private SimpleDateFormat dateFormat;

        public PhotoLocationAdapter(List<PhotoLocation> photoLocations) {
            this.photoLocations = photoLocations;
            this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        }

        @Override
        public int getCount() {
            return photoLocations.size();
        }

        @Override
        public Object getItem(int position) {
            return photoLocations.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(DataListActivity.this)
                        .inflate(R.layout.list_item_photo_location, parent, false);
            }

            PhotoLocation item = photoLocations.get(position);

            ImageView imageView = convertView.findViewById(R.id.imageView);
            TextView tvLocation = convertView.findViewById(R.id.tvLocation);
            TextView tvTimestamp = convertView.findViewById(R.id.tvTimestamp);

            // Configurar la imagen
            byte[] photoBytes = item.getPhoto();
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length));

            // Configurar la ubicación
            String location = String.format(Locale.getDefault(),
                    "Lat: %.4f, Long: %.4f",
                    item.getLatitude(),
                    item.getLongitude());
            tvLocation.setText(location);

            // Configurar la fecha
            String timestamp = dateFormat.format(new Date(item.getTimestamp()));
            tvTimestamp.setText(timestamp);

            return convertView;
        }
    }
}
