package com.example.eventmaster;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.eventmaster.FavouriteClass;

import java.sql.Array;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DBNAME = "Login.db";

    //EVENT DATABASE
    public static final String event_table = "events";
    public static final String eventID = "eventid";
    public static final String name = "name";
    public static final String location = "location";
    public static final String description  = "description";
    public static final String type = "type";
    public static final String startdatetime = "startdatetime";
    public static final String enddatetime = "enddatetime";
    public static final String image = "image";
    public static final String status = "status";

    //USER DATABASE
    public static final String user_table = "users";
    public static final String userID = "userid";
    public static final String email = "email";
    public static final String password = "password";
    public static final String phone = "phone";
    public static final String username = "username";
    public static final String profileimage = "profileimage";
    public static final String role = "role";

    //FAVOURITE TABLE
    public static final String favourite_table = "favourites";
    public static final String favouriteID = "favouriteid";
    public static final String favouriteUserID = "favouriteuserid";
    public static final String favouriteEventID = "favouriteeventid";

    public static final String notification_table = "notification";
    public static final String notificationID = "notificationID";
    public static final String notificationuserID = "notificationuserid";
    public static final String notificationeventID = "notificationeventid";
    public static final String notificationMessage = "notificationmessage";
    public static final String notificationType = "notificationtype";
    public static final String notificationTime = "notificationtime";
    public static final String notificationName = "notificationname";




    public DBHelper(Context context) {
        super(context, DBNAME, null,  30);
    }

    @Override
    public void onCreate(SQLiteDatabase MyDB) {
        String CREATE_EVENT_TABLE = "CREATE TABLE " + event_table + "("
                + eventID + " integer primary key autoincrement,"
                + name + " TEXT,"
                + location + " TEXT,"
                + description + " TEXT,"
                + type + " TEXT,"
                + startdatetime + " LONG,"
                + enddatetime + " LONG,"
                + image + " BLOB,"
                + status + " integer,"
                + "userID INTEGER,"
                + "FOREIGN KEY(userID) REFERENCES " + user_table + "(" + userID + "))";

        String CREATE_USER_TABLE = "CREATE TABLE " + user_table + "("
                + userID + " integer primary key autoincrement,"
                + email + " TEXT,"
                + password + " TEXT,"
                + phone + " TEXT,"
                + username + " TEXT,"
                + profileimage + " BLOB,"
                + role + " integer)";


        String CREATE_FAVOURITE_TABLE = "CREATE TABLE " + favourite_table + "("
                + favouriteID + " integer primary key autoincrement,"
                + favouriteUserID + " integer,"
                + favouriteEventID + " integer)";

        String CREATE_NOTIFICATION_TABLE = "CREATE TABLE " + notification_table + "("
                + notificationID + " integer primary key autoincrement,"
                + notificationuserID + " integer,"
                + notificationeventID + " integer,"
                + notificationMessage + " TEXT,"
                + notificationType + " TEXT,"
                + notificationTime + " TEXT,"
                + notificationName + " TEXT)";


        MyDB.execSQL(CREATE_EVENT_TABLE);
        MyDB.execSQL(CREATE_USER_TABLE);
        MyDB.execSQL(CREATE_FAVOURITE_TABLE);
        MyDB.execSQL(CREATE_NOTIFICATION_TABLE);

    }



    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int i, int i1) {
        String event_query = "DROP TABLE " + event_table;
        String user_query = "DROP TABLE " + user_table;
        String favourite_query = "DROP TABLE " + favourite_table;
        String notification_query = "DROP TABLE " + notification_table;
        MyDB.execSQL(event_query);
        MyDB.execSQL(user_query);
        MyDB.execSQL(favourite_query);
        MyDB.execSQL(notification_query);
        onCreate(MyDB);

        addUserWithDefaultRole(MyDB);
    }

    public void addUserWithDefaultRole(SQLiteDatabase MyDB) {
        // Check if the default user already exists
        Cursor cursor = MyDB.rawQuery("SELECT * FROM " + user_table, null);
        if (cursor.getCount() > 0) {
            cursor.close();
            return; // Default user already exists, do nothing
        }
        cursor.close();

        // Add the default user
        ContentValues contentValues = new ContentValues();
        contentValues.put(email, "admin@gmail.com");
        contentValues.put(password, "123123");
        contentValues.put(phone, "1234567890");
        contentValues.put(username, "admin");
        contentValues.put(profileimage, (byte[]) null); // Replace null with default image if available
        contentValues.put(role, 1);

        MyDB.insert(user_table, null, contentValues);
    }
    //USER FUNCTIONS
    public Boolean addUser(User user) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(email, user.getEmail());
        contentValues.put(password, user.getPassword());
        contentValues.put(phone, user.getPhone());
        contentValues.put(username, user.getUsername());
        contentValues.put(profileimage, user.getProfileimage());
        contentValues.put(role, user.getRole());
        MyDB.insert("users", null, contentValues);
        MyDB.close();
        return true;

    }

    //EVENT FUNCTIONS
    public void addEvent(Event event, int userID) {
        SQLiteDatabase MyDB = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(name, event.getName());
        contentValues.put(location, event.getLocation());
        contentValues.put(description, event.getDescription());
        contentValues.put(type, event.getType());
        contentValues.put(startdatetime, event.getStartdatetime());
        contentValues.put(enddatetime, event.getEnddatetime());
        contentValues.put(image, event.getImage());
        contentValues.put("userID", userID);

        contentValues.put(status, event.getStatus());

        MyDB.insert("events", null, contentValues);
        MyDB.close();

    }

    public void addFavourite(FavouriteClass favourite) {
        SQLiteDatabase MyDB = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(favouriteUserID, favourite.getUserID());
        contentValues.put(favouriteEventID, favourite.getEventID());

        MyDB.insert("favourites", null, contentValues);
        MyDB.close();

    }

    public void addUpdateNotification(NotificationClass notification) {
        SQLiteDatabase MyDB = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(notificationuserID, notification.getUserID());
        contentValues.put(notificationeventID, notification.getEventID());
        contentValues.put(notificationMessage, notification.getNotificationMessage());
        contentValues.put(notificationType, notification.getType());
        contentValues.put(notificationTime, notification.getTime());
        contentValues.put(notificationName, notification.getEventname());

        MyDB.insert("notification", null, contentValues);
        MyDB.close();

    }

    public List<Event> getAllFavourite(int userID) {
        SQLiteDatabase MyDB = getReadableDatabase();
        List<Integer> favoriteEventIDs = new ArrayList<>();
        List<Event> eventList = new ArrayList<>();

        String query = "SELECT favouriteeventid FROM " + favourite_table + " WHERE " + favouriteUserID + "=?";
        Cursor cursor = MyDB.rawQuery(query, new String[]{String.valueOf(userID)});

        if (cursor.moveToFirst()) {
            do {
                int eventID = cursor.getInt(cursor.getColumnIndex(favouriteEventID));
                favoriteEventIDs.add(eventID);
            } while (cursor.moveToNext());
        }

        cursor.close();

        // Now, for each favorite event ID, retrieve the corresponding Event object
        for (int eventID : favoriteEventIDs) {
            Event event = getEventByID(eventID);
            if (event != null) {
                eventList.add(event);
            }
        }

        return eventList;
    }

    public List<Event> getFilteredFavourites(String userID, String filtered) {
        SQLiteDatabase MyDB = getReadableDatabase();
        List<Integer> favoriteEventIDs = new ArrayList<>();
        List<Event> eventList = new ArrayList<>();

        String query = "SELECT favouriteeventid FROM " + favourite_table + " WHERE " + favouriteUserID + "=?";
        Cursor cursor = MyDB.rawQuery(query, new String[]{String.valueOf(userID)});

        if (cursor.moveToFirst()) {
            do {
                int eventID = cursor.getInt(cursor.getColumnIndex(favouriteEventID));
                favoriteEventIDs.add(eventID);
            } while (cursor.moveToNext());
        }

        cursor.close();

        // Now, for each favorite event ID, retrieve the corresponding Event object
        for (int eventID : favoriteEventIDs) {
            Event event = getEventByID(eventID);
            if (event != null && event.getName().toLowerCase().contains(filtered.toLowerCase())) {
                eventList.add(event);
            }
        }

        return eventList;
    }


    public Event getEventByID(int eventID1) {
        SQLiteDatabase MyDB = getReadableDatabase();

        String query = "SELECT * FROM " + event_table + " WHERE " + eventID + "=?";
        Cursor cursor = MyDB.rawQuery(query, new String[]{String.valueOf(eventID1)});

        Event event = new Event();

        if(cursor.moveToFirst()) {
            do {

                event.setEventID(Integer.parseInt(cursor.getString(0)));
                event.setName(cursor.getString(1));
                event.setLocation(cursor.getString(2));
                event.setDescription(cursor.getString(3));
                event.setType(cursor.getString(4));
                event.setStartdatetime(cursor.getLong(5));
                event.setEnddatetime(cursor.getLong(6));
                event.setImage(cursor.getBlob(7));
                event.setStatus(Integer.parseInt(cursor.getString(8)));

            }while (cursor.moveToNext());
        }

        return event;

    }



    public Event getEvent(int userID) {
        SQLiteDatabase MyDB = getReadableDatabase();


        String query = "SELECT * FROM " + event_table + " WHERE userID =?";

        Cursor cursor = MyDB.rawQuery(query, new String[]{String.valueOf(userID)});

        Event event;

        if(cursor != null) {
            cursor.moveToFirst();

            event = new Event(
                    Integer.parseInt(cursor.getString(0)),
                    cursor.getLong(1),
                    cursor.getLong(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    Integer.parseInt(cursor.getString(7)),
                    cursor.getBlob(8),
                    cursor.getInt(9)
            );
            return event;
        }else {
            return null;
        }
    }

    public List<Event> getAllEvents(String userID) {
        SQLiteDatabase MyDB = getReadableDatabase();
        List<Event> event_array = new ArrayList<>();

        String query = "SELECT e.* " +
                "FROM " + event_table + " AS e " +
                "LEFT JOIN " + favourite_table + " AS f " +
                "ON e.eventid = f.favouriteeventid AND f.favouriteuserid = ? " +
                "WHERE f.favouriteeventid IS NULL";

        Cursor cursor = MyDB.rawQuery(query, new String[]{userID});

        if (cursor.moveToFirst()) {
            do {
                Event event = new Event();

                event.setEventID(Integer.parseInt(cursor.getString(0)));
                event.setName(cursor.getString(1));
                event.setLocation(cursor.getString(2));
                event.setDescription(cursor.getString(3));
                event.setType(cursor.getString(4));
                event.setStartdatetime(cursor.getLong(5));
                event.setEnddatetime(cursor.getLong(6));
                event.setImage(cursor.getBlob(7));
                event.setStatus(Integer.parseInt(cursor.getString(8)));

                event_array.add(event);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return event_array;
    }

    public List<Event> getAllEventsPending(String userID) {
        SQLiteDatabase MyDB = getReadableDatabase();
        List<Event> event_array = new ArrayList<>();

        String query = "SELECT e.* " +
                "FROM " + event_table + " AS e " +
                "LEFT JOIN " + favourite_table + " AS f " +
                "ON e.eventid = f.favouriteeventid AND f.favouriteuserid = ? " +
                "WHERE f.favouriteeventid IS NULL AND e.status = 0";

        Cursor cursor = MyDB.rawQuery(query, new String[]{userID});

        if (cursor.moveToFirst()) {
            do {
                Event event = new Event();

                event.setEventID(Integer.parseInt(cursor.getString(0)));
                event.setName(cursor.getString(1));
                event.setLocation(cursor.getString(2));
                event.setDescription(cursor.getString(3));
                event.setType(cursor.getString(4));
                event.setStartdatetime(cursor.getLong(5));
                event.setEnddatetime(cursor.getLong(6));
                event.setImage(cursor.getBlob(7));
                event.setStatus(Integer.parseInt(cursor.getString(8)));

                event_array.add(event);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return event_array;
    }

    public List<Event> getAllEventsApproved(String userID) {
        SQLiteDatabase MyDB = getReadableDatabase();
        List<Event> event_array = new ArrayList<>();

        String query = "SELECT e.* " +
                "FROM " + event_table + " AS e " +
                "LEFT JOIN " + favourite_table + " AS f " +
                "ON e.eventid = f.favouriteeventid AND f.favouriteuserid = ? " +
                "WHERE f.favouriteeventid IS NULL AND e.status = 1";

        Cursor cursor = MyDB.rawQuery(query, new String[]{userID});

        if (cursor.moveToFirst()) {
            do {
                Event event = new Event();

                event.setEventID(Integer.parseInt(cursor.getString(0)));
                event.setName(cursor.getString(1));
                event.setLocation(cursor.getString(2));
                event.setDescription(cursor.getString(3));
                event.setType(cursor.getString(4));
                event.setStartdatetime(cursor.getLong(5));
                event.setEnddatetime(cursor.getLong(6));
                event.setImage(cursor.getBlob(7));
                event.setStatus(Integer.parseInt(cursor.getString(8)));

                event_array.add(event);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return event_array;
    }

    public List<Event> getFilteredAllEvents(String userID, String filtered) {
        SQLiteDatabase MyDB = getReadableDatabase();
        List<Event> event_array = new ArrayList<>();

        String query = "SELECT e.* " +
                "FROM " + event_table + " AS e " +
                "LEFT JOIN " + favourite_table + " AS f " +
                "ON e.eventid = f.favouriteeventid AND f.favouriteuserid = ? " +
                "WHERE f.favouriteeventid IS NULL " +
                "AND e.name LIKE ?";
        Cursor cursor = MyDB.rawQuery(query, new String[]{userID, "%" + filtered + "%"});
        if (cursor.moveToFirst()) {
            do {
                Event event = new Event();
                event.setEventID(Integer.parseInt(cursor.getString(0)));
                event.setName(cursor.getString(1));
                event.setLocation(cursor.getString(2));
                event.setDescription(cursor.getString(3));
                event.setType(cursor.getString(4));
                event.setStartdatetime(cursor.getLong(5));
                event.setEnddatetime(cursor.getLong(6));
                event.setImage(cursor.getBlob(7));
                event.setStatus(Integer.parseInt(cursor.getString(8)));

                event_array.add(event);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return event_array;
    }



    public List<Event> getPersonalEvents(int userID) {
        SQLiteDatabase MyDB = getReadableDatabase();
        List<Event> event_array = new ArrayList<>();



        String query = "SELECT * FROM " + event_table + " WHERE userID = ? ";

        Cursor cursor = MyDB.rawQuery(query, new String[]{String.valueOf(userID)});

        if(cursor.moveToFirst()) {
            do {
                Event event = new Event();

                event.setEventID(Integer.parseInt(cursor.getString(0)));
                event.setName(cursor.getString(1));
                event.setLocation(cursor.getString(2));
                event.setDescription(cursor.getString(3));
                event.setType(cursor.getString(4));
                event.setStartdatetime(cursor.getLong(5));
                event.setEnddatetime(cursor.getLong(6));
                event.setImage(cursor.getBlob(7));
                event.setStatus(Integer.parseInt(cursor.getString(8)));

                event_array.add(event);
            }while (cursor.moveToNext());
        }

        return event_array;
    }

    public List<Event> getFilteredAllPersonalEvents(String userID, String filtered) {
        SQLiteDatabase MyDB = getReadableDatabase();
        List<Event> event_array = new ArrayList<>();



        String query = "SELECT * FROM " + event_table + " WHERE userID = ? " +
                "AND name LIKE ?";

        Cursor cursor = MyDB.rawQuery(query, new String[]{userID, "%" + filtered + "%"});

        if(cursor.moveToFirst()) {
            do {
                Event event = new Event();

                event.setEventID(Integer.parseInt(cursor.getString(0)));
                event.setName(cursor.getString(1));
                event.setLocation(cursor.getString(2));
                event.setDescription(cursor.getString(3));
                event.setType(cursor.getString(4));
                event.setStartdatetime(cursor.getLong(5));
                event.setEnddatetime(cursor.getLong(6));
                event.setImage(cursor.getBlob(7));
                event.setStatus(Integer.parseInt(cursor.getString(8)));

                event_array.add(event);
            }while (cursor.moveToNext());
        }

        return event_array;
    }

    public List<Event> getDateEvent(int userID, long dayInMillis) {
        SQLiteDatabase MyDB = getReadableDatabase();
        List<Integer> favoriteEventIDs = new ArrayList<>();
        List<Event> eventList = new ArrayList<>();

        String query = "SELECT favouriteeventid FROM " + favourite_table + " WHERE " + favouriteUserID + "=?";
        Cursor cursor = MyDB.rawQuery(query, new String[]{String.valueOf(userID)});

        if (cursor.moveToFirst()) {
            do {
                int eventID = cursor.getInt(cursor.getColumnIndex(favouriteEventID));
                favoriteEventIDs.add(eventID);
            } while (cursor.moveToNext());
        }

        cursor.close();

        // Now, for each favorite event ID, retrieve the corresponding Event object
        for (int eventID : favoriteEventIDs) {
            Event event = getEventByID(eventID);
            if (event != null) {
                eventList.add(event);
            }
        }

        List<Event> filteredEvents = new ArrayList<>();
        for (Event event : eventList) {
            if (event.getStartdatetime() >= (dayInMillis - 86400000) && event.getStartdatetime() <= (dayInMillis)) {
                filteredEvents.add(event);
            }
        }

        return filteredEvents;
    }

    public List<NotificationClass> getNotifications(int userID) {
        SQLiteDatabase MyDB = getReadableDatabase();
        List<NotificationClass> notification_array = new ArrayList<>();

        String query = "SELECT * FROM " + notification_table + " WHERE notificationuserID = ?";
        Cursor cursor = MyDB.rawQuery(query, new String[]{String.valueOf(userID)});

        if(cursor.moveToFirst()) {
            do {
                NotificationClass notificationclass =  new NotificationClass();

                notificationclass.setNotificationID(Integer.parseInt(cursor.getString(0)));
                notificationclass.setUserID(Integer.parseInt(cursor.getString(1)));
                notificationclass.setEventID(Integer.parseInt(cursor.getString(2)));
                notificationclass.setNotificationMessage(cursor.getString(3));
                notificationclass.setType(cursor.getString(4));
                notificationclass.setTime(cursor.getString(5));
                notificationclass.setEventname(cursor.getString(6));


                notification_array.add(notificationclass);
            }while (cursor.moveToNext());
        }

        return notification_array;
    }


    public void updateEvent(Event event, int userId) {
        SQLiteDatabase MyDB = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(name, event.getName());
        contentValues.put(location, event.getLocation());
        contentValues.put(description, event.getDescription());
        contentValues.put(type, event.getType());
        contentValues.put(startdatetime, event.getStartdatetime());
        contentValues.put(enddatetime, event.getEnddatetime());
        contentValues.put(userID, userId);
        contentValues.put(image, event.getImage());
        contentValues.put(status, event.getStatus());


        MyDB.update(
                event_table,
                contentValues,
                eventID + "=?",
                new String[]{String.valueOf(event.getEventID())}
        );

        MyDB.close();

    }

    public void approveEvent(int eventID1) {
        SQLiteDatabase MyDB = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(status, 1);
        //1 is approved
        // 2 is reject


        MyDB.update(
                event_table,
                contentValues,
                eventID + "=?",
                new String[]{String.valueOf(eventID1)}
        );

        MyDB.close();

    }

    public void rejectEvent(int eventID1) {
        SQLiteDatabase MyDB = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(status, 2);

        MyDB.update(
                event_table,
                contentValues,
                eventID + "=?",
                new String[]{String.valueOf(eventID1)}
        );

        MyDB.close();

    }

    public void updateProfileImage(int userID1, byte[] image) {
        SQLiteDatabase MyDB = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(profileimage, image);

        MyDB.update(
                user_table,
                contentValues,
                userID + "=?",
                new String[]{String.valueOf(userID1)}
        );
    }

    public void updateUserPassword(int userID1, String password1) {
        SQLiteDatabase MyDB = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(password, password1);

        MyDB.update(
                user_table,
                contentValues,
                userID + "=?",
                new String[]{String.valueOf(userID1)}
        );
    }

    public void deleteEvent(int eventID1) {
        SQLiteDatabase MyDB = getWritableDatabase();
        MyDB.delete(
                event_table,
                eventID + "=?",
                new String[] { String.valueOf(eventID1) }
        );

        MyDB.delete(
                favourite_table,
                favouriteEventID + "=?",
                new String[] { String.valueOf(eventID1) }
        );
        MyDB.close();
    }

    public void deleteFavourite(int eventID1, int userID1) {
        SQLiteDatabase MyDB = getWritableDatabase();
        MyDB.delete(
                favourite_table,
                favouriteEventID + "=? AND " + favouriteUserID + "=?",
                new String[] { String.valueOf(eventID1), String.valueOf(userID1) }
        );
        MyDB.close();
    }


    public int countEvents() {
        SQLiteDatabase MyDB = getReadableDatabase();
        String query = "SELECT * FROM " + event_table;
        Cursor cursor = MyDB.rawQuery(query, null);
        return cursor.getCount();
    }

    public int countFavourite(int eventid1) {
        SQLiteDatabase MyDB = getReadableDatabase();
        String query = "SELECT * FROM " + favourite_table + " WHERE favouriteeventid =?";
        Cursor cursor = MyDB.rawQuery(query, new String[] {String.valueOf(eventid1)});
        return cursor.getCount();
    }


    ///checks if the user exist in the database
    public Boolean checkemail(String email) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from users where email = ?", new String[] {email});
        if(cursor.getCount()>0)
            return true;
        else
            return false;
    }

    public Boolean checkemailpassword(String email, String password) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from users where email = ? and password = ?", new String[] {email,password});
        if(cursor.getCount()>0)
            return true;
        else
            return false;
    }

    public int getUserID(String email, String password) {
        SQLiteDatabase MyDB = this.getReadableDatabase();
        Cursor cursor = MyDB.rawQuery("SELECT " + userID + " FROM " + user_table + " WHERE email= ? AND password= ?", new String[]{email, password});

        int userID = -1; // Initialize userID to -1 as a default value

        if (cursor.moveToFirst()) {
            userID = cursor.getInt(0); // Get the userID from the first row
        }

        cursor.close();
        return userID;
    }

    public int getUserRole(int userID1) {
        SQLiteDatabase MyDB = this.getReadableDatabase();
        Cursor cursor = MyDB.rawQuery("SELECT " + role + " FROM " + user_table + " WHERE userID =?", new String[]{String.valueOf(userID1)});

        int role = -1; // Initialize userID to -1 as a default value

        if (cursor.moveToFirst()) {
            role = cursor.getInt(0); // Get the userID from the first row
        }

        cursor.close();
        return role;
    }

    public List<User> getUserInfo(int userID1) {
        SQLiteDatabase MyDB = this.getReadableDatabase();
        Cursor cursor = MyDB.rawQuery("SELECT * FROM " + user_table + " WHERE userID=?", new String[]{String.valueOf(userID1)});
        List<User> user_array = new ArrayList<>();


        while (cursor.moveToNext()) {
            // Retrieve user information from the cursor for each result
            int userID = cursor.getInt(0);
            String username = cursor.getString(1);
            String email = cursor.getString(2);
            String phone = cursor.getString(3);
            String username1 = cursor.getString(4);
            byte[] profileimage = cursor.getBlob(5);
            int role = cursor.getInt(6);

            // Create a User object with the retrieved information and add it to the list
            User user = new User(userID, username, email, phone, username1, profileimage, role);
            user_array.add(user);
        }

        cursor.close();
        MyDB.close();

        return user_array;
    }

    public String getOldPassword(int userID1) {
        SQLiteDatabase MyDB = getReadableDatabase();
        String oldPassword = null;
        String query = "SELECT password FROM " + user_table + " WHERE userid = ?";

        Cursor cursor = MyDB.rawQuery(query, new String[]{String.valueOf(userID1)});

        if (cursor.moveToFirst()) {
            oldPassword = cursor.getString(0); // Get the password from the first row
        }

        cursor.close();

        return oldPassword;
    }

    public String getUserEmail(int userID1) {
        SQLiteDatabase MyDB = getReadableDatabase();
        String email1 = null;
        String query = "SELECT email FROM " + user_table + " WHERE userid = ?";

        Cursor cursor = MyDB.rawQuery(query, new String[]{String.valueOf(userID1)});

        if (cursor.moveToFirst()) {
            email1 = cursor.getString(0); // Get the password from the first row
        }

        cursor.close();

        return email1;
    }

    public int getUserIDByEmail(String email) {
        SQLiteDatabase MyDB = this.getReadableDatabase();
        Cursor cursor = MyDB.rawQuery("SELECT " + userID + " FROM " + user_table + " WHERE email= ?", new String[]{email});

        int userID = -1; // Initialize userID to -1 as a default value

        if (cursor.moveToFirst()) {
            userID = cursor.getInt(0); // Get the userID from the first row
        }

        cursor.close();
        return userID;
    }

    public int getUserIDByPhone(String phone) {
        SQLiteDatabase MyDB = this.getReadableDatabase();
        Cursor cursor = MyDB.rawQuery("SELECT " + userID + " FROM " + user_table + " WHERE phone= ?", new String[]{phone});

        int userID = -1; // Initialize userID to -1 as a default value

        if (cursor.moveToFirst()) {
            userID = cursor.getInt(0); // Get the userID from the first row
        }

        cursor.close();
        return userID;
    }

    public int getUserIDByEventID(int eventID) {
        SQLiteDatabase MyDB = this.getReadableDatabase();
        Cursor cursor = MyDB.rawQuery("SELECT " + userID + " FROM " + event_table + " WHERE eventid= ?", new String[]{String.valueOf(eventID)});

        int userID = -1; // Initialize userID to -1 as a default value

        if (cursor.moveToFirst()) {
            userID = cursor.getInt(0); // Get the userID from the first row
        }

        cursor.close();
        return userID;
    }
    public void updateUserPhoneNumber(String email1, String phone1) {
        SQLiteDatabase MyDB = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(phone, phone1);

        MyDB.update(
                user_table,
                contentValues,
                email + "=?",
                new String[]{email1}
        );

        MyDB.close();
    }

    public void deleteUser(int userID1) {
        SQLiteDatabase MyDB = getWritableDatabase();
        MyDB.delete(
                user_table,
                userID + "=?",
                new String[] { String.valueOf(userID1) }
        );

        MyDB.close();
    }



    public ArrayList<Integer> findAllFavouriteUsers(int eventId) {
        SQLiteDatabase MyDB = getReadableDatabase();
        ArrayList<Integer> favouriteUsers = new ArrayList<>();

        String query = "SELECT favouriteuserid FROM " + favourite_table + " WHERE favouriteeventid = ?";

        Cursor cursor = MyDB.rawQuery(query, new String[]{String.valueOf(eventId)});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int userId = cursor.getInt(0);
                    favouriteUsers.add(userId);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return favouriteUsers;
    }


}
