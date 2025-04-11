package vn.edu.tlucse.thanhnguyen.appcuoiky;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.content.Intent;

public class dangky extends Activity {

    EditText edtTaiKhoan, edtMatKhau, edtHoTen, edtSDT, edtDiaChi;
    Button btnDangKy;
    dulieu db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dangky);

        edtTaiKhoan = findViewById(R.id.edtTaiKhoan);
        edtMatKhau = findViewById(R.id.edtMatKhau);
        edtHoTen = findViewById(R.id.edtHoTen);
        edtSDT = findViewById(R.id.edtSDT);
        edtDiaChi = findViewById(R.id.edtDiaChi);
        btnDangKy = findViewById(R.id.btnDangKy);

        db = new dulieu(this);

        btnDangKy.setOnClickListener(v -> {
            String tk = edtTaiKhoan.getText().toString();
            String mk = edtMatKhau.getText().toString();
            String ht = edtHoTen.getText().toString();
            String sdt = edtSDT.getText().toString();
            String dc = edtDiaChi.getText().toString();

            if (tk.isEmpty() || mk.isEmpty() || ht.isEmpty() || sdt.isEmpty() || dc.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            } else {
                boolean inserted = db.insertKhachHang(tk, mk, ht, sdt, dc);
                if (inserted) {
                    Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Tài khoản đã tồn tại!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
