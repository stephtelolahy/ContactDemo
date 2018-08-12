package com.example.c44787.contactdemo

import android.Manifest
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.ContactsContract
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileWriter


//https://www.dev2qa.com/android-add-contact-programmatically-example/

class MainActivity : AppCompatActivity() {

    // Constants

    companion object {
        const val PERMISSION_REQUEST_CODE_READ_CONTACTS = 1
        const val PERMISSION_REQUEST_CODE_WRITE_CONTACTS = 2
        const val PERMISSION_REQUEST_CODE_MAKE_CALL = 3
        const val PERMISSION_REQUEST_CODE_SEND_SMS = 4
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
                requestPermission(arrayOf(Manifest.permission.WRITE_CONTACTS), PERMISSION_REQUEST_CODE_WRITE_CONTACTS)
            } else {
                addPhoneContact()
            }
        }

        // Click this button to get and display phone contacts in the list view.
        read_phone_contacts_button.setOnClickListener {
            if (!hasPermission(Manifest.permission.READ_CONTACTS)) {
                requestPermission(arrayOf(Manifest.permission.READ_CONTACTS), PERMISSION_REQUEST_CODE_READ_CONTACTS)
            } else {
                readPhoneContacts()
            }
        }

        settings_phone_contacts_button.setOnClickListener {
            openContactSettings()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.share -> shareContactVCardFile()
            R.id.call -> callContact()
            R.id.message -> sendSMSToContact()
            R.id.email -> sendEmailToContact()
            R.id.direction -> showContactAddress()
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Actions

    private fun showContactAddress(): Boolean {
        val address = "3-5, Place Victor Hugo, 60180 Nogent-sur-Oise"
        val intent = Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q=$address"))
        startActivity(intent)
        return true
    }

    private fun sendEmailToContact(): Boolean {
        val email = "abc@gmail.com"
        val intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null))
        startActivity(Intent.createChooser(intent, "Send Email"))
        return true
    }

    private fun sendSMSToContact(): Boolean {
        if (!hasPermission(Manifest.permission.SEND_SMS)) {
            requestPermission(arrayOf(Manifest.permission.SEND_SMS), PERMISSION_REQUEST_CODE_SEND_SMS)
            return true
        }

        val phoneNumber = "0781824530"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$phoneNumber"))
        intent.putExtra("sms_body", "")
        startActivity(intent)
        return true
    }

    private fun callContact(): Boolean {
        if (!hasPermission(Manifest.permission.CALL_PHONE)) {
            requestPermission(arrayOf(Manifest.permission.CALL_PHONE), PERMISSION_REQUEST_CODE_MAKE_CALL)
            return true
        }

        val phoneNumber = "0781824530"
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$phoneNumber")
        startActivity(intent)
        return true
    }

    /* Checks if external storage is available for read and write */
    private fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    private fun shareContactVCardFile(): Boolean {

        if (!isExternalStorageWritable()) {
            Toast.makeText(this, "External storage not writable", Toast.LENGTH_SHORT).show()
            return true
        }

        // TODO: use vCard library
        // https://stackoverflow.com/questions/13702357/how-to-generate-a-vcf-file-from-an-object-which-contains-contact-detail-and-obj

        val firstName = "000FirstName"
        val lastName = "000Surname"
        val companyName = "000Company"
        val jobTitle = "Title"
        val workPhone = "0123456789"
        val homePhone = "065432178"
        val street = "18 rue des Bois"
        val city = "Bonn"
        val state = "State"
        val postCode = "54089"
        val country = "Italia"
        val email = "user@company.com"

        try {

            val vcfFile = File(getExternalFilesDir(null), "generated.vcf")

            val fw = FileWriter(vcfFile)
            fw.write("BEGIN:VCARD\r\n")
            fw.write("VERSION:3.0\r\n")
            fw.write("N:$lastName;$firstName\r\n")
            fw.write("FN:$firstName $lastName\r\n")
            fw.write("ORG:$companyName\r\n")
            fw.write("TITLE:$jobTitle\r\n")
            fw.write("TEL;TYPE=WORK,VOICE:$workPhone\r\n")
            fw.write("TEL;TYPE=HOME,VOICE:$homePhone\r\n")
            fw.write("ADR;TYPE=WORK:;;$street;$city;$state;$postCode;$country\r\n")
            fw.write("EMAIL;TYPE=PREF,INTERNET:$email\r\n")
            fw.write("END:VCARD\r\n")
            fw.close()

            val intent = Intent()
            intent.action = android.content.Intent.ACTION_SEND
            // https://stackoverflow.com/questions/38200282/android-os-fileuriexposedexception-file-storage-emulated-0-test-txt-exposed
            val uri = FileProvider.getUriForFile(this, "com.example.c44787.contactdemo.fileprovider", vcfFile)
            intent.type = "text/x-vcard"
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(intent, "Share using"))

        } catch (e: Exception) {
            Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
        return true
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
    private fun requestPermission(permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(this, permissions, requestCode)
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
        val list = ArrayList<String>()
        val cursor = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                arrayOf(ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.CommonDataKinds.Email.ADDRESS),
                ContactsContract.Data.MIMETYPE + " = ?",
                arrayOf(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE),
                null)
        while (cursor.moveToNext()) {
            val name = cursor.getString(0)
            val email = cursor.getString(1)
            list.add("$name\n$email")
        }
        cursor.close()
        return list
    }

    // Read and display android phone contacts in list view.
    private fun readPhoneContacts() {
        // Refresh the listView to display read out phone contacts.
        contactsListDataAdapter.clear()
        contactsListDataAdapter.addAll(getAllPhoneContacts())
        contactsListDataAdapter.notifyDataSetChanged()
    }
}
