package com.example.sebastian.appdrawer.appdrawer;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.view.KeyEvent;


import com.example.sebastian.appdrawer.R;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;

public class CreateItem extends AppCompatActivity {
    LinearLayout imageLayout;
    HorizontalScrollView imageScroll;
    EditText itemTag,etDescription;
    ScrollView scrollView;
    Button addItemBtn;
    int iCounter = 0;
    int tagCounter;
    TextView imageCounter;
    List<String> tags = new ArrayList<String>();
    ListView tagsList;
    ImageView imagePlaceholder;
    TextView tvServings, tvLocation, tvPrice;
    EditText edNrOfServings, etTitle;
    int maxLength = 13;
    SwitchCompat swLocation, swPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_close);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        imageCounter = (TextView)findViewById(R.id.imageCounter);
        tagsList = (ListView)findViewById(R.id.listView);
        imagePlaceholder = (ImageView) findViewById(R.id.imagePlaceholder);
        tvServings = (TextView) findViewById(R.id.servings);
        edNrOfServings = (EditText) findViewById(R.id.nrOfServings);
        etTitle = (EditText) findViewById(R.id.etTitle);
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        swLocation = (SwitchCompat) findViewById(R.id.swLocation);
        tvPrice = (TextView) findViewById(R.id.tvPrice);
        swPrice = (SwitchCompat) findViewById(R.id.swPrice);




        edNrOfServings.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
        // OnClickListener for servings textview
        // husk at sætte default værdien på EditText til 1
        tvServings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edNrOfServings.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(CreateItem.this.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                edNrOfServings.setHint("");
            }
        });

        // Added tagsArea as LinearLayout view

        final LinearLayout imageLayout = (LinearLayout)findViewById(R.id.imageLayout);
        imageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ImageView image = new ImageView(CreateItem.this);
                image.setImageResource(R.drawable.lasagne);
                image.setScaleType(ImageView.ScaleType.CENTER);
                imageLayout.removeView(imagePlaceholder);
                imageLayout.addView(image);
                iCounter = iCounter+1;


                imageCounter.setText(""+iCounter+" image(s)");
            }

        });


        etDescription = (EditText) findViewById(R.id.etDesc);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        itemTag = (EditText) findViewById(R.id.etTags);

        itemTag.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        itemTag.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            addTag();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        itemTag.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                arg0.requestFocus();
            }
        });


        addItemBtn = (Button) findViewById(R.id.btnAddItem);
        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, tags);
                tagsList.setAdapter(adapter1);

            }
        });
    }

    public void addTag(){


        final TextView tag = new TextView(CreateItem.this);

        tag.setPadding(10,10, 10, 10);


        tag.setBackgroundColor(Color.parseColor("#BDBDBD"));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        tag.setMaxLines(1);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        tag.setText("X      "+itemTag.getText().toString());


        //Flexbox yo! https://github.com/google/flexbox-layout/blob/master/README.md
        final FlexboxLayout linearLayout = (FlexboxLayout) findViewById(R.id.tagsArea);
        tag.setId(tagCounter);
        linearLayout.addView(tag);

        focusOnView();
        itemTag.getText().clear();
        tagCounter=tagCounter+1;
        tags.add(tag.getText().toString());
        tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayout.removeView(tag);

            }
        });


    }
    private final void focusOnView(){
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }
}
