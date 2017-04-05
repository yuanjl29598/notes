package com.zzj.notes;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zzj.notes.utils.PreferenceHelper;
import com.zzj.notes.utils.SystemUtils;
import com.zzj.notes.widget.base.NoteBaseActivity;
import com.zzj.notes.widget.view.LookNoteActivity;

public class MainActivity extends NoteBaseActivity implements View.OnClickListener {

    private TextInputLayout inputPassLayout;
    private TextInputLayout inputValiaPassLayout;
    private TextInputEditText userPass;
    private TextInputEditText valiaUserPass;
    private Button login;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    public void initView() {
        inputPassLayout = (TextInputLayout) findViewById(R.id.id_input_pass);
        inputValiaPassLayout = (TextInputLayout) findViewById(R.id.id_valia_view);
        userPass = (TextInputEditText) findViewById(R.id.user_password);
        valiaUserPass = (TextInputEditText) findViewById(R.id.input_valia_password);
        login = (Button) findViewById(R.id.login);
        inputValiaPassLayout.setError("输入密码不正确");
        inputPassLayout.setError("输入密码不正确");
        if (TextUtils.isEmpty(readPass())) {
            inputValiaPassLayout.setVisibility(View.VISIBLE);
            login.setText("注册");
        } else {
            inputValiaPassLayout.setVisibility(View.GONE);
            login.setText("登录");
        }
        userPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputPassLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        valiaUserPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                inputValiaPassLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        login.setOnClickListener(this);
    }

    private void savePass(String pass) {
        if (!TextUtils.isEmpty(pass)) {
            PreferenceHelper.write(this, "note_data", "note_pass", pass);
        }
    }

    private String readPass() {
        return PreferenceHelper.readString(this, "note_data", "note_pass");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                loginAndRegister();
                break;
            default:
                break;
        }
    }

    private void loginAndRegister() {
        String userPassStr = userPass.getText().toString();
        if (TextUtils.isEmpty(userPassStr)) {
            userPass.setError("请输入密码");
            inputPassLayout.setErrorEnabled(true);
        }
        if (TextUtils.isEmpty(readPass())) {
            String valiaPassStr = valiaUserPass.getText().toString();
            if (!userPassStr.equals(valiaPassStr)) {
                inputValiaPassLayout.setErrorEnabled(true);
                valiaUserPass.setError("输入密码不一致，请重新输入");
            } else if (userPassStr.equals(valiaPassStr)) {
                savePass(userPassStr);
            }
        } else {
            String pass = readPass();
            if (pass.equals(userPassStr)) {
                Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                SystemUtils.jumpActivity(this, LookNoteActivity.class);
            } else {
                valiaUserPass.setError("密码错误");
                inputPassLayout.setEnabled(true);
                Toast.makeText(this, "输入密码错误", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
