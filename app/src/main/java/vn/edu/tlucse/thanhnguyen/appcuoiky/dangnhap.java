package vn.edu.tlucse.thanhnguyen.appcuoiky;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class dangnhap extends AppCompatActivity {
    EditText edttk,edtmk;
    Button buttondangnhap,buttondangky;
    dulieu db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dangnhap);
        buttondangnhap = findViewById(R.id.buttondangnhap);
        buttondangky = findViewById(R.id.buttondangky);
        edttk = findViewById(R.id.edttk);
        edtmk = findViewById(R.id.edtmk);
        db = new dulieu(this);
        buttondangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tk = edttk.getText().toString();
                String mk = edtmk.getText().toString();
                Cursor cursor = db.checkLogin(tk, mk);

                if (tk.isEmpty() || mk.isEmpty()) {
                    Toast.makeText(dangnhap.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_LONG).show();
                }
                else if (tk.equals("quanly") && mk.equals("123")) {
                    Intent intent = new Intent(dangnhap.this, quanlysanpham.class);
                    startActivity(intent);
                    Toast.makeText(dangnhap.this, "Chào mừng Quản Lý ", Toast.LENGTH_LONG).show();

                }
                else if (cursor.moveToFirst()) {
                    Toast.makeText(dangnhap.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(dangnhap.this, khachhang.class);
                    intent.putExtra("taikhoan", tk);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(dangnhap.this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                }

            }
        });
        buttondangky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(dangnhap.this, dangky.class);
                startActivity(intent);

            }
        });

    }
}
