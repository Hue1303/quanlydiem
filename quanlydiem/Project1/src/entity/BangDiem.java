package entity;
public class BangDiem {
    private int id;
    private String maHS;
    private String hoTen;
    private String maMon;
    private int hocKy;
    private Float diemMieng;
    private Float diem15Phut;
    private Float diem1Tiet;
    private Float diemThi;

    private Float diemTBHocKy;
    private Float diemTBCaNam;

    public BangDiem() {
    }

    public BangDiem(int id, String maHS, String hoTen, String maMon, int hocKy,
                    Float diemMieng, Float diem15Phut, Float diem1Tiet, Float diemThi,
                    Float diemTBHocKy, Float diemTBCaNam) {
        this.id = id;
        this.maHS = maHS;
        this.hoTen = hoTen;
        this.maMon = maMon;
        this.hocKy = hocKy;
        this.diemMieng = diemMieng;
        this.diem15Phut = diem15Phut;
        this.diem1Tiet = diem1Tiet;
        this.diemThi = diemThi;
        this.diemTBHocKy = diemTBHocKy;
        this.diemTBCaNam = diemTBCaNam;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getMaHS() {
        return maHS;
    }
    public void setMaHS(String maHS) {
        this.maHS = maHS;
    }

    public String getHoTen() {
        return hoTen;
    }
    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getMaMon() {
        return maMon;
    }
    public void setMaMon(String maMon) {
        this.maMon = maMon;
    }

    public int getHocKy() {
        return hocKy;
    }
    public void setHocKy(int hocKy) {
        this.hocKy = hocKy;
    }

    public Float getDiemMieng() {
        return diemMieng;
    }
    public void setDiemMieng(Float diemMieng) {
        this.diemMieng = diemMieng;
    }

    public Float getDiem15Phut() {
        return diem15Phut;
    }
    public void setDiem15Phut(Float diem15Phut) {
        this.diem15Phut = diem15Phut;
    }

    public Float getDiem1Tiet() {
        return diem1Tiet;
    }
    public void setDiem1Tiet(Float diem1Tiet) {
        this.diem1Tiet = diem1Tiet;
    }

    public Float getDiemThi() {
        return diemThi;
    }
    public void setDiemThi(Float diemThi) {
        this.diemThi = diemThi;
    }

    public Float getDiemTBHocKy() {
        return diemTBHocKy;
    }
    public void setDiemTBHocKy(Float diemTBHocKy) {
        this.diemTBHocKy = diemTBHocKy;
    }

    public Float getDiemTBCaNam() {
        return diemTBCaNam;
    }
    public void setDiemTBCaNam(Float diemTBCaNam) {
        this.diemTBCaNam = diemTBCaNam;
    }
}