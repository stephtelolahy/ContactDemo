package com.example.c44787.contactdemo

import android.Manifest
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


//https://www.dev2qa.com/android-add-contact-programmatically-example/

class MainActivity : AppCompatActivity() {

    // Constants

    companion object {
        const val PERMISSION_REQUEST_CODE_READ_CONTACTS = 1
        const val PERMISSION_REQUEST_CODE_WRITE_CONTACTS = 2
    }

    // Properties

    // This is the phone contacts list view's data adapter.
    private lateinit var contactsListDataAdapter: ArrayAdapter<String>

    // Lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create the list view data adapter
        contactsListDataAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1)
        display_phone_contacts_list_view.adapter = contactsListDataAdapter

        // Click this button start add phone contact activity.
        add_phone_contacts_button.setOnClickListener {
            if (!hasPermission(Manifest.permission.WRITE_CONTACTS)) {
                requestPermission(Manifest.permission.WRITE_CONTACTS, PERMISSION_REQUEST_CODE_WRITE_CONTACTS)
            } else {
                addPhoneContact()
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

        settings_phone_contacts_button.setOnClickListener {
            openContactSettings()
        }
    }


    // Private

    private fun openContactSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        refreshPermissionStatus()
    }

    private fun refreshPermissionStatus() {
        switch_read_permission.isChecked = hasPermission(Manifest.permission.READ_CONTACTS)
        switch_write_permission.isChecked = hasPermission(Manifest.permission.WRITE_CONTACTS)
    }

    // All Keys here
    // https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds

    // This method will only insert an empty data to RawContacts.CONTENT_URI
    // The purpose is to get a system generated raw contact id.
    private fun getRawContactId(): Long {
        // Insert an empty contact.
        val contentValues = ContentValues()
        val rawContactUri = contentResolver.insert(ContactsContract.RawContacts.CONTENT_URI, contentValues)
        // Get the newly created contact raw id.
        return ContentUris.parseId(rawContactUri)
    }

    private fun addPhoneContact() {
        // Get android phone contact content provider uri.
        val addContactsUri = ContactsContract.Data.CONTENT_URI

        // Add an empty contact and get the generated id.
        val rowContactId = getRawContactId()

        // Add contact name data.
        insertContactName(addContactsUri, rowContactId, "000FirstName", "000LastName")
        insertContactWorkPhoneNumber(addContactsUri, rowContactId, "0123456789")
        insertContactMobilePhoneNumber(addContactsUri, rowContactId, "0687954321")
        insertContactAddress(addContactsUri, rowContactId, "3 rue des Meaux", "75012", "Nanterre", "France")
        insertContactEmail(addContactsUri, rowContactId, "abc@def.com")
        insertContactJob(addContactsUri, rowContactId, "CompanyName", "JobTitle", "Office B021")
        insertContactNote(addContactsUri, rowContactId, "Note1: Data1")
        insertContactNote(addContactsUri, rowContactId, "Note2: Data2")

        Toast.makeText(this, "Contact successfully added", Toast.LENGTH_LONG).show()
    }

    private fun insertContactName(addContactsUri: Uri, rawContactId: Long, firstName: String, lastName: String) {
        val contentValues = ContentValues()
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
        contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
        contentValues.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, firstName)
        contentValues.put(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, lastName)
        contentResolver.insert(addContactsUri, contentValues)
    }

    private fun insertContactWorkPhoneNumber(addContactsUri: Uri, rawContactId: Long, workNumber: String) {
        val contentValues = ContentValues()
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
        contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
        contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, workNumber)
        contentValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
        contentResolver.insert(addContactsUri, contentValues)
    }

    private fun insertContactMobilePhoneNumber(addContactsUri: Uri, rawContactId: Long, mobileNumber: String) {
        val contentValues = ContentValues()
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
        contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
        contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, mobileNumber)
        contentValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
        contentResolver.insert(addContactsUri, contentValues)
    }

    private fun insertContactAddress(addContactsUri: Uri, rawContactId: Long, street: String, postCode: String, city: String, country: String) {
        val contentValues = ContentValues()
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
        contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
        contentValues.put(ContactsContract.CommonDataKinds.StructuredPostal.STREET, street)
        contentValues.put(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE, postCode)
        contentValues.put(ContactsContract.CommonDataKinds.StructuredPostal.CITY, city)
        contentValues.put(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY, country)
        contentValues.put(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK)
        contentResolver.insert(addContactsUri, contentValues)
    }

    private fun insertContactEmail(addContactsUri: Uri, rawContactId: Long, email: String) {
        val contentValues = ContentValues()
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
        contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
        contentValues.put(ContactsContract.CommonDataKinds.Email.ADDRESS, email)
        contentValues.put(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
        contentResolver.insert(addContactsUri, contentValues)
    }

    private fun insertContactJob(addContactsUri: Uri, rawContactId: Long, company: String, jobTitle: String, officeLocation: String) {
        val contentValues = ContentValues()
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
        contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
        contentValues.put(ContactsContract.CommonDataKinds.Organization.COMPANY, company)
        contentValues.put(ContactsContract.CommonDataKinds.Organization.TITLE, jobTitle)
        contentValues.put(ContactsContract.CommonDataKinds.Organization.OFFICE_LOCATION, officeLocation)
        contentResolver.insert(addContactsUri, contentValues)
    }

    private fun insertContactNote(addContactsUri: Uri, rawContactId: Long, notes: String) {
        val contentValues = ContentValues()
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
        contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)
        contentValues.put(ContactsContract.CommonDataKinds.Note.NOTE, notes)
        contentResolver.insert(addContactsUri, contentValues)
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
                    addPhoneContact()
                }
            } else {
                Toast.makeText(applicationContext, "You denied permission.", Toast.LENGTH_LONG).show()
            }
        }

        refreshPermissionStatus()
    }

    // https://developer.android.com/training/contacts-provider/retrieve-names
    private fun getAllPhoneContacts(): List<String> {
        // Store all phone contacts list.
        // Each String format is " DisplayName \r\n Phone Number \r\n Phone Type " ( Jerry \r\n 111111 \r\n Home) .
        val phoneContacts = ArrayList<String>()

        // Get query phone contacts cursor object.
        val readContactsUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val cursor = contentResolver.query(readContactsUri, null, null, null, null)
        cursor?.let {
            cursor.moveToFirst()

            // Loop in the phone contacts cursor to add each contacts in phoneContactsList.
            do {
                // Get contact display name.
                val displayNameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val userDisplayName = cursor.getString(displayNameIndex)

                // Get contact phone number.
                val phoneNumberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val phoneNumber = cursor.getString(phoneNumberIndex)

                // Get contact phone type.

                val phoneTypeColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)
                val phoneTypeInt = cursor.getInt(phoneTypeColumnIndex)

                val phoneTypeStr = when (phoneTypeInt) {
                    ContactsContract.CommonDataKinds.Phone.TYPE_HOME -> "Home"
                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE -> "Mobile"
                    ContactsContract.CommonDataKinds.Phone.TYPE_WORK -> "Work"
                    else -> "Mobile"
                }

                val contactStringBuf = StringBuffer()
                contactStringBuf.append(userDisplayName)
                contactStringBuf.append("\r\n")
                contactStringBuf.append(phoneNumber)
                contactStringBuf.append("\r\n")
                contactStringBuf.append(phoneTypeStr)

                phoneContacts.add(contactStringBuf.toString())
            } while (cursor.moveToNext())
        }
        cursor.close()

        return phoneContacts
    }

    // Read and display android phone contacts in list view.
    private fun readPhoneContacts() {
        // Refresh the listView to display read out phone contacts.
        contactsListDataAdapter.clear()
        contactsListDataAdapter.addAll(getAllPhoneContacts())
        contactsListDataAdapter.notifyDataSetChanged()
    }
}
