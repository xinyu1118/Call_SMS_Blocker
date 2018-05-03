package io.github.call_sms_blocker;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import io.github.privacysecurer.core.ContactCallback;
import io.github.privacysecurer.core.ContactEvent;
import io.github.privacysecurer.core.Event;
import io.github.privacysecurer.core.MessageCallback;
import io.github.privacysecurer.core.MessageEvent;
import io.github.privacysecurer.core.UQI;

public class MainActivity extends AppCompatActivity {
    // indicating Phone, SMS, Phone & SMS options
    String result;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final UQI uqi = new UQI(this);

        final EditText text = (EditText)findViewById(R.id.phoneno);
        Button btn = (Button)findViewById(R.id.button1);
        Spinner sp1 = (Spinner)findViewById(R.id.spinner1);

        final String [] array = getResources().getStringArray(R.array.block);
        result = array[0];

        //final PhoneLog app=(PhoneLog) getApplicationContext();
        final SharedPreferences mpref = this.getSharedPreferences("BLOCK",MODE_PRIVATE);

        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                result = array[arg2];
                //Log.d("Log","arg1=="+arg1+" arg2"+arg2+" arg3"+arg3+" arg0"+arg0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                result = array[0];
            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor mSharedEditor = mpref.edit();
                mSharedEditor.putString("phoneno",text.getText().toString());
                mSharedEditor.putString("phonesms", result);
                mSharedEditor.commit();

                String caller = text.getText().toString();
                ((TextView)findViewById(R.id.textView1)).setText(caller+" "+result+" is blocked");

                // event handling
                Event contactEvent = new ContactEvent.ContactEventBuilder()
                        .setEventType(Event.Call_Check_Unwanted)
                        .setCaller(caller)
                        .build();

                Event messageEvent = new MessageEvent.MessageEventBuilder()
                        .setEventType(Event.Message_Check_Unwanted)
                        .setCaller(caller)
                        .build();

                switch (result) {
                    case "Phone":
                        uqi.addEventListener(contactEvent, new ContactCallback() {
							@Override
							public void onEvent() {
							    showDialog("Unwanted calls!");
							}
						});
                        Log.d("Log", "Phone selected.");
                        break;

                    case "SMS":
                        uqi.addEventListener(messageEvent, new MessageCallback() {
                            @Override
                            public void onEvent() {
                                showDialog("Unwanted messages!");
                            }
                        });
                        Log.d("Log", "SMS selected.");
                        break;

                    case "Phone & SMS":
                        uqi.addEventListener(contactEvent, new ContactCallback() {
                            @Override
                            public void onEvent() {
                                showDialog("Unwanted calls!");
                            }
                        });
                        uqi.addEventListener(messageEvent, new MessageCallback() {
                            @Override
                            public void onEvent() {
                                showDialog("Unwanted messages!");
                            }
                        });
                        Log.d("Log", "Phone & SMS selected.");
                        break;

                    default:
                        Log.d("Log", "No blocking operation selected.");

                }
            }
        });
    }

    private void showDialog(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setMessage(message);
        alertDialog.show();
    }
}
