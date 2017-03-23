package com.example.sadashivsinha.mprosmart.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.sadashivsinha.mprosmart.R;
import com.example.sadashivsinha.mprosmart.SharedPreference.PreferenceManager;
import com.example.sadashivsinha.mprosmart.font.HelveticaRegular;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LockScreen extends AppCompatActivity implements View.OnClickListener {

    ImageButton pin_one, pin_two, pin_three, pin_four;
    Button btn_one, btn_two, btn_three, btn_four, btn_five, btn_six, btn_seven, btn_eight, btn_nine, btn_zero;
    HelveticaRegular btn_reload, btn_next, text_enter_pin;
    PreferenceManager pm;
    String lockScreenPin, enteredPin;
    int enteredPinCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getBooleanExtra("EXIT", false))
        {
            finish();
        }
        setContentView(R.layout.activity_lock_screen);

        pm = new PreferenceManager(getApplicationContext());

        pin_one = (ImageButton) findViewById(R.id.pin_one);
        pin_two = (ImageButton) findViewById(R.id.pin_two);
        pin_three = (ImageButton) findViewById(R.id.pin_three);
        pin_four = (ImageButton) findViewById(R.id.pin_four);

        enteredPin = "";

        setTag("", pin_one);
        setTag("", pin_two);
        setTag("", pin_three);
        setTag("", pin_four);

        btn_one = (Button) findViewById(R.id.btn_one);
        btn_two = (Button) findViewById(R.id.btn_two);
        btn_three = (Button) findViewById(R.id.btn_three);
        btn_four = (Button) findViewById(R.id.btn_four);
        btn_five = (Button) findViewById(R.id.btn_five);
        btn_six = (Button) findViewById(R.id.btn_six);
        btn_seven = (Button) findViewById(R.id.btn_seven);
        btn_eight = (Button) findViewById(R.id.btn_eight);
        btn_nine = (Button) findViewById(R.id.btn_nine);
        btn_zero = (Button) findViewById(R.id.btn_zero);

        btn_reload = (HelveticaRegular) findViewById(R.id.btn_reload);
        btn_next = (HelveticaRegular) findViewById(R.id.btn_next);

        lockScreenPin = pm.getString("lockScreenPin");

        text_enter_pin = (HelveticaRegular) findViewById(R.id.text_enter_pin);

        if(!pm.getBoolean("lockScreenEnable"))
        {
            text_enter_pin.setText("Set New Pin");
        }


        btn_one.setOnClickListener(this);
        btn_two.setOnClickListener(this);
        btn_three.setOnClickListener(this);
        btn_four.setOnClickListener(this);
        btn_five.setOnClickListener(this);
        btn_six.setOnClickListener(this);
        btn_seven.setOnClickListener(this);
        btn_eight.setOnClickListener(this);
        btn_nine.setOnClickListener(this);
        btn_zero.setOnClickListener(this);

        btn_next.setOnClickListener(this);
        btn_reload.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_one:
            {
                checkAndSetAstericks("1");
            }
            break;
            case R.id.btn_two:
            {
                checkAndSetAstericks("2");
            }
            break;
            case R.id.btn_three:
            {
                checkAndSetAstericks("3");
            }
            break;
            case R.id.btn_four:
            {
                checkAndSetAstericks("4");
            }
            break;
            case R.id.btn_five:
            {
                checkAndSetAstericks("5");
            }
            break;
            case R.id.btn_six:
            {
                checkAndSetAstericks("6");
            }
            break;
            case R.id.btn_seven:
            {
                checkAndSetAstericks("7");
            }
            break;
            case R.id.btn_eight:
            {
                checkAndSetAstericks("8");
            }
            break;
            case R.id.btn_nine:
            {
                checkAndSetAstericks("9");
            }
            break;
            case R.id.btn_zero:
            {
                checkAndSetAstericks("0");
            }
            break;
            case R.id.btn_next:
            {
                if(enteredPinCount<4)
                {
                    Toast.makeText(this, "Enter PIN", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(!pm.getBoolean("lockScreenEnable"))
                    {
                        pm.putString("lockScreenPin", enteredPin );
                        pm.putBoolean("lockScreenEnable", true );

                        Intent intent = new Intent(LockScreen.this, WelcomeActivity.class);
                        Log.d("PIN CODE : ", enteredPin);
                        startActivity(intent);
                    }
                    else if(lockScreenPin.equals(enteredPin))
                    {
                        if(getIntent().hasExtra("notification"))
                        {
                            if(getIntent().getStringExtra("notification").equals("true"))
                            {
                                Intent intent = new Intent(LockScreen.this, ApproveRejectActivity.class);
                                startActivity(intent);
                            }
                        }
                        else
                        {
                            Intent intent = new Intent(LockScreen.this, WelcomeActivity.class);
                            startActivity(intent);
                        }
                    }

                    else
                    {
                        Log.d("PIN ENTERED : ", enteredPin);
                        Log.d("PIN ACTUAL : ", lockScreenPin);
                        Toast.makeText(LockScreen.this, "Wrong Pin Entered. Please try again .", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
            case R.id.btn_reload:
            {
                enteredPinCount = 0;
                pin_one.setBackground(getResources().getDrawable(R.drawable.ic_dot_empty));
                pin_two.setBackground(getResources().getDrawable(R.drawable.ic_dot_empty));
                pin_three.setBackground(getResources().getDrawable(R.drawable.ic_dot_empty));
                pin_four.setBackground(getResources().getDrawable(R.drawable.ic_dot_empty));


                enteredPin = "";

                setTag("", pin_one);
                setTag("", pin_two);
                setTag("", pin_three);
                setTag("", pin_four);
            }
            break;
         }
    }

    public String checkTag(ImageButton pinBtn)
    {
        return pinBtn.getTag().toString();
    }



    public void setTag(String tagDone, ImageButton pinBtn)
    {
        pinBtn.setTag(tagDone);
    }



    public void checkAndSetAstericks(String pressedNum) {

        if (enteredPinCount > 3)
        {
            Toast.makeText(LockScreen.this, "4-digits already entered", Toast.LENGTH_SHORT).show();
        }

        else
        {

            enteredPinCount++;
            enteredPin = enteredPin + pressedNum;

            if (checkTag(pin_one).equals("DONE"))
            {
                if (checkTag(pin_two).equals("DONE"))
                {
                    if (checkTag(pin_three).equals("DONE"))
                    {
                        if (checkTag(pin_four).equals("DONE"))
                        {
                            //already all pin entered
                        }
                        else
                        {
                            pin_four.setBackground(getResources().getDrawable(R.drawable.ic_dot_fill));
                            setTag("DONE", pin_four);
                        }

                    }
                    else
                    {
                        pin_three.setBackground(getResources().getDrawable(R.drawable.ic_dot_fill));
                        setTag("DONE", pin_three);
                    }
                }
                else
                {
                    pin_two.setBackground(getResources().getDrawable(R.drawable.ic_dot_fill));
                    setTag("DONE", pin_two);
                }
            }
            else
            {
                pin_one.setBackground(getResources().getDrawable(R.drawable.ic_dot_fill));
                setTag("DONE", pin_one);
            }
        }
    }
}
