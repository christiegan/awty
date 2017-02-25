package edu.washington.clgan.awty;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    boolean fStart = true; //has not started, show start
    EditText editText1;
    String message;
    EditText editText2;
    String phone;
    EditText editText3;
    String min;
    Button button;
    int minutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},1);
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String m = sharedPref.getString("message", "");
        String p = sharedPref.getString("phone number", "");
        int min = sharedPref.getInt("minutes", 0);
        fStart = sharedPref.getBoolean("button", true);
        editText1 = (EditText) findViewById(R.id.editText);
        editText1.setText(m);
        editText2 = (EditText) findViewById(R.id.editText2);
        editText2.setText(p);
        editText3 = (EditText) findViewById(R.id.editText3);
        editText3.setText(Integer.toString(min));
        button = (Button) findViewById(R.id.button);
        if(fStart){
            button.setText("Start");
        }else{
            button.setText("Stop");
        }
    }

    private boolean isValidMobile(String phone) {
        boolean check=false;
        if(!Pattern.matches("[a-zA-Z]+", phone)) {
            if(phone.length() < 6 || phone.length() > 13) {
                // if(phone.length() != 10) {
                check = false;
            } else {
                check = true;
            }
        } else {
            check=false;
        }
        return check;
    }

    public void nag(View view){
        fStart = !fStart;
        String errMsg =new String();
        boolean fError = false;
        Button startButton = (Button)findViewById(R.id.button);

        message = editText1.getText().toString();
        phone = editText2.getText().toString();
        min = editText3.getText().toString();
        minutes = Integer.parseInt(min);

        if (message.equals(""))
        {
            editText1.setError("Message is required");
            errMsg = errMsg + "Error: Message is required. ";
            fError =true;
        }

        if (!isValidMobile(phone))
        {
            editText2.setError("Phone number is required. Ex: (425)234-5555");
            errMsg = errMsg + "Error: Phone number is required. Ex: (425)234-5555. ";
            fError =true;
        }

        if (minutes <=0)
        {
            editText3.setError("Minutes must be bigger than 0");
            errMsg = errMsg + "Error: Minutes must be bigger than 0. ";
            fError =true;
        }

        if(!fError) {
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("message", message);
            editor.putString("phone number", phone);
            editor.putInt("minutes", minutes);
            editor.putBoolean("button", fStart);
            editor.commit();
            Intent i = new Intent(this, AlarmReceiver.class);
            i.putExtra("message", message);
            i.putExtra("phone", phone);
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + (1000 * 60 * minutes), (1000 * 60 * minutes), pi);
            
            if (!fStart) {
                startButton.setText("Stop");
            } else {
                startButton.setText("Start");
                am.cancel(pi); // cancel any existing alarms
            }
        } else {
            Log.i("info", errMsg);
            Toast.makeText(getApplicationContext(), "Unable to start: " + errMsg, Toast.LENGTH_SHORT).show();
        }
    }
}
