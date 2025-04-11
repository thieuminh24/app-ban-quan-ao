package vn.edu.tlucse.thanhnguyen.appcuoiky;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class quanlysanpham extends AppCompatActivity {

    ListView listView;
    Button btnThem,btnDuyet;
    ArrayList<SanPham> dsSanPham;
    SanPhamAdapter adapter;

    SQLiteDatabase db;

    String[] danhSachHinhAnh = {"ao1", "ao2", "quan1", "quan2"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quanly_sanpham);

        listView = findViewById(R.id.listViewSanPham);
        btnThem = findViewById(R.id.btnThemSP);
        btnDuyet= findViewById(R.id.btnDuyetDonHang);

        db = openOrCreateDatabase("QuanLyQuanAo.db", MODE_PRIVATE, null);
        taoBangSanPham();

        dsSanPham = new ArrayList<>();
        adapter = new SanPhamAdapter();
        listView.setAdapter(adapter);

        napDuLieu();

        btnThem.setOnClickListener(v -> moFormSanPham(null));

        listView.setOnItemClickListener((parent, view, position, id) -> {
            SanPham sp = dsSanPham.get(position);
            moFormSanPham(sp);
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            xoaSanPham(dsSanPham.get(position).getId());
            return true;
        });
        btnDuyet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(quanlysanpham.this, duyetdonhang.class));

            }
        });
    }

    private void taoBangSanPham() {
        String sql = "CREATE TABLE IF NOT EXISTS SanPham (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ten TEXT," +
                "hang TEXT," +
                "gia REAL," +
                "conlai INTEGER," +
                "ghichu TEXT," +
                "hinhanh TEXT)";
        db.execSQL(sql);
    }

    private void napDuLieu() {
        dsSanPham.clear();
        Cursor c = db.rawQuery("SELECT * FROM SanPham", null);
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String ten = c.getString(1);
            String hang = c.getString(2);
            double gia = c.getDouble(3);
            int conlai = c.getInt(4);
            String ghichu = c.getString(5);
            String hinhanh = c.getString(6);
            dsSanPham.add(new SanPham(id, ten, hang, gia, conlai, ghichu, hinhanh));
        }
        c.close();
        adapter.notifyDataSetChanged();
    }

    private void moFormSanPham(SanPham sp) {
        View view = LayoutInflater.from(this).inflate(R.layout.activity_form_sanpham, null);
        EditText edtTen = view.findViewById(R.id.edtTen);
        EditText edtHang = view.findViewById(R.id.edtHang);
        EditText edtGia = view.findViewById(R.id.edtGia);
        EditText edtConLai = view.findViewById(R.id.edtConLai);
        EditText edtGhiChu = view.findViewById(R.id.edtGhiChu);
        Spinner spinnerHinhAnh = view.findViewById(R.id.spinnerHinhAnh);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, danhSachHinhAnh);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHinhAnh.setAdapter(spinnerAdapter);

        int idSP = -1;
        if (sp != null) {
            idSP = sp.getId();
            edtTen.setText(sp.getTen());
            edtHang.setText(sp.getHang());
            edtGia.setText(String.valueOf(sp.getGia()));
            edtConLai.setText(String.valueOf(sp.getConlai()));
            edtGhiChu.setText(sp.getGhichu());
            for (int i = 0; i < danhSachHinhAnh.length; i++) {
                if (danhSachHinhAnh[i].equals(sp.getHinhanh())) {
                    spinnerHinhAnh.setSelection(i);
                    break;
                }
            }
        }

        int finalIdSP = idSP;
        new AlertDialog.Builder(this)
                .setTitle(sp == null ? "Thêm sản phẩm" : "Sửa sản phẩm")
                .setView(view)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String ten = edtTen.getText().toString();
                    String hang = edtHang.getText().toString();
                    double gia = Double.parseDouble(edtGia.getText().toString());
                    int conlai = Integer.parseInt(edtConLai.getText().toString());
                    String ghichu = edtGhiChu.getText().toString();
                    String hinh = spinnerHinhAnh.getSelectedItem().toString();

                    ContentValues values = new ContentValues();
                    values.put("ten", ten);
                    values.put("hang", hang);
                    values.put("gia", gia);
                    values.put("conlai", conlai);
                    values.put("ghichu", ghichu);
                    values.put("hinhanh", hinh);

                    if (finalIdSP == -1) {
                        db.insert("SanPham", null, values);
                    } else {
                        db.update("SanPham", values, "id = ?", new String[]{String.valueOf(finalIdSP)});
                    }
                    napDuLieu();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void xoaSanPham(int id) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa sản phẩm")
                .setMessage("Bạn có chắc muốn xóa sản phẩm này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    db.delete("SanPham", "id = ?", new String[]{String.valueOf(id)});
                    napDuLieu();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // === MODEL SANPHAM ===
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
        public String getHang() { return hang; }
        public double getGia() { return gia; }
        public int getConlai() { return conlai; }
        public String getGhichu() { return ghichu; }
        public String getHinhanh() { return hinhanh; }
    }

    // === ADAPTER ===
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
