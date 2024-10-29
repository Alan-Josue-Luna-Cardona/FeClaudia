package gt.edu.umg.feclaudia;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "PhotoLocationDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "photo_locations";

    private static final String KEY_ID = "id";
    private static final String KEY_PHOTO = "photo";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_TIMESTAMP = "timestamp";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_PHOTO + " BLOB,"
                + KEY_LATITUDE + " DOUBLE,"
                + KEY_LONGITUDE + " DOUBLE,"
                + KEY_TIMESTAMP + " LONG"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addPhotoLocation(PhotoLocation photoLocation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_PHOTO, photoLocation.getPhoto());
        values.put(KEY_LATITUDE, photoLocation.getLatitude());
        values.put(KEY_LONGITUDE, photoLocation.getLongitude());
        values.put(KEY_TIMESTAMP, photoLocation.getTimestamp());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<PhotoLocation> getAllPhotoLocations() {
        List<PhotoLocation> photoLocationList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + KEY_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") PhotoLocation photoLocation = new PhotoLocation(
                        cursor.getBlob(cursor.getColumnIndex(KEY_PHOTO)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE)),
                        cursor.getLong(cursor.getColumnIndex(KEY_TIMESTAMP))
                );
                photoLocationList.add(photoLocation);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return photoLocationList;
    }
}