package com.example.markohudomal.santorini;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity {
    public final static int MY_REQUEST_WRITE=201;

    public final static int GAME_MODE_HUMAN_HUMAN = 0;
    public final static int GAME_MODE_HUMAN_BOT = 1;
    public final static int GAME_MODE_BOT_BOT = 2;

    public final static int START_GAME=101;

    private Spinner spinner1;
    private Spinner spinner2;
    private EditText edit_text;
    private Switch button_switch;
    private Button button_start;
    private RadioGroup radioGroup;
    private LinearLayout linear_diff;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Get permissions
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_REQUEST_WRITE);
        }

        //Game mode
        spinner1 = findViewById(R.id.spinner1);
        ArrayAdapter adapter1 = ArrayAdapter.createFromResource(this,R.array.game_mode,R.layout.spinner_item);
        spinner1.setAdapter(adapter1);

        //Switch
        button_switch = findViewById(R.id.switch_load_game_state);

        //Button start
        button_start=findViewById(R.id.button_start);

        //Bot vs Bot view
        spinner2 = findViewById(R.id.spinner2);
        ArrayAdapter adapter2 = ArrayAdapter.createFromResource(this,R.array.bot_vs_bot_view,R.layout.spinner_item);
        spinner2.setAdapter(adapter2);

        //LinearLayout
        linear_diff=findViewById(R.id.linear_depth_diff);
        //Game depth(if bot is selected)
        edit_text=findViewById(R.id.edittext_game_depth);
        //Radio group
        radioGroup = findViewById(R.id.radio_group);
        //----------------------------------------------------------------------------------------------------------------------
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(MainActivity.this, ""+position, Toast.LENGTH_SHORT).show();
                switch (position)
                {
                    case GAME_MODE_HUMAN_HUMAN:{
                        spinner2.setVisibility(View.GONE);
                        linear_diff.setVisibility(View.GONE);
                        break;
                    }
                    case GAME_MODE_HUMAN_BOT:{
                        spinner2.setVisibility(View.GONE);
                        linear_diff.setVisibility(View.VISIBLE);
                        break;
                    }
                    case GAME_MODE_BOT_BOT:{
                        spinner2.setVisibility(View.VISIBLE);
                        linear_diff.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //==================================================================================

    }
    public void onStartClick(View view)
    {
        Intent intent = new Intent(this,GameActivity.class);

        intent.putExtra("game_mode",spinner1.getSelectedItemPosition());
        intent.putExtra("load_file",button_switch.isChecked());

        intent.putExtra("depth",(edit_text.getText().toString().equals("")?-1:Integer.parseInt(edit_text.getText().toString())));
        intent.putExtra("bot_view",spinner2.getSelectedItemPosition());


        if ((intent.getIntExtra("game_mode",0)!=0)&&(intent.getIntExtra("depth",-1)==-1) || (intent.getIntExtra("depth",10)>=6)){
            Toast.makeText(this, "Depth not regular", Toast.LENGTH_SHORT).show();
            return;
        }
        int diff;
        switch (radioGroup.getCheckedRadioButtonId()){
            case R.id.radio_bolid:{
                diff=0; break;
            }
            case R.id.radio_stupid:{
                diff=1; break;
            }
            case R.id.radio_ninja:{
                diff=2; break;
            }
            default:{
                diff=-1;break;
            }
        }
        intent.putExtra("difficulty",diff);


        startActivityForResult(intent,START_GAME);
        //Toast.makeText(this, "game_mode: "+spinner1.getSelectedItemId(), Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, "load_file: "+button_switch.isChecked(), Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, "Depth: "+(edit_text.getText().toString()), Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, "Bot view: "+spinner2.getSelectedItemId(), Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==MainActivity.MY_REQUEST_WRITE)
        {
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_DENIED)
            {
                Toast.makeText(this, "Give me permissions :(..", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}
