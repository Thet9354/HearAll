package com.example.hearlall.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hearlall.EditProfile_Activity;
import com.example.hearlall.MainActivity;
import com.example.hearlall.MainMenuPage_Activity;
import com.example.hearlall.R;
import com.example.hearlall.Settings.AboutUs.AboutUs_Activity;
import com.example.hearlall.Settings.AdditionalResource.AdditionalResources_Activity;
import com.example.hearlall.Settings.Feedback.Feedback_Activity;
import com.example.hearlall.Settings.Language.Language_Activity;
import com.example.hearlall.Settings.SecurityAndPrivacy.SecurityNPrivacy_Activity;
import com.example.hearlall.Settings.TextSize.TextSize_Activity;
import com.example.hearlall.DataBase.userSettingModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class Settings_Activity extends AppCompatActivity {

    private ImageView btn_back, securityIcon, btn_Security, textSizeIcon, btn_textSIze, languageIcon, btn_language,
            feedbackIcon, btn_feedback, aboutUsIcon, btn_aboutUs, additionalIcon, btn_additionalResources, logOutIcon, btn_logOut;

    private RelativeLayout rel_security, rel_textSize, rel_language, rel_feedback, rel_aboutUs, rel_additionalResources, rel_logOut;

    private LinearLayout linearLayout_NNP, linearLayout_STL, linearLayout_FAF, linearLayout_L;

    private androidx.appcompat.widget.AppCompatButton btn_editProfile;

    private LinearLayout parentView;

    private androidx.appcompat.widget.SwitchCompat nightMode_switch, notifications_Switch, privateAcc_switch;

    private TextView txtView_security, txtView_textSize, txtView_languages, txtView_feedback, txtView_aboutUs, txtView_additionalResources, txtView_logOut,
            txtView_nightMode, txtView_notification, txtView_privateAccount, txtView_settings, txtView_FullName, txtView_done;

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://hearall-3017f-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference databaseReference  = database.getReference().child("users");

    FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();

    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;

    private FirebaseAuth mAuth;

    private SharedPreferences sharedPreferences;

    private userSettingModel settingModel;

    private SharedPreferences.Editor editor;

    private Intent intent;

    private String mPhoneNumber, nightMode, notification, privateAcc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //--> Google Sign in Integration with Firebase
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        intent = getIntent();

        mPhoneNumber = intent.getStringExtra("PhoneNumber");
        System.out.println(mPhoneNumber);

        settingModel = new userSettingModel();

        initWidget();

        loadSharedPreferences();

        pageDirectories();
    }

    private void pageDirectories() {

        txtView_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //--->Updating data in firebase
                HashMap user = new HashMap();
                user.put("Night Mode", nightMode);
                user.put("Notifications", notification);
                user.put("Private Account", privateAcc);

                databaseReference.child(mPhoneNumber)
                        .child("User's Information")
                        .child("User's Settings")
                        .updateChildren(user).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {

                                if (task.isSuccessful())
                                {
                                    //--->Updating data in firestore
                                    firestoreDB.collection("user")
                                            .whereEqualTo("Phone Number", mPhoneNumber)
                                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful() && !task.getResult().isEmpty())
                                                    {
                                                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                                        String documentID = documentSnapshot.getId();
                                                        firestoreDB.collection("user")
                                                                .document(documentID)
                                                                .update(user)
                                                                .addOnSuccessListener(new OnSuccessListener() {
                                                                    @Override
                                                                    public void onSuccess(Object o) {
                                                                        Toast.makeText(Settings_Activity.this, "Successfully updated in firestore", Toast.LENGTH_SHORT).show();
                                                                        startActivity(new Intent(getApplicationContext(), MainMenuPage_Activity.class));
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(Settings_Activity.this, "An error occured while updating, please try again later", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                    }
                                                }
                                            });
                                }
                                else
                                {
                                    Toast.makeText(Settings_Activity.this, "An error occured while updating, please try again later", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        /** OnCLickListener for Night Mode switch **/
        nightMode_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    nightMode = "ON";
                    settingModel.setCustomTheme(userSettingModel.DARK_THEME);
                }
                else
                {
                    settingModel.setCustomTheme(userSettingModel.LIGHT_THEME);
                    nightMode = "OFF";
                }
                editor = getSharedPreferences(userSettingModel.PREFERENCES, MODE_PRIVATE).edit();
                editor.putString(userSettingModel.CUSTOM_THEME, settingModel.getCustomTheme());
                editor.apply();
                updateView();

            }
        });

        /** OnCheckedChangeListener for notification switch **/
        notifications_Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    oneSignalSetup();
                    notification = "ON";
                }
                else
                    notification = "OFF";
            }
        });

        /** onCheckedListener for **/
        privateAcc_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    privateAcc_switch.setTrackTintList(AppCompatResources.getColorStateList(getApplicationContext(), R.color.purple_500));
                    privateAcc = "ON";
                }
                else
                {
                    privateAcc_switch.setTrackTintList(AppCompatResources.getColorStateList(getApplicationContext(), R.color.gray));
                    privateAcc = "OFF";
                }
            }
        });

        /** OnClickListener for Edit Profile**/
        btn_editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EditProfile_Activity.class);
                intent.putExtra("PhoneNumber", mPhoneNumber);
                startActivity(intent);
            }
        });

        /** OnCLickListener for Security & Privacy **/
        securityIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SecurityNPrivacy_Activity.class);
                intent.putExtra("PhoneNumber", mPhoneNumber);
                startActivity(intent);
            }
        });

        rel_security.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SecurityNPrivacy_Activity.class);
                intent.putExtra("PhoneNumber", mPhoneNumber);
                startActivity(intent);
            }
        });

        txtView_security.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SecurityNPrivacy_Activity.class);
                intent.putExtra("PhoneNumber", mPhoneNumber);
                startActivity(intent);
            }
        });

        btn_Security.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SecurityNPrivacy_Activity.class);
                intent.putExtra("PhoneNumber", mPhoneNumber);
                startActivity(intent);
            }
        });


        /** OnCLickListener for Text Size **/
        textSizeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TextSize_Activity.class);
                intent.putExtra("PhoneNumber", mPhoneNumber);
                startActivity(intent);
            }
        });

        rel_textSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TextSize_Activity.class);
                intent.putExtra("PhoneNumber", mPhoneNumber);
                startActivity(intent);
            }
        });

        txtView_textSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TextSize_Activity.class);
                intent.putExtra("PhoneNumber", mPhoneNumber);
                startActivity(intent);
            }
        });

        btn_textSIze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TextSize_Activity.class);
                intent.putExtra("PhoneNumber", mPhoneNumber);
                startActivity(intent);
            }
        });


        /** OnCLickListener for Language **/
        languageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Language_Activity.class);
                intent.putExtra("PhoneNumber", mPhoneNumber);
                startActivity(intent);
            }
        });

        rel_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Language_Activity.class);
                intent.putExtra("PhoneNumber", mPhoneNumber);
                startActivity(intent);
            }
        });

        txtView_languages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Language_Activity.class);
                intent.putExtra("PhoneNumber", mPhoneNumber);
                startActivity(intent);
            }
        });

        btn_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Language_Activity.class);
                intent.putExtra("PhoneNumber", mPhoneNumber);
                startActivity(intent);
            }
        });


        /** OnCLickListener for Feedback **/
        feedbackIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Feedback_Activity.class));
            }
        });

        rel_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Feedback_Activity.class));
            }
        });

        txtView_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Feedback_Activity.class));
            }
        });

        btn_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Feedback_Activity.class));
            }
        });


        /** OnCLickListener for About Us **/
        aboutUsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AboutUs_Activity.class));
            }
        });

        rel_aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AboutUs_Activity.class));
            }
        });

        txtView_aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AboutUs_Activity.class));
            }
        });

        btn_aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AboutUs_Activity.class));
            }
        });


        /** OnCLickListener for Additional resources **/
        additionalIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AdditionalResources_Activity.class);
                intent.putExtra("PhoneNumber", mPhoneNumber);
                startActivity(intent);
            }
        });

        rel_additionalResources.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AdditionalResources_Activity.class);
                intent.putExtra("PhoneNumber", mPhoneNumber);
                startActivity(intent);
            }
        });

        txtView_additionalResources.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AdditionalResources_Activity.class);
                intent.putExtra("PhoneNumber", mPhoneNumber);
                startActivity(intent);
            }
        });

        btn_additionalResources.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AdditionalResources_Activity.class);
                intent.putExtra("PhoneNumber", mPhoneNumber);
                startActivity(intent);
            }
        });

        /** OnCLickListener for Log Out **/
        logOutIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Add the intent leading to the Log Out page
            }
        });

        rel_logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Add the intent leading to the Log Out page
            }
        });

        txtView_logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Add the intent leading to the Log Out page
            }
        });

        btn_logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Settings_Activity.this);
                builder.setTitle("HearAll");
                builder.setMessage("Are you sure you want to log out?");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //--->Log out off everything here
                        googleSignOut();
                        mAuth.signOut();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.create().show();
            }
        });
    }

    private void googleSignOut() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    private void oneSignalSetup() {
        //TODO: Set up the notification feature with oneSignal
    }

    private void loadSharedPreferences() {
        sharedPreferences = getSharedPreferences(userSettingModel.PREFERENCES, MODE_PRIVATE);
        String theme = sharedPreferences.getString(userSettingModel.CUSTOM_THEME, userSettingModel.LIGHT_THEME);
        settingModel.setCustomTheme(theme);
        updateView();
    }

    private void updateView() {
        final Drawable purple_background = ContextCompat.getDrawable(this, R.drawable.purple_background);
        final Drawable white_standard_background = ContextCompat.getDrawable(this, R.drawable.white_standard_background);
        final Drawable round_back_white10_20 = ContextCompat.getDrawable(Settings_Activity.this, R.drawable.round_back_white10_20);


        if (settingModel.getCustomTheme().equals(userSettingModel.DARK_THEME))
        {
            parentView.setBackground(purple_background);
            darkModeChanges();
        }
        else
        {
            parentView.setBackground(white_standard_background);
            lightModeChanges();
        }
    }

    private void lightModeChanges() {
        /** Text color changes for TextView **/
        txtView_nightMode.setTextColor(Color.BLACK);
        txtView_notification.setTextColor(Color.BLACK);
        txtView_privateAccount.setTextColor(Color.BLACK);
        txtView_security.setTextColor(Color.BLACK);
        txtView_textSize.setTextColor(Color.BLACK);
        txtView_languages.setTextColor(Color.BLACK);
        txtView_feedback.setTextColor(Color.BLACK);
        txtView_aboutUs.setTextColor(Color.BLACK);
        txtView_additionalResources.setTextColor(Color.BLACK);
        txtView_logOut.setTextColor(Color.BLACK);

        /** Src changes for ImageView **/
        btn_Security.setImageResource(R.drawable.right_arrow_icon);
        btn_textSIze.setImageResource(R.drawable.right_arrow_icon);
        btn_language.setImageResource(R.drawable.right_arrow_icon);
        btn_feedback.setImageResource(R.drawable.right_arrow_icon);
        btn_aboutUs.setImageResource(R.drawable.right_arrow_icon);
        btn_additionalResources.setImageResource(R.drawable.right_arrow_icon);
        btn_logOut.setImageResource(R.drawable.right_arrow_icon);

        /** Color changes for switches **/
        notifications_Switch.setThumbTintList(AppCompatResources.getColorStateList(this, R.color.white));
        notifications_Switch.setTrackTintList(AppCompatResources.getColorStateList(this, R.color.gray));

        privateAcc_switch.setThumbTintList(AppCompatResources.getColorStateList(this, R.color.white));
        privateAcc_switch.setTrackTintList(AppCompatResources.getColorStateList(this, R.color.gray));

        /** Background change for Linear Layouts**/
        linearLayout_NNP.setBackgroundResource(R.drawable.right_arrow_icon);
        linearLayout_STL.setBackgroundResource(R.drawable.right_arrow_icon);
        linearLayout_FAF.setBackgroundResource(R.drawable.right_arrow_icon);
        linearLayout_L.setBackgroundResource(R.drawable.right_arrow_icon);

        notifications_Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    notifications_Switch.setTrackTintList(AppCompatResources.getColorStateList(getApplicationContext(), R.color.teal_200));
                }
                else
                    notifications_Switch.setTrackTintList(AppCompatResources.getColorStateList(getApplicationContext(), R.color.gray));
            }
        });

        privateAcc_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    privateAcc_switch.setTrackTintList(AppCompatResources.getColorStateList(getApplicationContext(), R.color.purple_500));
                else
                    privateAcc_switch.setTrackTintList(AppCompatResources.getColorStateList(getApplicationContext(), R.color.gray));

            }
        });
    }

    private void darkModeChanges() {
        /** Text color changes for TextView **/
        txtView_nightMode.setTextColor(Color.WHITE);
        txtView_notification.setTextColor(Color.WHITE);
        txtView_privateAccount.setTextColor(Color.WHITE);
        txtView_security.setTextColor(Color.WHITE);
        txtView_textSize.setTextColor(Color.WHITE);
        txtView_languages.setTextColor(Color.WHITE);
        txtView_feedback.setTextColor(Color.WHITE);
        txtView_aboutUs.setTextColor(Color.WHITE);
        txtView_additionalResources.setTextColor(Color.WHITE);
        txtView_logOut.setTextColor(Color.WHITE);

        /** Src changes for ImageView **/
        btn_Security.setImageResource(R.drawable.right_arrow_icon);
        btn_textSIze.setImageResource(R.drawable.right_arrow_icon);
        btn_language.setImageResource(R.drawable.right_arrow_icon);
        btn_feedback.setImageResource(R.drawable.right_arrow_icon);
        btn_aboutUs.setImageResource(R.drawable.right_arrow_icon);
        btn_additionalResources.setImageResource(R.drawable.right_arrow_icon);
        btn_logOut.setImageResource(R.drawable.right_arrow_icon);

        /** Color changes for switches **/
        notifications_Switch.setThumbTintList(AppCompatResources.getColorStateList(this, R.color.teal_200));
//        notifications_Switch.setTrackTintList(AppCompatResources.getColorStateList(this, R.color.gray));

        privateAcc_switch.setThumbTintList(AppCompatResources.getColorStateList(this, R.color.purple));
//        privateAcc_switch.setTrackTintList(AppCompatResources.getColorStateList(this, R.color.gray));

        /** Background change for Linear Layouts**/
        linearLayout_NNP.setBackgroundResource(R.drawable.round_back_white10_20);
        linearLayout_STL.setBackgroundResource(R.drawable.round_back_white10_20);
        linearLayout_FAF.setBackgroundResource(R.drawable.round_back_white10_20);
        linearLayout_L.setBackgroundResource(R.drawable.round_back_white10_20);

        notifications_Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    notifications_Switch.setTrackTintList(AppCompatResources.getColorStateList(getApplicationContext(), R.color.teal_200));
                }
                else
                    notifications_Switch.setTrackTintList(AppCompatResources.getColorStateList(getApplicationContext(), R.color.gray));
            }
        });

        privateAcc_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    privateAcc_switch.setTrackTintList(AppCompatResources.getColorStateList(getApplicationContext(), R.color.purple_500));
                else
                    privateAcc_switch.setTrackTintList(AppCompatResources.getColorStateList(getApplicationContext(), R.color.gray));

            }
        });
    }

    private void initWidget() {
        //--->ImageView
        btn_back = findViewById(R.id.btn_back);
        securityIcon = findViewById(R.id.securityIcon);
        btn_Security = findViewById(R.id.btn_Security);
        textSizeIcon = findViewById(R.id.textSizeIcon);
        btn_textSIze = findViewById(R.id.btn_textSIze);
        languageIcon = findViewById(R.id.languageIcon);
        btn_language = findViewById(R.id.btn_language);
        feedbackIcon = findViewById(R.id.feedbackIcon);
        btn_feedback = findViewById(R.id.btn_feedback);
        aboutUsIcon = findViewById(R.id.aboutUsIcon);
        btn_aboutUs = findViewById(R.id.btn_aboutUs);
        additionalIcon = findViewById(R.id.additionalIcon);
        btn_additionalResources = findViewById(R.id.btn_additionalResources);
        logOutIcon = findViewById(R.id.logOutIcon);
        btn_logOut = findViewById(R.id.btn_logOut);
        btn_editProfile = findViewById(R.id.btn_editProfile);

        //--->RelativeLayout
        rel_security = findViewById(R.id.rel_security);
        rel_textSize = findViewById(R.id.rel_textSize);
        rel_language = findViewById(R.id.rel_language);
        rel_feedback = findViewById(R.id.rel_feedback);
        rel_aboutUs = findViewById(R.id.rel_aboutUs);
        rel_additionalResources = findViewById(R.id.rel_additionalResources);
        rel_logOut = findViewById(R.id.rel_logOut);

        //--->androidx.appcompat.widget.SwitchCompat
        nightMode_switch = findViewById(R.id.nightMode_switch);
        notifications_Switch = findViewById(R.id.notifications_Switch);
        privateAcc_switch = findViewById(R.id.privateAcc_switch);

        //--->TextView
        txtView_security = findViewById(R.id.txtView_security);
        txtView_textSize = findViewById(R.id.txtView_textSize);
        txtView_languages = findViewById(R.id.txtView_languages);
        txtView_feedback = findViewById(R.id.txtView_feedback);
        txtView_aboutUs = findViewById(R.id.txtView_aboutUs);
        txtView_additionalResources = findViewById(R.id.txtView_additionalResources);
        txtView_logOut = findViewById(R.id.txtView_logOut);
        txtView_nightMode = findViewById(R.id.txtView_nightMode);
        txtView_notification = findViewById(R.id.txtView_notification);
        txtView_privateAccount = findViewById(R.id.txtView_privateAccount);
        txtView_settings = findViewById(R.id.txtView_settings);
        txtView_FullName = findViewById(R.id.txtView_FullName);
        txtView_done = findViewById(R.id.txtView_done);

        //--->LinearLayout
        linearLayout_NNP = findViewById(R.id.linearLayout_NNP);
        linearLayout_STL = findViewById(R.id.linearLayout_STL);
        linearLayout_FAF = findViewById(R.id.linearLayout_FAF);
        linearLayout_L = findViewById(R.id.linearLayout_L);

        //--->ParentView
        parentView = findViewById(R.id.parentView);
    }
}