package com.msmith.messagegramsender;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

/**
 * Created by morgan on 8/12/16.
 */
public class ContactCallbackHandler implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {
    private View contactView;
    private String contactNumber;
    private String contactName;
    private LoaderManager loaderManager;


    public ContactCallbackHandler(View contactView, LoaderManager loaderManager) {
        this.contactView = contactView;
        this.loaderManager = loaderManager;

    }

    @SuppressLint("InlinedApi")
    private final static String[] FROM_COLUMNS = {
            Build.VERSION.SDK_INT
                    >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                    ContactsContract.Contacts.DISPLAY_NAME
    };
    /*
     * Defines an array that contains resource ids for the layout views
     * that get the Cursor column contents. The id is pre-defined in
     * the Android framework, so it is prefaced with "android.R.id"
     */
    private final static int[] TO_IDS = {
            android.R.id.text1
    };
    // Define global mutable variables
    // Define a ListView object
    ListView mContactsList;
    // Define variables for the contact the user selects
    // The contact's _ID value
    long mContactId;
    // The contact's LOOKUP_KEY
    String mContactKey;
    // A content URI for the selected contact
    Uri mContactUri;
    // An adapter that binds the result Cursor to the ListView
    private SimpleCursorAdapter mCursorAdapter;

    // Defines a variable for the search string
    private String mSearchString;
    // Defines the array to hold values that replace the ?
    private String[] mSelectionArgs = {mSearchString};

    @SuppressLint("InlinedApi")
    private static final String SELECTION =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ? AND " + ContactsContract.Contacts.HAS_PHONE_NUMBER + " = 1" :
                    ContactsContract.Contacts.DISPLAY_NAME + " LIKE ? AND " + ContactsContract.Contacts.HAS_PHONE_NUMBER + " = 1";

    @SuppressLint("InlinedApi")
    private static final String[] PROJECTION =
            {
                    ContactsContract.Contacts._ID,

                    ContactsContract.Contacts.LOOKUP_KEY,
                    Build.VERSION.SDK_INT
                            >= Build.VERSION_CODES.HONEYCOMB ?
                            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                            ContactsContract.Contacts.DISPLAY_NAME


            };


    public void init() {

        SearchView searchView = (SearchView) contactView.findViewById(R.id.search_view);
        searchView.setQueryHint("Search View");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                doMySearch(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                doMySearch(newText);
                return false;
            }


        });
    }

    private void doMySearch(String query) {
        setupList(query);
    }

    private void setupList(String query) {
        Loader loader = loaderManager.getLoader(0);
        if (loader != null)
            loaderManager.destroyLoader(0);
        mSearchString = query;
        mContactsList = (ListView) contactView.findViewById(R.id.list_results);
        mCursorAdapter = new SimpleCursorAdapter(
                contactView.getContext(),
                R.layout.contacts_search_item,
                null,
                FROM_COLUMNS, TO_IDS,
                0);

        mContactsList.setAdapter(mCursorAdapter);

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor = ((SimpleCursorAdapter) parent.getAdapter()).getCursor();
                String lookup_id = cursor.getString(1);
                mContactId = id;
                mContactKey = lookup_id;

                Map<String, String> contactInfo = ContactUtils.getContactDetail(contactView.getContext(), lookup_id);
                contactName = contactInfo.get("contactName");
                contactNumber = contactInfo.get("contactNumber");

                TextView nameText = (TextView) contactView.findViewById(R.id.contact_name);
                TextView numText = (TextView) contactView.findViewById(R.id.number);
                nameText.setText(contactName);
                numText.setText(contactNumber);
                ListView res = (ListView) contactView.findViewById(R.id.list_results);
                res.setAdapter(null);
                contactView.findViewById(R.id.search_view).clearFocus();


            }
        };
        mContactsList.setOnItemClickListener(itemClickListener);
        loaderManager.initLoader(0, null, this);


    }

    public String getContactName() {
        return contactName;
    }

    public String getContactKey() {
        return mContactKey;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        /*
         * Makes search string into pattern and
         * stores it in the selection array
         */


        mSelectionArgs[0] = "%" + mSearchString + "%";
        // Starts the query
        return new CursorLoader(
                mContactsList.getContext(),
                ContactsContract.Contacts.CONTENT_URI,
                PROJECTION,
                SELECTION,
                mSelectionArgs,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
