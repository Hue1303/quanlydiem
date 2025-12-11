package repository;

import entity.HocSinh;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utils.DatabaseConnection;

import java.sql.*;

public class HocSinhRepository {

    // 1. Lấy danh sách học sinh
    public ObservableList<HocSinh> getAllHocSinh(String tuKhoa) {
        ObservableList<HocSinh> danhSachHocSinh = FXCollections.observableArrayList();
        Connection ketNoi = null;
        Statement cauLenh = null;
        ResultSet ketQua = null;

        try {
            // B1: Kết nối Database
            DatabaseConnection db = new DatabaseConnection();
            ketNoi = db.connect();

            if (ketNoi != null) {
                // B2: Chuẩn bị câu lệnh SQL
                String sql = "SELECT * FROM HOC_SINH";
                if (tuKhoa != null && !tuKhoa.isEmpty()) {
                    sql = "SELECT * FROM HOC_SINH WHERE HO_TEN LIKE '%" + tuKhoa + "%' OR MA_HOC_SINH LIKE '%" + tuKhoa + "%'";
                }

                cauLenh = ketNoi.createStatement();
                ketQua = cauLenh.executeQuery(sql);
                while (ketQua.next()) {
                    String maHS = ketQua.getString("MA_HOC_SINH");
                    String hoTen = ketQua.getString("HO_TEN");
                    Date ngaySinh = ketQua.getDate("NGAY_SINH");
                    String gioiTinh = ketQua.getString("GIOI_TINH");
                    String diaChi = ketQua.getString("DIA_CHI");
                    String maLop = ketQua.getString("MA_LOP");

                    HocSinh hs = new HocSinh(maHS, hoTen, ngaySinh, gioiTinh, diaChi, maLop);
                    danhSachHocSinh.add(hs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dongKetNoi(ketNoi, cauLenh, ketQua);
        }
        return danhSachHocSinh;
    }

    // 2. Thêm mới một học sinh vào CSDL
    public boolean insert(HocSinh hs) {
        Connection ketNoi = null;
        PreparedStatement cauLenh = null;

        try {
            DatabaseConnection db = new DatabaseConnection();
            ketNoi = db.connect();

            String sql = "INSERT INTO HOC_SINH (MA_HOC_SINH, HO_TEN, NGAY_SINH, GIOI_TINH, DIA_CHI, MA_LOP) VALUES (?, ?, ?, ?, ?, ?)";

            cauLenh = ketNoi.prepareStatement(sql);

            cauLenh.setString(1, hs.getMaHS());
            cauLenh.setString(2, hs.getHoTen());
            cauLenh.setDate(3, hs.getNgaySinh());
            cauLenh.setString(4, hs.getGioiTinh());
            cauLenh.setString(5, hs.getDiaChi());
            cauLenh.setString(6, hs.getMaLop());

            int soDongAnhHuong = cauLenh.executeUpdate();
            if (soDongAnhHuong > 0) {
                return true; // Thêm thành công
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dongKetNoi(ketNoi, cauLenh, null);
        }
        return false;
    }

    // 3. Cập nhật thông tin học sinh
    public boolean update(HocSinh hs) {
        Connection ketNoi = null;
        PreparedStatement cauLenh = null;

        try {
            DatabaseConnection db = new DatabaseConnection();
            ketNoi = db.connect();

            String sql = "UPDATE HOC_SINH SET HO_TEN=?, NGAY_SINH=?, GIOI_TINH=?, DIA_CHI=?, MA_LOP=? WHERE MA_HOC_SINH=?";

            cauLenh = ketNoi.prepareStatement(sql);

            cauLenh.setString(1, hs.getHoTen());
            cauLenh.setDate(2, hs.getNgaySinh());
            cauLenh.setString(3, hs.getGioiTinh());
            cauLenh.setString(4, hs.getDiaChi());
            cauLenh.setString(5, hs.getMaLop());

            cauLenh.setString(6, hs.getMaHS());

            int ketQua = cauLenh.executeUpdate();
            if (ketQua > 0) return true;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dongKetNoi(ketNoi, cauLenh, null);
        }
        return false;
    }

    // Xóa học sinh khỏi CSDL
    public boolean delete(String maHS) {
        Connection ketNoi = null;
        PreparedStatement cauLenh = null;

        try {
            DatabaseConnection db = new DatabaseConnection();
            ketNoi = db.connect();

            String sql = "DELETE FROM HOC_SINH WHERE MA_HOC_SINH = ?";

            cauLenh = ketNoi.prepareStatement(sql);
            cauLenh.setString(1, maHS); 

            int ketQua = cauLenh.executeUpdate();
            if (ketQua > 0) return true;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dongKetNoi(ketNoi, cauLenh, null);
        }
        return false;
    }

    // 5. Lấy danh sách Lớp
    public ObservableList<String> getAllLop() {
        ObservableList<String> danhSachLop = FXCollections.observableArrayList();
        Connection ketNoi = null;
        Statement cauLenh = null;
        ResultSet ketQua = null;

        try {
            DatabaseConnection db = new DatabaseConnection();
            ketNoi = db.connect();

            cauLenh = ketNoi.createStatement();
            // Lấy mã lớp và sắp xếp theo thứ tự A-Z
            String sql = "SELECT MA_LOP FROM LOP_HOC ORDER BY MA_LOP";
            ketQua = cauLenh.executeQuery(sql);

            while (ketQua.next()) {
                danhSachLop.add(ketQua.getString("MA_LOP"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dongKetNoi(ketNoi, cauLenh, ketQua);
        }
        return danhSachLop;
    }

    // 6. Lọc danh sách học sinh theo lớp cụ thể
    public ObservableList<HocSinh> getHocSinhByLop(String maLop) {
        ObservableList<HocSinh> danhSachHocSinh = FXCollections.observableArrayList();
        Connection ketNoi = null;
        PreparedStatement cauLenh = null;
        ResultSet ketQua = null;

        try {
            DatabaseConnection db = new DatabaseConnection();
            ketNoi = db.connect();

            String sql = "SELECT * FROM HOC_SINH WHERE MA_LOP = ?";
            cauLenh = ketNoi.prepareStatement(sql);
            cauLenh.setString(1, maLop);

            ketQua = cauLenh.executeQuery();

            while (ketQua.next()) {
                danhSachHocSinh.add(new HocSinh(
                        ketQua.getString("MA_HOC_SINH"),
                        ketQua.getString("HO_TEN"),
                        ketQua.getDate("NGAY_SINH"),
                        ketQua.getString("GIOI_TINH"),
                        ketQua.getString("DIA_CHI"),
                        ketQua.getString("MA_LOP")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dongKetNoi(ketNoi, cauLenh, ketQua);
        }
        return danhSachHocSinh;
    }
    private void dongKetNoi(Connection conn, Statement st, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (st != null) st.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}