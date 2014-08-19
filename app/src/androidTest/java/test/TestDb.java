package test;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.example.mmewes.de.sunshine.data.WeatherContract.LocationEntry;
import android.example.mmewes.de.sunshine.data.WeatherDbHelper;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.Map;

import static android.example.mmewes.de.sunshine.data.WeatherContract.WeatherEntry;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb() {

        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = getLocationValues();

        long locationRowId = db.insert(LocationEntry.TABLE_NAME, null, values);
        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);
        // A cursor is your primary interface to the query results.
        validateCursor(db.query(
                LocationEntry.TABLE_NAME,  // Table to Query
                null,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        ), getLocationValues());

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues weatherValues = getWeatherValues(locationRowId);
        final long weatherRowId = db.insert(WeatherEntry.TABLE_NAME, null, weatherValues);

        validateCursor(db.query(
                WeatherEntry.TABLE_NAME,  // Table to Query
                null,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        ), weatherValues);

        db.close();
    }
    private ContentValues getWeatherValues(long locationRowId) {
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(WeatherEntry.COLUMN_DATETEXT, "20141205");
        weatherValues.put(WeatherEntry.COLUMN_DEGREES, 1.1);
        weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, 1.2);
        weatherValues.put(WeatherEntry.COLUMN_PRESSURE, 1.3);
        weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, 75);
        weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, 65);
        weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
        weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, 5.5);
        weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, 321);
        return weatherValues;
    }

    private ContentValues getLocationValues() {
        ContentValues values = new ContentValues();
        values.put(LocationEntry.COLUMN_LOCATION_SETTING, "99705");
        values.put(LocationEntry.COLUMN_CITY, "North Pole");
        values.put(LocationEntry.COLUMN_LATITUDE, 64.7488);
        values.put(LocationEntry.COLUMN_LONGITUDE, -147.353);
        return values;
    }

    private void validateCursor(Cursor cursor, ContentValues contentValues) {
        if (cursor.moveToFirst()) {
            for (Map.Entry<String, Object> entry : contentValues.valueSet()) {
                int index = cursor.getColumnIndex(entry.getKey());
                assertFalse(index == -1);
                assertEquals(entry.getValue().toString(), cursor.getString(index));
            }
            cursor.close();
        } else {
            fail("Cursor with no data");
        }
    }
}