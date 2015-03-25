// DetailsFragment.java
// Displays one contact's details
package com.deitel.addressbook;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailsFragment extends Fragment
{
   // callback methods implemented by MainActivity  
   public interface DetailsFragmentListener
   {
      // called when a contact is deleted
      public void onContactDeleted();
      
      // called to pass Bundle of contact's info for editing
      public void onEditContact(Bundle arguments);
   }
   
   private DetailsFragmentListener listener;
   
   private long rowID = -1; // selected contact's rowID
   private TextView titleTextView; // displays contact's name 
   private TextView directorTextView; // displays contact's phone
   private TextView genreTextView; // displays contact's email
   private TextView releaseDateTextView; // displays contact's street
   private TextView ratingTextView; // displays contact's city
   private TextView yearTextView; // displays contact's state
   private TextView durationTextView; // displays contact's zip
   
   // set DetailsFragmentListener when fragment attached   
   @Override
   public void onAttach(Activity activity)
   {
      super.onAttach(activity);
      listener = (DetailsFragmentListener) activity;
   }
   
   // remove DetailsFragmentListener when fragment detached
   @Override
   public void onDetach()
   {
      super.onDetach();
      listener = null;
   }

   // called when DetailsFragmentListener's view needs to be created
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
   {
      super.onCreateView(inflater, container, savedInstanceState);  
      setRetainInstance(true); // save fragment across config changes

      // if DetailsFragment is being restored, get saved row ID
      if (savedInstanceState != null) 
         rowID = savedInstanceState.getLong(MainActivity.ROW_ID);
      else 
      {
         // get Bundle of arguments then extract the contact's row ID
         Bundle arguments = getArguments(); 
         
         if (arguments != null)
            rowID = arguments.getLong(MainActivity.ROW_ID);
      }
         
      // inflate DetailsFragment's layout
      View view = 
         inflater.inflate(R.layout.fragment_details, container, false);               
      setHasOptionsMenu(true); // this fragment has menu items to display

      // get the EditTexts
      titleTextView = (TextView) view.findViewById(R.id.titleTextView);
      directorTextView = (TextView) view.findViewById(R.id.directorTextView);
      genreTextView = (TextView) view.findViewById(R.id.genreTextView);
      releaseDateTextView = (TextView) view.findViewById(R.id.releaseDateTextView);
      ratingTextView = (TextView) view.findViewById(R.id.ratingTextView);
      yearTextView = (TextView) view.findViewById(R.id.yearTextView);
      durationTextView = (TextView) view.findViewById(R.id.durationTextView);
      return view;
   }
   
   // called when the DetailsFragment resumes
   @Override
   public void onResume()
   {
      super.onResume();
      new LoadContactTask().execute(rowID); // load contact at rowID
   } 

   // save currently displayed contact's row ID
   @Override
   public void onSaveInstanceState(Bundle outState) 
   {
       super.onSaveInstanceState(outState);
       outState.putLong(MainActivity.ROW_ID, rowID);
   }

   // display this fragment's menu items
   @Override
   public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
   {
      super.onCreateOptionsMenu(menu, inflater);
      inflater.inflate(R.menu.fragment_details_menu, menu);
   }

   // handle menu item selections
   @Override
   public boolean onOptionsItemSelected(MenuItem item) 
   {
      switch (item.getItemId())
      {
         case R.id.action_edit: 
            // create Bundle containing contact data to edit
            Bundle arguments = new Bundle();
            arguments.putLong(MainActivity.ROW_ID, rowID);
            arguments.putCharSequence("title", titleTextView.getText());
            arguments.putCharSequence("director", directorTextView.getText());
            arguments.putCharSequence("genre", genreTextView.getText());
            arguments.putCharSequence("releaseDate", releaseDateTextView.getText());
            arguments.putCharSequence("rating", ratingTextView.getText());
            arguments.putCharSequence("year", yearTextView.getText());
            arguments.putCharSequence("duration", durationTextView.getText());            
            listener.onEditContact(arguments); // pass Bundle to listener
            return true;
         case R.id.action_delete:
            deleteContact();
            return true;
      }
      
      return super.onOptionsItemSelected(item);
   } 
   
   // performs database query outside GUI thread
   private class LoadContactTask extends AsyncTask<Long, Object, Cursor> 
   {
      DatabaseConnector databaseConnector = 
         new DatabaseConnector(getActivity());

      // open database & get Cursor representing specified contact's data
      @Override
      protected Cursor doInBackground(Long... params)
      {
         databaseConnector.open();
         return databaseConnector.getOneContact(params[0]);
      } 

      // use the Cursor returned from the doInBackground method
      @Override
      protected void onPostExecute(Cursor result)
      {
         super.onPostExecute(result);
         result.moveToFirst(); // move to the first item 
   
         // get the column index for each data item
         int titleIndex = result.getColumnIndex("title");
         int directorIndex = result.getColumnIndex("director");
         int genreIndex = result.getColumnIndex("genre");
         int releaseDateIndex = result.getColumnIndex("releaseDate");
         int ratingIndex = result.getColumnIndex("rating");
         int yearIndex = result.getColumnIndex("year");
         int durationIndex = result.getColumnIndex("duration");
   
         // fill TextViews with the retrieved data
         titleTextView.setText(result.getString(titleIndex));
         directorTextView.setText(result.getString(directorIndex));
         genreTextView.setText(result.getString(genreIndex));
         releaseDateTextView.setText(result.getString(releaseDateIndex));
         ratingTextView.setText(result.getString(ratingIndex));
         yearTextView.setText(result.getString(yearIndex));
         durationTextView.setText(result.getString(durationIndex));
   
         result.close(); // close the result cursor
         databaseConnector.close(); // close database connection
      } // end method onPostExecute
   } // end class LoadContactTask

   // delete a contact
   private void deleteContact()
   {         
      // use FragmentManager to display the confirmDelete DialogFragment
      confirmDelete.show(getFragmentManager(), "confirm delete");
   } 

   // DialogFragment to confirm deletion of contact
   private DialogFragment confirmDelete = 
      new DialogFragment()
      {
         // create an AlertDialog and return it
         @Override
         public Dialog onCreateDialog(Bundle bundle)
         {
            // create a new AlertDialog Builder
            AlertDialog.Builder builder = 
               new AlertDialog.Builder(getActivity());
      
            builder.setTitle(R.string.confirm_title); 
            builder.setMessage(R.string.confirm_message);
      
            // provide an OK button that simply dismisses the dialog
            builder.setPositiveButton(R.string.button_delete,
               new DialogInterface.OnClickListener()
               {
                  @Override
                  public void onClick(
                     DialogInterface dialog, int button)
                  {
                     final DatabaseConnector databaseConnector = 
                        new DatabaseConnector(getActivity());
      
                     // AsyncTask deletes contact and notifies listener
                     AsyncTask<Long, Object, Object> deleteTask =
                        new AsyncTask<Long, Object, Object>()
                        {
                           @Override
                           protected Object doInBackground(Long... params)
                           {
                              databaseConnector.deleteContact(params[0]); 
                              return null;
                           } 
      
                           @Override
                           protected void onPostExecute(Object result)
                           {                                 
                              listener.onContactDeleted();
                           }
                        }; // end new AsyncTask
      
                     // execute the AsyncTask to delete contact at rowID
                     deleteTask.execute(new Long[] { rowID });               
                  } // end method onClick
               } // end anonymous inner class
            ); // end call to method setPositiveButton
            
            builder.setNegativeButton(R.string.button_cancel, null);
            return builder.create(); // return the AlertDialog
         }
      }; // end DialogFragment anonymous inner class
} // end class DetailsFragment


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
