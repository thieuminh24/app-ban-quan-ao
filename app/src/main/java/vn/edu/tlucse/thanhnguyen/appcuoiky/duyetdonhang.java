package vn.edu.tlucse.thanhnguyen.appcuoiky;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class duyetdonhang extends AppCompatActivity {

    ListView listView;
    ArrayList<DonHang> dsDonHang;
    ArrayAdapter<String> adapter;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duyet_donhang);

        listView = findViewById(R.id.listViewDonHangDuyet);
        db = openOrCreateDatabase("QuanLyQuanAo.db", MODE_PRIVATE, null);

        dsDonHang = new ArrayList<>();
        hienThiDonHang();
    }

    private void hienThiDonHang() {
        dsDonHang.clear();
        ArrayList<String> donHangText = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT * FROM DonHang", null);
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String email = c.getString(1);
            String ngay = c.getString(2);
            double tongtien = c.getDouble(3);
            String trangthai = c.getString(4);

            DonHang dh = new DonHang(id, email, ngay, tongtien, trangthai);
            dsDonHang.add(dh);

            donHangText.add("ID: " + id +
                    "\nEmail: " + email +
                    "\nNgày: " + ngay +
                    "\nTổng: " + tongtien + "đ" +
                    "\nTrạng thái: " + trangthai);
        }
        c.close();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, donHangText);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            DonHang don = dsDonHang.get(position);
            if (!don.getTrangthai().equals("Đã duyệt")) {
                new AlertDialog.Builder(this)
                        .setTitle("Duyệt đơn hàng")
                        .setMessage("Duyệt đơn hàng ID: " + don.getId() + " của " + don.getEmail() + "?")
                        .setPositiveButton("Duyệt", (dialog, which) -> {
                            ContentValues values = new ContentValues();
                            values.put("trangthai", "Đã duyệt");
                            db.update("DonHang", values, "id = ?", new String[]{String.valueOf(don.getId())});
                            hienThiDonHang();
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });
    }

    // === MODEL ===
    public class DonHang {
        private int id;
        private String email, ngay, trangthai;
        private double tongtien;

        public DonHang(int id, String email, String ngay, double tongtien, String trangthai) {
            this.id = id;
            this.email = email;
            this.ngay = ngay;
            this.tongtien = tongtien;
            this.trangthai = trangthai;
        }

        public int getId() { return id; }
        public String getEmail() { return email; }
        public String getNgay() { return ngay; }
        public double getTongtien() { return tongtien; }
        public String getTrangthai() { return trangthai; }
    }
}
