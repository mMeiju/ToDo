package com.example.meiju.todo;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class ToDoEditActivity extends AppCompatActivity {
    private Realm mRealm;
    EditText mDateEdit;
    EditText mTitleEdit;
    EditText mDetailEdit;
    Button mDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_edit);
        mRealm = Realm.getDefaultInstance();
        mDateEdit = (EditText) findViewById(R.id.dateEdit);
        mTitleEdit = (EditText) findViewById(R.id.titleEdit);
        mDetailEdit = (EditText) findViewById(R.id.detailEdit);
        mDelete = (Button) findViewById(R.id.deleteButton);

        long todoId = getIntent().getLongExtra("todo_id", -1);
        if (todoId != -1) {
            RealmResults<ToDo> results = mRealm.where(ToDo.class).equalTo("id", todoId).findAll();
            ToDo todo = results.first();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String date = sdf.format(todo.getDate());
            mDateEdit.setText(date);
            mTitleEdit.setText(todo.getTitle());
            mDetailEdit.setText(todo.getDetail());
            mDelete.setVisibility(View.VISIBLE);
        } else {
            mDelete.setVisibility(View.INVISIBLE);
        }
    }

    public void onSaveTapped(View view) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date dateParse = new Date();
        try {
            dateParse = sdf.parse(mDateEdit.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final Date date = dateParse;
        long todoId = getIntent().getLongExtra("todo_id", -1);
        if (todoId != -1) {
            final RealmResults<ToDo> results = mRealm.where(ToDo.class).equalTo("id", todoId).findAll();
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    ToDo todo = results.first();
                    todo.setDate(date);
                    todo.setTitle(mTitleEdit.getText().toString());
                    todo.setDetail(mDetailEdit.getText().toString());
                }
            });
            Snackbar.make(findViewById(android.R.id.content),
                    "Updated!", Snackbar.LENGTH_LONG)
                    .setAction("Return", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).setActionTextColor(Color.YELLOW).show();
        } else {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Number maxId = realm.where(ToDo.class).max("id");
                    long nextId = 0;
                    if (maxId != null) nextId = maxId.longValue() + 1;
                    ToDo todo = realm.createObject(ToDo.class, new Long(nextId));
                    todo.setDate(date);
                    todo.setTitle(mTitleEdit.getText().toString());
                    todo.setDetail(mDetailEdit.getText().toString());
                }
            });
            Toast.makeText(this, "Added!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void onDeleteTapped(View view) {
        final long todoId = getIntent().getLongExtra("todo_id", -1);
        if (todoId != -1) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    ToDo todo = realm.where(ToDo.class).equalTo("id", todoId).findFirst();
                    todo.deleteFromRealm();
                }
            });
            Toast.makeText(this, "Deleted!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
