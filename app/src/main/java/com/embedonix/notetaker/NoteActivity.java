package com.embedonix.notetaker;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class NoteActivity extends AppCompatActivity {

    private boolean isViewingOrUpdating;
    private long noteCreationTime;
    private String mFileName;
    private Note mLoadedNote = null;

    private EditText mEtTitle;
    private EditText mEtContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        mEtTitle = (EditText) findViewById(R.id.note_et_title);
        mEtContent = (EditText) findViewById(R.id.note_et_content);

        //check if view/edit note bundle is set, otherwise user wants to create new note
        mFileName = getIntent().getStringExtra("NOTE_FILE_NAME");
        if(mFileName != null && !mFileName.isEmpty() && mFileName.endsWith(".bin")) {
           mLoadedNote = Utilities.getNoteByFileName(getApplicationContext(), mFileName);
            if (mLoadedNote != null) {
                //update the widgets from the loaded note
                mEtTitle.setText(mLoadedNote.getTitle());
                mEtContent.setText(mLoadedNote.getContent());
                noteCreationTime = mLoadedNote.getDateTime();
                isViewingOrUpdating = true;
            }
        } else {
            noteCreationTime = System.currentTimeMillis();
            isViewingOrUpdating = false;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(isViewingOrUpdating) { //user is viewing or updating a note
            getMenuInflater().inflate(R.menu.menu_note_view, menu);
        } else { //user wants to create a new note
            getMenuInflater().inflate(R.menu.menu_note_add, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_save_note: //save the note
            case R.id.action_update: //or update :P
                validateAndSaveNote();
                break;

            case R.id.action_delete:
                //ask user if he really wants to delete the note!
                AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle("delete note")
                .setMessage("really delete the note?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mLoadedNote != null && Utilities.deleteFile(getApplicationContext(), mFileName)) {
                            Toast.makeText(NoteActivity.this, mLoadedNote.getTitle() + " is deleted"
                                    , Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(NoteActivity.this, "can not delete " + mLoadedNote.getTitle()
                                    , Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    }
                })
                .setNegativeButton("NO", null);

                dialog.show();
                break;

            case R.id.action_cancel: //cancel the note
                finish(); //just go back :P
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void validateAndSaveNote() {

        //get the content of widgets to make a note object
        String title = mEtTitle.getText().toString();
        String content = mEtContent.getText().toString();

        //see if user has entered anything :D lol
        if(title.isEmpty()) { //title
            Toast.makeText(NoteActivity.this, "please enter a title!"
                    , Toast.LENGTH_SHORT).show();
            return;
        }

        if(content.isEmpty()) { //content
            Toast.makeText(NoteActivity.this, "please enter a content for your note!"
                    , Toast.LENGTH_SHORT).show();
            return;
        }

        //finally save the note!
        Utilities.saveNote(this, new Note(System.currentTimeMillis(), title, content));
        finish(); //exit the activity, should return us to MainActivity
    }

}