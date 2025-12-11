package repository;

import entity.BangDiem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BangDiemRepository {

    // Lấy danh sách tên các môn học
    public ObservableList<String> getAllMonHoc() {
        ObservableList<String> danhSachTenMon = FXCollections.observableArrayList();
        Connection ketNoi = null;
        Statement cauLenh = null;
        ResultSet ketQua = null;

        try {
            ketNoi = new DatabaseConnection().connect();
            String sql = "SELECT MA_MON FROM MON_HOC";
            cauLenh = ketNoi.createStatement();
            ketQua = cauLenh.executeQuery(sql);

            while (ketQua.next()) {
                danhSachTenMon.add(ketQua.getString("MA_MON"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dongKetNoi(ketNoi, cauLenh, ketQua);
        }
        return danhSachTenMon;
    }

    // Lấy danh sách tên các lớp
    public ObservableList<String> getAllLop() {
        ObservableList<String> danhSachTenLop = FXCollections.observableArrayList();
        Connection ketNoi = null;
        Statement cauLenh = null;
        ResultSet ketQua = null;

        try {
            ketNoi = new DatabaseConnection().connect();
            String sql = "SELECT MA_LOP FROM LOP_HOC ORDER BY MA_LOP";
            cauLenh = ketNoi.createStatement();
            ketQua = cauLenh.executeQuery(sql);

            while (ketQua.next()) {
                danhSachTenLop.add(ketQua.getString("MA_LOP"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dongKetNoi(ketNoi, cauLenh, ketQua);
        }
        return danhSachTenLop;
    }

    //  Lấy bảng điểm (Lọc theo Môn, Học kỳ, Lớp)
    public List<BangDiem> getBangDiemByFilter(String maMon, int hocKy, String maLop) {
        List<BangDiem> danhSachDiem = new ArrayList<>();
        Connection ketNoi = null;
        PreparedStatement cauLenh = null;
        ResultSet ketQua = null;

        try {
            ketNoi = new DatabaseConnection().connect();
            // Câu lệnh SQL nối bảng Học sinh và Bảng điểm
            String sql = "SELECT hs.MA_HOC_SINH, hs.HO_TEN, bd.* " +
                    "FROM HOC_SINH hs " +
                    "LEFT JOIN BANG_DIEM bd " +
                    "ON hs.MA_HOC_SINH = bd.MA_HOC_SINH AND bd.MA_MON = ? AND bd.HOC_KY = ? " +
                    "WHERE hs.MA_LOP = ?";

            cauLenh = ketNoi.prepareStatement(sql);
            cauLenh.setString(1, maMon);
            cauLenh.setInt(2, hocKy);
            cauLenh.setString(3, maLop);

            ketQua = cauLenh.executeQuery();

            while (ketQua.next()) {
                // Lấy thông tin cơ bản
                int id = 0;
                try {
                    id = ketQua.getInt("ID");
                } catch (SQLException ex) {} // Nếu chưa có điểm thì ID null

                String maHS = ketQua.getString("MA_HOC_SINH");
                String tenHS = ketQua.getString("HO_TEN");

                int hk = ketQua.getInt("HOC_KY");
                if (hk == 0) hk = hocKy;

                // Lấy điểm (xử lý trường hợp chưa nhập điểm thì trả về null hoặc 0)
                Float diemMieng = ketQua.getObject("DIEM_MIENG") != null ? ketQua.getFloat("DIEM_MIENG") : null;
                Float diem15p = ketQua.getObject("DIEM_15P") != null ? ketQua.getFloat("DIEM_15P") : null;
                Float diem1Tiet = ketQua.getObject("DIEM_1_TIET") != null ? ketQua.getFloat("DIEM_1_TIET") : null;
                Float diemThi = ketQua.getObject("DIEM_THI") != null ? ketQua.getFloat("DIEM_THI") : null;
                Float diemTB = ketQua.getObject("DIEM_TRUNG_BINH") != null ? ketQua.getFloat("DIEM_TRUNG_BINH") : null;

                BangDiem b = new BangDiem(id, maHS, tenHS, maMon, hk, diemMieng, diem15p, diem1Tiet, diemThi, diemTB, 0.0f);
                danhSachDiem.add(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dongKetNoi(ketNoi, cauLenh, ketQua);
        }
        return danhSachDiem;
    }

    // Lưu điểm
    public boolean saveDiem(String maHS, String maMon, int hocKy,
                            float diemMieng, float diem15Phut, float diem1Tiet, float diemThi, float diemTrungBinh) {

        Connection ketNoi = null;
        PreparedStatement cauLenhKiemTra = null;
        PreparedStatement cauLenhLuu = null;
        ResultSet ketQuaKiemTra = null;
        boolean thanhCong = false;

        try {
            ketNoi = new DatabaseConnection().connect();

            String sqlKiemTra = "SELECT * FROM BANG_DIEM WHERE MA_HOC_SINH = ? AND MA_MON = ? AND HOC_KY = ?";
            cauLenhKiemTra = ketNoi.prepareStatement(sqlKiemTra);
            cauLenhKiemTra.setString(1, maHS);
            cauLenhKiemTra.setString(2, maMon);
            cauLenhKiemTra.setInt(3, hocKy);

            ketQuaKiemTra = cauLenhKiemTra.executeQuery();
            boolean daCoDiem = ketQuaKiemTra.next();

            String sqlHanhDong = "";
            if (daCoDiem) {
                sqlHanhDong = "UPDATE BANG_DIEM SET DIEM_MIENG=?, DIEM_15P=?, DIEM_1_TIET=?, DIEM_THI=?, DIEM_TRUNG_BINH=? " +
                        "WHERE MA_HOC_SINH=? AND MA_MON=? AND HOC_KY=?";
            } else {
                sqlHanhDong = "INSERT INTO BANG_DIEM (DIEM_MIENG, DIEM_15P, DIEM_1_TIET, DIEM_THI, DIEM_TRUNG_BINH, MA_HOC_SINH, MA_MON, HOC_KY) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            }

            cauLenhLuu = ketNoi.prepareStatement(sqlHanhDong);
            cauLenhLuu.setFloat(1, diemMieng);
            cauLenhLuu.setFloat(2, diem15Phut);
            cauLenhLuu.setFloat(3, diem1Tiet);
            cauLenhLuu.setFloat(4, diemThi);
            cauLenhLuu.setFloat(5, diemTrungBinh);
            cauLenhLuu.setString(6, maHS);
            cauLenhLuu.setString(7, maMon);
            cauLenhLuu.setInt(8, hocKy);

            int soDongAnhHuong = cauLenhLuu.executeUpdate();
            if (soDongAnhHuong > 0) thanhCong = true;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (ketQuaKiemTra != null) ketQuaKiemTra.close();
                if (cauLenhKiemTra != null) cauLenhKiemTra.close();
                if (cauLenhLuu != null) cauLenhLuu.close();
                if (ketNoi != null) ketNoi.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return thanhCong;
    }

    // Thống kê điểm số
    public int[] getThongKe(String maMon, String maLop) {
        int[] ketQuaThongKe = new int[5]; // [giỏi, khá, tb, yếu, tổng số]
        Connection ketNoi = null;
        PreparedStatement cauLenh = null;
        ResultSet ketQua = null;

        try {
            ketNoi = new DatabaseConnection().connect();
            String sql;
            if (maLop == null || maLop.trim().isEmpty()) {
                sql = "SELECT DIEM_TRUNG_BINH FROM BANG_DIEM WHERE MA_MON = ?";
                cauLenh = ketNoi.prepareStatement(sql);
                cauLenh.setString(1, maMon);
            } else {
                sql = "SELECT bd.DIEM_TRUNG_BINH FROM BANG_DIEM bd JOIN HOC_SINH hs ON bd.MA_HOC_SINH = hs.MA_HOC_SINH " +
                        "WHERE bd.MA_MON = ? AND hs.MA_LOP = ?";
                cauLenh = ketNoi.prepareStatement(sql);
                cauLenh.setString(1, maMon);
                cauLenh.setString(2, maLop);
            }

            ketQua = cauLenh.executeQuery();
            while (ketQua.next()) {
                float diem = ketQua.getFloat(1);
                ketQuaThongKe[4]++; // Tăng tổng số học sinh

                // Phân loại
                if (diem >= 8.0) ketQuaThongKe[0]++;      // Giỏi
                else if (diem >= 6.5) ketQuaThongKe[1]++; // Khá
                else if (diem >= 5.0) ketQuaThongKe[2]++; // TB
                else ketQuaThongKe[3]++;                  // Yếu
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dongKetNoi(ketNoi, cauLenh, ketQua);
        }
        return ketQuaThongKe;
    }
    public ObservableList<BangDiem> getBangDiemByHocSinh(String maHS) {
        ObservableList<BangDiem> danhSachDiem = FXCollections.observableArrayList();
        Connection ketNoi = null;
        PreparedStatement cauLenh = null;
        ResultSet ketQua = null;

        try {
            ketNoi = new DatabaseConnection().connect();
            String sql = "SELECT bd.*, hs.HO_TEN FROM BANG_DIEM bd " +
                    "JOIN HOC_SINH hs ON bd.MA_HOC_SINH = hs.MA_HOC_SINH " +
                    "WHERE bd.MA_HOC_SINH = ?";

            cauLenh = ketNoi.prepareStatement(sql);
            cauLenh.setString(1, maHS);
            ketQua = cauLenh.executeQuery();

            while (ketQua.next()) {
                int id = ketQua.getInt("ID");
                String tenHS = ketQua.getString("HO_TEN");
                String maMon = ketQua.getString("MA_MON");

                int hocKy = ketQua.getInt("HOC_KY");

                Float diemMieng = ketQua.getObject("DIEM_MIENG") != null ? ketQua.getFloat("DIEM_MIENG") : null;
                Float diem15p = ketQua.getObject("DIEM_15P") != null ? ketQua.getFloat("DIEM_15P") : null;
                Float diem1Tiet = ketQua.getObject("DIEM_1_TIET") != null ? ketQua.getFloat("DIEM_1_TIET") : null;
                Float diemThi = ketQua.getObject("DIEM_THI") != null ? ketQua.getFloat("DIEM_THI") : null;
                Float diemTB = ketQua.getObject("DIEM_TRUNG_BINH") != null ? ketQua.getFloat("DIEM_TRUNG_BINH") : null;

                BangDiem b = new BangDiem(
                        id, maHS, tenHS, maMon, hocKy,
                        diemMieng, diem15p, diem1Tiet, diemThi, diemTB, 0.0f
                );
                danhSachDiem.add(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dongKetNoi(ketNoi, cauLenh, ketQua);
        }
        return danhSachDiem;
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