// DatabaseConnector.java
// Provides easy connection and creation of UserContacts database.
package com.deitel.addressbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DatabaseConnector 
{
   // database name
   private static final String DATABASE_NAME = "MoviesDatabase";
      
   private SQLiteDatabase database; // for interacting with the database
   private DatabaseOpenHelper databaseOpenHelper; // creates the database

   // public constructor for DatabaseConnector
   public DatabaseConnector(Context context) 
   {
      // create a new DatabaseOpenHelper
      databaseOpenHelper = 
         new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
   }

   // open the database connection
   public void open() throws SQLException 
   {
      // create or open a database for reading/writing
      database = databaseOpenHelper.getWritableDatabase();
   }

   // close the database connection
   public void close() 
   {
      if (database != null)
         database.close(); // close the database connection
   } 

   // inserts a new contact in the database
   public long insertContact(String title, String director, String genre,  
      String writer, String rating, String year, String duration) 
   {
      ContentValues newContact = new ContentValues();
      newContact.put("title", title);
      newContact.put("director", director);
      newContact.put("genre", genre);
      newContact.put("writer", writer);
      newContact.put("rating", rating);
      newContact.put("year", year);
      newContact.put("duration", duration);

      open(); // open the database
      long rowID = database.insert("movies", null, newContact);
      close(); // close the database
      return rowID;
   } 

   // updates an existing contact in the database
   public void updateContact(long id, String title, String director, 
      String genre, String writer, String rating, String year, String duration) 
   {
      ContentValues editContact = new ContentValues();
      editContact.put("title", title);
      editContact.put("director", director);
      editContact.put("genre", genre);
      editContact.put("writer", writer);
      editContact.put("rating", rating);
      editContact.put("year", year);
      editContact.put("duration", duration);

      open(); // open the database
      database.update("movies", editContact, "_id=" + id, null);
      close(); // close the database
   } // end method updateContact

   // return a Cursor with all contact names in the database
   public Cursor getAllContacts() 
   {
      return database.query("movies", new String[] {"_id", "title"}, 
         null, null, null, null, "title");
   } 

   // return a Cursor containing specified contact's information 
   public Cursor getOneContact(long id) 
   {
      return database.query(
         "movies", null, "_id=" + id, null, null, null, null);
   } 

   // delete the contact specified by the given String name
   public void deleteContact(long id) 
   {
      open(); // open the database
      database.delete("movies", "_id=" + id, null);
      close(); // close the database
   } 
   
   private class DatabaseOpenHelper extends SQLiteOpenHelper 
   {
      // constructor
      public DatabaseOpenHelper(Context context, String name,
         CursorFactory factory, int version) 
      {
         super(context, name, factory, version);
      }

      // creates the contacts table when the database is created
      @Override
      public void onCreate(SQLiteDatabase db) 
      {
         // query to create a new table named contacts
         String createQuery = "CREATE TABLE movies" +
            "(_id integer primary key autoincrement," +
            "title TEXT, director TEXT, genre TEXT, " +
            "writer TEXT, rating TEXT, year TEXT, duration TEXT);";
                  
         db.execSQL(createQuery); // execute query to create the database
      } 

      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion, 
          int newVersion) 
      {
      }
   } // end class DatabaseOpenHelper
} // end class DatabaseConnector


/**************************************************************************
 * (C) Copyright 1992-2014 by Deitel & Associates, Inc. and               *
 * Pearson Education, Inc. All Rights Reserved.                           *
 *                                                                        *
 * DISCLAIMER: The authors and publisher of this book have used their     *
 * best efforts in preparing the book. These efforts include the          *
 * development, research, and testing of the theories and programs        *
 * to determine their effectiveness. The authors and publisher make       *
 * no warranty of any kind, expressed or implied, with regard to these    *
 * programs or to the documentation contained in these books. The authors *
 * and publisher shall not be liable in any event for incidental or       *
 * consequential damages in connection with, or arising out of, the       *
 * furnishing, performance, or use of these programs.                     *
 **************************************************************************/
