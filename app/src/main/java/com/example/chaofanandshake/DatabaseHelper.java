        package com.example.chaofanandshake;

        import android.annotation.SuppressLint;
        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.util.Base64;
        import android.util.Log;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;

        import com.example.chaofanandshake.Domain.Order;
        import com.example.chaofanandshake.Domain.ProductDomain;
        import com.example.chaofanandshake.Domain.User;

        import java.nio.charset.StandardCharsets;
        import java.security.MessageDigest;
        import java.security.NoSuchAlgorithmException;
        import java.util.ArrayList;
        import java.util.List;
        import java.util.jar.Attributes;

        public class DatabaseHelper extends SQLiteOpenHelper {

            private static final String DATABASE_NAME = "ChaofanUserDb";
            private static final int DATABASE_VERSION = 8;

            public static final String TABLE_USERS = "users";
            private static final String COLUMN_ID = "id";
            private static final String COLUMN_NAME = "name";
            private static final String COLUMN_USERNAME = "username";
            private static final String COLUMN_PHONE = "phone";
            private static final String COLUMN_PASSWORD = "password";
            private static final String COLUMN_ROLE = "role";

            public static final String TABLE_ORDERS = "orders";

            private static final String TABLE_PRODUCTS = "products";

            private static final String CREATE_TABLE_PRODUCTS = "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "imageName TEXT, " +
                    "title TEXT, " +
                    "description TEXT, " +
                    "price REAL);";


            private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_USERNAME + " TEXT, " +
                    COLUMN_PHONE + " TEXT, " +
                    COLUMN_PASSWORD + " TEXT, " +
                    COLUMN_ROLE + " TEXT);";

            private static final String CREATE_TABLE_ORDERS = "CREATE TABLE " + TABLE_ORDERS + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    getOrderSummary() + " TEXT, " +
                    "name TEXT, " +
                    "phone_number  TEXT, " +
                    "username TEXT, " +
                    "payment_method TEXT, " +
                    "total_price REAL, " +
                    "status TEXT DEFAULT 'Pending', " +
                    "date DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "orderPlacedTimestamp INTEGER DEFAULT (strftime('%s','now')));";

            @NonNull
            private static String getOrderSummary() {
                return "order_summary";
            }


            public DatabaseHelper(@Nullable Context context) {
                super(context, DATABASE_NAME, null, DATABASE_VERSION);
            }

            public int getUserCount() {
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USERS, null);
                int count = 0;
                if (cursor.moveToFirst()) {
                    count = cursor.getInt(0);
                }
                cursor.close();
                return count;
            }

            public int getOrderCount() {
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ORDERS, null);
                int count = 0;
                if (cursor.moveToFirst()) {
                    count = cursor.getInt(0);
                }
                cursor.close();
                return count;
            }

            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL(CREATE_TABLE_USERS);
                db.execSQL(CREATE_TABLE_ORDERS);
                db.execSQL("CREATE TABLE IF NOT EXISTS products (id INTEGER PRIMARY KEY AUTOINCREMENT, imageName TEXT, title TEXT, description TEXT, price REAL)");

                ContentValues admin = new ContentValues();
                admin.put(COLUMN_NAME, "Administrator");
                admin.put(COLUMN_USERNAME, "admin");
                admin.put(COLUMN_PHONE, "");
                admin.put(COLUMN_PASSWORD, "1");
                admin.put(COLUMN_ROLE, "admin");
                db.insert(TABLE_USERS, null, admin);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);

                onCreate(db);
            }
            public boolean checkUsernameExists(String username) {
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ?", new String[]{username});
                boolean exists = cursor.getCount() > 0;
                cursor.close();
                return exists;
            }

            public boolean checkPhoneExists(String phone) {
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_PHONE + " = ?", new String[]{phone});
                boolean exists = cursor.getCount() > 0;
                cursor.close();
                return exists;
            }

            public boolean insertUser(String name, String username, String phone, String password) {
                return insertUser(name, username, phone, password, "user");
            }

            public boolean insertUser(String name, String username, String phone, String password, String role) {
                if (checkUsernameExists(username) || checkPhoneExists(phone)) return false;

                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(COLUMN_NAME, name);
                values.put(COLUMN_USERNAME, username);
                values.put(COLUMN_PHONE, phone);
                values.put(COLUMN_PASSWORD, password);
                values.put(COLUMN_ROLE, role);

                long result = db.insert(TABLE_USERS, null, values);
                db.close();
                return result != -1;
            }

            public boolean isAdmin(String username) {
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT " + COLUMN_ROLE + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + "=?", new String[]{username});
                if (cursor != null && cursor.moveToFirst()) {
                    String role = cursor.getString(0);
                    cursor.close();
                    return "admin".equalsIgnoreCase(role);
                }
                return false;
            }

            // In DatabaseHelper.java
            public boolean checkUser(String username, String password) {
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery(
                        "SELECT * FROM " + TABLE_USERS +
                                " WHERE " + COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                        new String[]{username, password}
                );
                boolean exists = cursor.getCount() > 0;
                cursor.close();
                db.close();
                return exists;
            }

            private String hashPassword(String password) {
                try {
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
                    return Base64.encodeToString(hash, Base64.DEFAULT);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    return password; // fallback (not secure)
                }
            }

            public String getUserName(String username) {
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT " + COLUMN_NAME + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + "=?", new String[]{username});
                String userName = "";
                if (cursor.moveToFirst()) {
                    userName = cursor.getString(0);
                }
                cursor.close();
                return userName;
            }

            public String getUsernameByName(String name) {
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT " + COLUMN_USERNAME + " FROM " + TABLE_USERS + " WHERE " + COLUMN_NAME + " = ?", new String[]{name});
                if (cursor != null && cursor.moveToFirst()) {
                    String updatedUsername = cursor.getString(0);
                    cursor.close();
                    return updatedUsername;
                }
                return null;
            }

            @SuppressLint("Range")
            public String[] getUserDataByUsername(String username) {
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + "=?", new String[]{username});
                if (cursor != null && cursor.moveToFirst()) {
                    String[] userData = new String[4];
                    userData[0] = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                    userData[1] = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME));
                    userData[2] = cursor.getString(cursor.getColumnIndex(COLUMN_PHONE));
                    userData[3] = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD));
                    cursor.close();
                    return userData;
                }
                return null;
            }

            public boolean updateUser(String currentUsername, String newUsername, String newName, String newPhone, String newPassword) {
                SQLiteDatabase db = this.getReadableDatabase();
                if (!currentUsername.equals(newUsername) && checkUsernameExists(newUsername)) return false;

                Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_PHONE + "=? AND " + COLUMN_USERNAME + "!=?", new String[]{newPhone, currentUsername});
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.close();
                    return false;
                }
                if (cursor != null) cursor.close();

                ContentValues values = new ContentValues();
                values.put(COLUMN_NAME, newName);
                values.put(COLUMN_USERNAME, newUsername);
                values.put(COLUMN_PHONE, newPhone);
                values.put(COLUMN_PASSWORD, newPassword);


                int rowsUpdated = db.update(TABLE_USERS, values, COLUMN_USERNAME + "=?", new String[]{currentUsername});
                return rowsUpdated > 0;
            }

            public boolean deleteUserAndOrders(String username) {
                SQLiteDatabase db = this.getWritableDatabase();
                db.beginTransaction();
                try {
                    // Delete orders linked to the user
                    db.delete(TABLE_ORDERS, "username = ?", new String[]{username});

                    // Delete the user
                    int rowsDeleted = db.delete(TABLE_USERS, COLUMN_USERNAME + " = ?", new String[]{username});

                    if (rowsDeleted > 0) {
                        db.setTransactionSuccessful(); // commit transaction
                        return true;
                    } else {
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    db.endTransaction(); // end transaction
                }
            }


            public boolean updateUser(int id, String name, String username, String phone, String role) {
                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(COLUMN_NAME, name);
                values.put(COLUMN_USERNAME, username);
                values.put(COLUMN_PHONE, phone);
                values.put(COLUMN_ROLE, role);

                int rowsUpdated = db.update(TABLE_USERS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
                return rowsUpdated > 0;
            }

            public boolean checkUserPhone(String username, String phone) {
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ? AND phone = ?", new String[]{username, phone});
                boolean exists = cursor.getCount() > 0;
                cursor.close();
                return exists;
            }

            public boolean updatePassword(String username, String newPassword) {
                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("password", newPassword);
                int rows = db.update("users", values, "username = ?", new String[]{username});
                return rows > 0;
            }

            public boolean deleteProduct(String productTitle) {
                SQLiteDatabase db = this.getWritableDatabase();
                int result = db.delete(TABLE_PRODUCTS, "title = ?", new String[]{productTitle});
                db.close();
                return result > 0;
            }

            public boolean updateProduct(String oldTitle, ProductDomain updatedProduct) {
                SQLiteDatabase db = this.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put("imageName", updatedProduct.getImageName());
                values.put("title", updatedProduct.getTitle());
                values.put("description", updatedProduct.getDescription());
                values.put("price", updatedProduct.getPrice());

                int result = db.update(TABLE_PRODUCTS, values, "title = ?", new String[]{oldTitle});
                db.close();
                return result > 0;
            }

            public boolean doesTableExist(String tableName) {
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery(
                        "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                        new String[]{tableName});
                boolean exists = cursor.getCount() > 0;
                cursor.close();
                return exists;
            }

            public List<User> getAllUsers() {
                List<User> users = new ArrayList<>();
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT id, name, username, phone FROM users", null);

                if (cursor.moveToFirst()) {
                    do {
                        int id = cursor.getInt(0);                // id
                        String name = cursor.getString(1);        // name
                        String username = cursor.getString(2);    // username
                        String phone = cursor.getString(3);       // phone

                        User user = new User(id, name, username, phone);
                        users.add(user);
                    } while (cursor.moveToNext());
                }

                cursor.close();
                return users;
            }

            public boolean insertOrder(String summary, String name, String phone, String username,
                                       String paymentMethod, double totalPrice, long timestamp) {
                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("order_summary", summary);
                values.put("name", name);
                values.put("phone_number", phone);
                values.put("username", username);
                values.put("payment_method", paymentMethod);
                values.put("total_price", totalPrice);
                values.put("orderPlacedTimestamp", timestamp);

                long result = db.insert(TABLE_ORDERS, null, values);
                db.close();
                return result != -1;
            }

            @SuppressLint("Range")
            public List<Order> getAllOrders() {
                List<Order> orderList = new ArrayList<>();
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = null;

                try {
                    cursor = db.query(
                            TABLE_ORDERS,
                            new String[]{
                                    "id",
                                    "name",
                                    "username",
                                    "order_summary",
                                    "phone_number",
                                    "payment_method",
                                    "total_price",
                                    "status",
                                    "date",
                                    "orderPlacedTimestamp"
                            },
                            null, null, null, null, "date DESC"
                    );

                    if (cursor != null && cursor.moveToFirst()) {
                        do {
                            Order order = new Order(
                                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                                    cursor.getString(cursor.getColumnIndexOrThrow("username")),
                                    cursor.getString(cursor.getColumnIndexOrThrow("order_summary")),
                                    cursor.getString(cursor.getColumnIndexOrThrow("phone_number")),
                                    cursor.getString(cursor.getColumnIndexOrThrow("payment_method")),
                                    cursor.getDouble(cursor.getColumnIndexOrThrow("total_price")),
                                    cursor.getString(cursor.getColumnIndexOrThrow("status")),
                                    cursor.getString(cursor.getColumnIndexOrThrow("date")),
                                    cursor.getLong(cursor.getColumnIndexOrThrow("orderPlacedTimestamp"))
                            );
                            orderList.add(order);
                        } while (cursor.moveToNext());
                    }
                } catch (Exception e) {
                    Log.e("DatabaseHelper", "Error getting orders", e);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                    db.close();
                }
                return orderList;
            }

            public void insertProduct(String imageName, String title, String description, double price) {
                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("imageName", imageName);
                values.put("title", title);
                values.put("description", description);
                values.put("price", price);
                db.insert(TABLE_PRODUCTS, null, values);
                db.close();
            }

            public List<ProductDomain> getAllProducts() {
                List<ProductDomain> products = new ArrayList<>();
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS, null);

                if (cursor.moveToFirst()) {
                    do {
                        ProductDomain product = new ProductDomain(
                                cursor.getString(cursor.getColumnIndexOrThrow("imageName")),
                                cursor.getString(cursor.getColumnIndexOrThrow("title")),
                                cursor.getString(cursor.getColumnIndexOrThrow("description")),
                                cursor.getDouble(cursor.getColumnIndexOrThrow("price"))
                        );
                        products.add(product);
                    } while (cursor.moveToNext());
                }
                cursor.close();
                return products;
            }

            // In DatabaseHelper.java
            public boolean updateOrderStatus(int orderId, String newStatus) {
                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("status", newStatus);

                int rowsAffected = db.update(
                        "orders",
                        values,
                        "id = ?",
                        new String[]{String.valueOf(orderId)}
                );

                db.close();
                return rowsAffected > 0;
            }

            public List<Order> getOrdersByStatus(String status) {
                List<Order> orderList = new ArrayList<>();
                SQLiteDatabase db = this.getReadableDatabase();

                Cursor cursor = db.query(
                        TABLE_ORDERS,
                        null,
                        "status = ?",
                        new String[]{status},
                        null, null, null
                );

                if (cursor.moveToFirst()) {
                    do {
                        Order order = new Order(
                                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                                cursor.getString(cursor.getColumnIndexOrThrow("order_summary")),
                                cursor.getString(cursor.getColumnIndexOrThrow("phone_number")),
                                cursor.getString(cursor.getColumnIndexOrThrow("username")),
                                cursor.getString(cursor.getColumnIndexOrThrow("payment_method")),
                                cursor.getDouble(cursor.getColumnIndexOrThrow("total_price")),
                                cursor.getString(cursor.getColumnIndexOrThrow("date")),
                                cursor.getString(cursor.getColumnIndexOrThrow("status")),
                                cursor.getLong(cursor.getColumnIndexOrThrow("orderPlacedTimestamp"))
                        );
                        orderList.add(order);
                    } while (cursor.moveToNext());
                }

                cursor.close();
                db.close();
                return orderList;
            }



        }
