package com.example.c44787.contactdemo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    // Constants

    private val PERMISSION_REQUEST_CODE_READ_CONTACTS = 1
    private val PERMISSION_REQUEST_CODE_WRITE_CONTACTS = 2

    // Properties

    // Store all phone contacts list.
    // Each String format is " DisplayName \r\n Phone Number \r\n Phone Type " ( Jerry \r\n 111111 \r\n Home) .
    val phoneContacts = ArrayList<String>()

    // This is the phone contacts list view's data adapter.
    private lateinit var contactsListDataAdapter: ArrayAdapter<String>

    // Lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        updatePermissionStatus()

        // Create the list view data adapter
        contactsListDataAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1)
        display_phone_contacts_list_view.adapter = contactsListDataAdapter

        // Click this button start add phone contact activity.
        add_phone_contacts_button.setOnClickListener {
            if (!hasPermission(Manifest.permission.WRITE_CONTACTS)) {
                requestPermission(Manifest.permission.WRITE_CONTACTS, PERMISSION_REQUEST_CODE_WRITE_CONTACTS)
            } else {
                addPhoneContacts()
            }
        }

        // Click this button to get and display phone contacts in the list view.
        read_phone_contacts_button.setOnClickListener {
            if (!hasPermission(Manifest.permission.READ_CONTACTS)) {
                requestPermission(Manifest.permission.READ_CONTACTS, PERMISSION_REQUEST_CODE_READ_CONTACTS)
            } else {
                readPhoneContacts()
            }
        }
    }


    // Private

    private fun updatePermissionStatus() {
        permission_text_view.text = "WRITE_CONTACTS=${hasPermission(Manifest.permission.WRITE_CONTACTS)}" +
                "\nREAD_CONTACTS=${hasPermission(Manifest.permission.READ_CONTACTS)}"
    }

    private fun addPhoneContacts() {
        //AddPhoneContactActivity.start(applicationContext);
    }

    // Check whether user has phone contacts manipulation permission or not.
    private fun hasPermission(permission: String): Boolean {
        // Here, thisActivity is the current activity
        return if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast.makeText(this, "Show an explanation to the user", Toast.LENGTH_LONG).show()
                false
            } else {
                // No explanation needed, we can request the permission.
                false
            }
        } else {
            // Permission has already been granted
            true
        }
    }

    // Request a runtime permission to app user.
    private fun requestPermission(permission: String, requestCode: Int) {
        val requestPermissionArray = arrayOf(permission)
        ActivityCompat.requestPermissions(this, requestPermissionArray, requestCode)
    }

    // After user select Allow or Deny button in request runtime permission dialog
    // , this method will be invoked.

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty()) {
            val grantResult = grantResults[0]

            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                if (requestCode == PERMISSION_REQUEST_CODE_READ_CONTACTS) {
                    // If user grant read contacts permission.
                    readPhoneContacts()
                } else if (requestCode == PERMISSION_REQUEST_CODE_WRITE_CONTACTS) {
                    // If user grant write contacts permission then start add phone contact activity.
                    addPhoneContacts()
                }
            } else {
                Toast.makeText(applicationContext, "You denied permission.", Toast.LENGTH_LONG).show()
            }
        }

        updatePermissionStatus()
    }

    // Read and display android phone contacts in list view.
    private fun readPhoneContacts() {
/*
        // First empty current phone contacts list data.
        int size = phoneContactsList . size ();
        for (int i = 0;i < size;i++)
        {
            phoneContactsList.remove(i);
            i--;
            size = phoneContactsList.size();
        }

        // Get query phone contacts cursor object.
        Uri readContactsUri = ContactsContract . CommonDataKinds . Phone . CONTENT_URI;
        Cursor cursor = getContentResolver ().query(readContactsUri, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();

            // Loop in the phone contacts cursor to add each contacts in phoneContactsList.
            do {
                // Get contact display name.
                int displayNameIndex = cursor . getColumnIndex (ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String userDisplayName = cursor . getString (displayNameIndex);

                // Get contact phone number.
                int phoneNumberIndex = cursor . getColumnIndex (ContactsContract.CommonDataKinds.Phone.NUMBER);
                String phoneNumber = cursor . getString (phoneNumberIndex);

                // Get contact phone type.
                String phoneTypeStr = "Mobile";
                int phoneTypeColumnIndex = cursor . getColumnIndex (ContactsContract.CommonDataKinds.Phone.TYPE);
                int phoneTypeInt = cursor . getInt (phoneTypeColumnIndex);
                if (phoneTypeInt == ContactsContract.CommonDataKinds.Phone.TYPE_HOME) {
                    phoneTypeStr = "Home";
                } else if (phoneTypeInt == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
                    phoneTypeStr = "Mobile";
                } else if (phoneTypeInt == ContactsContract.CommonDataKinds.Phone.TYPE_WORK) {
                    phoneTypeStr = "Work";
                }

                StringBuffer contactStringBuf = new StringBuffer();
                contactStringBuf.append(userDisplayName);
                contactStringBuf.append("\r\n");
                contactStringBuf.append(phoneNumber);
                contactStringBuf.append("\r\n");
                contactStringBuf.append(phoneTypeStr);

                phoneContactsList.add(contactStringBuf.toString());
            } while (cursor.moveToNext());

            // Refresh the listview to display read out phone contacts.
            contactsListDataAdapter.notifyDataSetChanged();
        }
        */
    }
}
