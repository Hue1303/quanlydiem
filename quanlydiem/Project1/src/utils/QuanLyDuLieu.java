package utils;

import entity.BangDiem;
import entity.HocSinh;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuanLyDuLieu {

    public QuanLyDuLieu() {
    }

    // 1. Lấy danh sách học sinh
    public ObservableList<HocSinh> layDanhSachHS(String tuKhoa) {
        ObservableList<HocSinh> danhSach = FXCollections.observableArrayList();
        Connection ketNoi = null;
        Statement cauLenh = null;
        ResultSet ketQua = null;

        try {
            ketNoi = new DatabaseConnection().connect();
            if (ketNoi != null) {
                String sql = "SELECT * FROM HOC_SINH";
                if (!tuKhoa.isEmpty()) {
                    sql = "SELECT * FROM HOC_SINH WHERE HO_TEN LIKE '%" + tuKhoa + "%' OR MA_HOC_SINH LIKE '%" + tuKhoa + "%'";
                }
                cauLenh = ketNoi.createStatement();
                ketQua = cauLenh.executeQuery(sql);

                while (ketQua.next()) {
                    HocSinh hs = new HocSinh(
                            ketQua.getString("MA_HOC_SINH"),
                            ketQua.getString("HO_TEN"),
                            ketQua.getDate("NGAY_SINH"),
                            ketQua.getString("GIOI_TINH"),
                            ketQua.getString("DIA_CHI"),
                            ketQua.getString("MA_LOP")
                    );
                    danhSach.add(hs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dongKetNoi(ketNoi, cauLenh, ketQua);
        }
        return danhSach;
    }

    // 2. Thêm Học Sinh Mới
    public boolean themHocSinh(HocSinh hs) {
        Connection ketNoi = null;
        PreparedStatement cauLenh = null;
        try {
            ketNoi = new DatabaseConnection().connect();
            String sql = "INSERT INTO HOC_SINH (MA_HOC_SINH, HO_TEN, NGAY_SINH, GIOI_TINH, DIA_CHI, MA_LOP) VALUES (?, ?, ?, ?, ?, ?)";
            cauLenh = ketNoi.prepareStatement(sql);
            cauLenh.setString(1, hs.getMaHS());
            cauLenh.setString(2, hs.getHoTen());
            cauLenh.setDate(3, hs.getNgaySinh());
            cauLenh.setString(4, hs.getGioiTinh());
            cauLenh.setString(5, hs.getDiaChi());
            cauLenh.setString(6, hs.getMaLop());
            return cauLenh.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace(); return false;
        } finally {
            dongKetNoi(ketNoi, cauLenh, null);
        }
    }

    // 3. Sửa Thông Tin Học Sinh
    public boolean suaHocSinh(HocSinh hs) {
        Connection ketNoi = null;
        PreparedStatement cauLenh = null;
        try {
            ketNoi = new DatabaseConnection().connect();
            String sql = "UPDATE HOC_SINH SET HO_TEN=?, NGAY_SINH=?, GIOI_TINH=?, DIA_CHI=?, MA_LOP=? WHERE MA_HOC_SINH=?";
            cauLenh = ketNoi.prepareStatement(sql);
            cauLenh.setString(1, hs.getHoTen());
            cauLenh.setDate(2, hs.getNgaySinh());
            cauLenh.setString(3, hs.getGioiTinh());
            cauLenh.setString(4, hs.getDiaChi());
            cauLenh.setString(5, hs.getMaLop());
            cauLenh.setString(6, hs.getMaHS());
            return cauLenh.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace(); return false;
        } finally {
            dongKetNoi(ketNoi, cauLenh, null);
        }
    }

    // 4. Xóa Học Sinh
    public boolean xoaHocSinh(String maHS) {
        Connection ketNoi = null;
        PreparedStatement cauLenh = null;
        try {
            ketNoi = new DatabaseConnection().connect();
            String sql = "DELETE FROM HOC_SINH WHERE MA_HOC_SINH = ?";
            cauLenh = ketNoi.prepareStatement(sql);
            cauLenh.setString(1, maHS);
            return cauLenh.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace(); return false;
        } finally {
            dongKetNoi(ketNoi, cauLenh, null);
        }
    }

    // 5. Lấy danh sách Bảng Điểm
    public List<BangDiem> layBangDiem(String maMon, int hocKy, String maLop) {
        List<BangDiem> list = new ArrayList<>();
        Connection ketNoi = null;
        PreparedStatement cauLenh = null;
        ResultSet ketQua = null;

        try {
            ketNoi = new DatabaseConnection().connect();
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
                int id = ketQua.getObject("ID") != null ? ketQua.getInt("ID") : -1;
                String mon = ketQua.getString("MA_MON") != null ? ketQua.getString("MA_MON") : maMon;
                int hk = ketQua.getObject("HOC_KY") != null ? ketQua.getInt("HOC_KY") : hocKy;

                Float diemMieng = ketQua.getObject("DIEM_MIENG") != null ? ketQua.getFloat("DIEM_MIENG") : null;
                Float diem15P = ketQua.getObject("DIEM_15P") != null ? ketQua.getFloat("DIEM_15P") : null;
                Float diem1Tiet = ketQua.getObject("DIEM_1_TIET") != null ? ketQua.getFloat("DIEM_1_TIET") : null;
                Float diemThi = ketQua.getObject("DIEM_THI") != null ? ketQua.getFloat("DIEM_THI") : null;
                Float diemTB = ketQua.getObject("DIEM_TRUNG_BINH") != null ? ketQua.getFloat("DIEM_TRUNG_BINH") : null;

                list.add(new BangDiem(id, ketQua.getString("MA_HOC_SINH"), ketQua.getString("HO_TEN"),
                        mon, hk, diemMieng, diem15P, diem1Tiet, diemThi, diemTB, 0.0f));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dongKetNoi(ketNoi, cauLenh, ketQua);
        }
        return list;
    }

    // 6. Lưu Điểm
    public boolean luuDiem(String maHS, String maMon, int hocKy, float m, float p15, float t1, float thi, float tb) {
        Connection ketNoi = null;
        PreparedStatement psCheck = null, psAction = null;
        ResultSet rsCheck = null;

        try {
            ketNoi = new DatabaseConnection().connect();
            String sqlCheck = "SELECT * FROM BANG_DIEM WHERE MA_HOC_SINH=? AND MA_MON=? AND HOC_KY=?";
            psCheck = ketNoi.prepareStatement(sqlCheck);
            psCheck.setString(1, maHS);
            psCheck.setString(2, maMon);
            psCheck.setInt(3, hocKy);
            rsCheck = psCheck.executeQuery();

            String sqlAction;
            if (rsCheck.next()) {
                sqlAction = "UPDATE BANG_DIEM SET DIEM_MIENG=?, DIEM_15P=?, DIEM_1_TIET=?, DIEM_THI=?, DIEM_TRUNG_BINH=? WHERE MA_HOC_SINH=? AND MA_MON=? AND HOC_KY=?";
            } else {
                sqlAction = "INSERT INTO BANG_DIEM (DIEM_MIENG, DIEM_15P, DIEM_1_TIET, DIEM_THI, DIEM_TRUNG_BINH, MA_HOC_SINH, MA_MON, HOC_KY) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            }

            psAction = ketNoi.prepareStatement(sqlAction);
            psAction.setFloat(1, m); psAction.setFloat(2, p15); psAction.setFloat(3, t1); psAction.setFloat(4, thi); psAction.setFloat(5, tb);
            psAction.setString(6, maHS); psAction.setString(7, maMon); psAction.setInt(8, hocKy);

            return psAction.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
        finally {
            try {
                if (rsCheck != null) rsCheck.close();
                if (psCheck != null) psCheck.close();
                if (psAction != null) psAction.close();
                if (ketNoi != null) ketNoi.close();
            } catch (SQLException e) {}
        }
    }

    // 7. Đổi Mật Khẩu
    public String doiMatKhau(String user, String oldPass, String newPass) {
        Connection ketNoi = null;
        try {
            ketNoi = new DatabaseConnection().connect();
            ResultSet rs = ketNoi.createStatement().executeQuery("SELECT * FROM GIAO_VIEN WHERE TEN_DANG_NHAP='" + user + "' AND MAT_KHAU='" + oldPass + "'");
            if (rs.next()) {
                ketNoi.createStatement().executeUpdate("UPDATE GIAO_VIEN SET MAT_KHAU='" + newPass + "' WHERE TEN_DANG_NHAP='" + user + "'");
                return "OK";
            } else return "SAI_MK";
        } catch (Exception e) { return "LỖI"; }
        finally { try { if (ketNoi != null) ketNoi.close(); } catch (SQLException e) {} }
    }

    // 8. Tính Điểm Tổng Kết Năm
    public float[] layDiemTongKetNam(String maHS) {
        float[] ketQua = {0, 0, 0};
        Connection ketNoi = null;
        try {
            ketNoi = new DatabaseConnection().connect();
            Statement st = ketNoi.createStatement();

            // Tính TB HK1
            ResultSet rs1 = st.executeQuery("SELECT AVG(DIEM_TRUNG_BINH) FROM BANG_DIEM WHERE MA_HOC_SINH='" + maHS + "' AND HOC_KY=1");
            if (rs1.next()) ketQua[0] = (float) (Math.round(rs1.getFloat(1) * 10.0) / 10.0);

            // Tính TB HK2
            ResultSet rs2 = st.executeQuery("SELECT AVG(DIEM_TRUNG_BINH) FROM BANG_DIEM WHERE MA_HOC_SINH='" + maHS + "' AND HOC_KY=2");
            if (rs2.next()) ketQua[1] = (float) (Math.round(rs2.getFloat(1) * 10.0) / 10.0);

            // Tính Cả Năm
            if (ketQua[0] > 0 && ketQua[1] > 0) {
                float caNam = (ketQua[0] + ketQua[1] * 2) / 3;
                ketQua[2] = (float) (Math.round(caNam * 10.0) / 10.0);
            }
        } catch (Exception e) { e.printStackTrace(); }
        finally { try { if (ketNoi != null) ketNoi.close(); } catch (SQLException e) {} }
        return ketQua;
    }

    // 9. Thống kê Học lực Lớp
    public int[] thongKeHocLucLop(String maLop, int hocKy) {
        int[] ketQuaThongKe = {0, 0, 0, 0};
        Connection ketNoi = null;
        try {
            ketNoi = new DatabaseConnection().connect();

            // TRƯỜNG HỢP 1: THỐNG KÊ CẢ NĂM
            if (hocKy == 0) {
                Statement st = ketNoi.createStatement();
                ResultSet rsHS = st.executeQuery("SELECT MA_HOC_SINH FROM HOC_SINH WHERE MA_LOP = '" + maLop + "'");

                while (rsHS.next()) {
                    String maHS = rsHS.getString("MA_HOC_SINH");
                    float[] bangDiem = layDiemTongKetNam(maHS);
                    float diemCaNam = bangDiem[2];
                    if (diemCaNam > 0) {
                        if (diemCaNam >= 8.0) ketQuaThongKe[0]++;
                        else if (diemCaNam >= 6.5) ketQuaThongKe[1]++;
                        else if (diemCaNam >= 5.0) ketQuaThongKe[2]++;
                        else ketQuaThongKe[3]++;
                    }
                }
                rsHS.close(); st.close();
            }
            // TRƯỜNG HỢP 2: THỐNG KÊ HỌC KỲ 1 HOẶC 2
            else {
                String sql = "SELECT MA_HOC_SINH, AVG(DIEM_TRUNG_BINH) as DTB FROM BANG_DIEM " +
                        "WHERE HOC_KY = " + hocKy + " AND MA_HOC_SINH IN (SELECT MA_HOC_SINH FROM HOC_SINH WHERE MA_LOP = '" + maLop + "') " +
                        "GROUP BY MA_HOC_SINH";

                Statement st = ketNoi.createStatement();
                ResultSet rs = st.executeQuery(sql);
                while (rs.next()) {
                    float dtb = rs.getFloat("DTB");
                    if (dtb >= 8.0) ketQuaThongKe[0]++;
                    else if (dtb >= 6.5) ketQuaThongKe[1]++;
                    else if (dtb >= 5.0) ketQuaThongKe[2]++;
                    else ketQuaThongKe[3]++;
                }
                rs.close(); st.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (ketNoi != null) ketNoi.close(); } catch (SQLException e) {}
        }
        return ketQuaThongKe;
    }

    // 10. Lấy Điểm TB Học Kỳ
    public Float layDiemTBHocKy(String maHS, String maMon, int hocKy) {
        Connection ketNoi = null;
        try {
            ketNoi = new DatabaseConnection().connect();
            String sql = "SELECT DIEM_TRUNG_BINH FROM BANG_DIEM WHERE MA_HOC_SINH = ? AND MA_MON = ? AND HOC_KY = ?";
            PreparedStatement ps = ketNoi.prepareStatement(sql);
            ps.setString(1, maHS); ps.setString(2, maMon); ps.setInt(3, hocKy);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Object result = rs.getObject("DIEM_TRUNG_BINH");
                if (result != null) return rs.getFloat("DIEM_TRUNG_BINH");
            }
        } catch (Exception e) { e.printStackTrace(); }
        finally { try { if (ketNoi != null) ketNoi.close(); } catch (SQLException e) {} }
        return null;
    }

    // Hàm phụ đóng kết nối
    private void dongKetNoi(Connection conn, Statement st, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (st != null) st.close();
            if (conn != null) conn.close();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}