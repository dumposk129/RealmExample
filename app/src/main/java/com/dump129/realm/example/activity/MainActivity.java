package com.dump129.realm.example.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dump129.realm.example.R;
import com.dump129.realm.example.database.User;

import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    private Realm realm;
    private RealmAsyncTask transaction;
    private RealmResults<User> result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        saveUser();
    }

    private void saveUser() {
        realm = Realm.getInstance(this);
        transaction = realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                User user = bgRealm.createObject(User.class);
                user.setName("Dump");
                user.setEmail("abc.xzy@gmail.com");
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                // Transaction was a success.
                queryData();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Transaction failed and was automatically canceled.
            }
        });
    }

    private void queryData() {
        result = realm.where(User.class).findAllAsync();
        result.addChangeListener(new RealmChangeListener() {
            @Override
            public void onChange() {
                if (result.isLoaded()) {
                    // Ready
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (transaction != null && !transaction.isCancelled()) {
            transaction.cancel();
        }

        result.removeChangeListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
