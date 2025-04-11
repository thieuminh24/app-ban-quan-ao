package vn.edu.tlucse.thanhnguyen.appcuoiky;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.*;

public class khachhang extends AppCompatActivity {

    ListView listViewSP;
    Button btnGioHang, btnDatHang, btnXemDonHang;
    ArrayList<SanPham> dsSanPham;
    ArrayList<SanPham> gioHang;
    SanPhamAdapter adapter;
    SQLiteDatabase db;

    String emailKhachHang = "user@example.com"; // Giả lập email đăng nhập

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_khachhang);

        listViewSP = findViewById(R.id.listViewSanPhamKH);
        btnGioHang = findViewById(R.id.btnXemGio);
        btnDatHang = findViewById(R.id.btnDatHang);
        btnXemDonHang = findViewById(R.id.btnXemDonHang);

        db = openOrCreateDatabase("QuanLyQuanAo.db", MODE_PRIVATE, null);
        taoBangGioHangVaDonHang();

        dsSanPham = new ArrayList<>();
        gioHang = new ArrayList<>();
        adapter = new SanPhamAdapter();
        listViewSP.setAdapter(adapter);

        napDuLieu();

        listViewSP.setOnItemClickListener((parent, view, position, id) -> {
            SanPham sp = dsSanPham.get(position);
            themVaoGio(sp);
        });

        btnGioHang.setOnClickListener(v -> hienThiGioHang());
        btnDatHang.setOnClickListener(v -> datHang());
        btnXemDonHang.setOnClickListener(v -> xemDonHang());
    }

    private void taoBangGioHangVaDonHang() {
        db.execSQL("CREATE TABLE IF NOT EXISTS GioHang (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "email TEXT," +
                "id_sanpham INTEGER," +
                "soluong INTEGER," +
                "thanhtien REAL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS DonHang (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "email TEXT," +
                "ngay TEXT," +
                "tongtien REAL," +
                "trangthai TEXT)");
    }

    private void napDuLieu() {
        dsSanPham.clear();
        Cursor c = db.rawQuery("SELECT * FROM SanPham", null);
        while (c.moveToNext()) {
            dsSanPham.add(new SanPham(
                    c.getInt(0), c.getString(1), c.getString(2),
                    c.getDouble(3), c.getInt(4), c.getString(5), c.getString(6)));
        }
        c.close();
        adapter.notifyDataSetChanged();
    }

    private void themVaoGio(SanPham sp) {
        int soLuong = 1;
        double thanhTien = soLuong * sp.getGia();

        ContentValues values = new ContentValues();
        values.put("email", emailKhachHang);
        values.put("id_sanpham", sp.getId());
        values.put("soluong", soLuong);
        values.put("thanhtien", thanhTien);
        db.insert("GioHang", null, values);

        Toast.makeText(this, "Đã thêm vào giỏ", Toast.LENGTH_SHORT).show();
    }

    private void hienThiGioHang() {
        Cursor c = db.rawQuery("SELECT SanPham.ten, GioHang.soluong, GioHang.thanhtien " +
                "FROM GioHang INNER JOIN SanPham ON GioHang.id_sanpham = SanPham.id " +
                "WHERE GioHang.email = ?", new String[]{emailKhachHang});

        StringBuilder builder = new StringBuilder();
        double tong = 0;
        while (c.moveToNext()) {
            String ten = c.getString(0);
            int soluong = c.getInt(1);
            double tien = c.getDouble(2);
            builder.append(ten).append(" x").append(soluong).append(" - ").append(tien).append("đ\n");
            tong += tien;
        }
        c.close();

        new AlertDialog.Builder(this)
                .setTitle("Giỏ hàng của bạn")
                .setMessage(builder.length() == 0 ? "Giỏ hàng rỗng." : builder.toString() + "\nTổng: " + tong + "đ")
                .setPositiveButton("OK", null)
                .show();
    }

    private void datHang() {
        Cursor c = db.rawQuery("SELECT SUM(thanhtien) FROM GioHang WHERE email = ?", new String[]{emailKhachHang});
        if (c.moveToFirst() && c.getDouble(0) > 0) {
            double tongTien = c.getDouble(0);
            c.close();

            ContentValues values = new ContentValues();
            values.put("email", emailKhachHang);
            values.put("ngay", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
            values.put("tongtien", tongTien);
            values.put("trangthai", "Chờ duyệt");

            db.insert("DonHang", null, values);
            db.delete("GioHang", "email = ?", new String[]{emailKhachHang});

            Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
        }
    }

    private void xemDonHang() {
        Cursor c = db.rawQuery("SELECT * FROM DonHang WHERE email = ?", new String[]{emailKhachHang});
        if (c.getCount() == 0) {
            Toast.makeText(this, "Bạn chưa đặt đơn hàng nào.", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder builder = new StringBuilder();
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String ngay = c.getString(2);
            double tong = c.getDouble(3);
            String trangthai = c.getString(4);

            builder.append("Mã đơn: ").append(id)
                    .append("\nNgày: ").append(ngay)
                    .append("\nTổng: ").append(tong).append("đ")
                    .append("\nTrạng thái: ").append(trangthai)
                    .append("\n\n");
        }
        c.close();

        new AlertDialog.Builder(this)
                .setTitle("Đơn hàng của bạn")
                .setMessage(builder.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    // Adapter và model giống bên QuanLySanPhamActivity
    public class SanPham {
        private int id;
        private String ten, hang, ghichu, hinhanh;
        private double gia;
        private int conlai;

        public SanPham(int id, String ten, String hang, double gia, int conlai, String ghichu, String hinhanh) {
            this.id = id;
            this.ten = ten;
            this.hang = hang;
            this.gia = gia;
            this.conlai = conlai;
            this.ghichu = ghichu;
            this.hinhanh = hinhanh;
        }

        public int getId() { return id; }
        public String getTen() { return ten; }
        public double getGia() { return gia; }
        public String getHinhanh() { return hinhanh; }
    }

    public class SanPhamAdapter extends BaseAdapter {
        @Override
        public int getCount() { return dsSanPham.size(); }

        @Override
        public Object getItem(int i) { return dsSanPham.get(i); }

        @Override
        public long getItemId(int i) { return dsSanPham.get(i).getId(); }

        @Override
        public View getView(int i, View view, android.view.ViewGroup parent) {
            if (view == null)
                view = getLayoutInflater().inflate(R.layout.item_sanpham, null);

            TextView txtTen = view.findViewById(R.id.txtTenSP);
            TextView txtGia = view.findViewById(R.id.txtGiaSP);
            ImageView img = view.findViewById(R.id.imgSanPham);

            SanPham sp = dsSanPham.get(i);
            txtTen.setText(sp.getTen());
            txtGia.setText("Giá: " + sp.getGia() + "đ");

            int imgId = getResources().getIdentifier(sp.getHinhanh(), "drawable", getPackageName());
            img.setImageResource(imgId != 0 ? imgId : R.drawable.default_image);

            return view;
        }
    }
}


