
DROP DATABASE IF EXISTS quanlydiem;
CREATE DATABASE quanlydiem CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE quanlydiem;

-- PHẦN 1: TẠO BẢNG
CREATE TABLE GIAO_VIEN (
    TEN_DANG_NHAP VARCHAR(50) PRIMARY KEY,
    MAT_KHAU VARCHAR(50) NOT NULL,
    HO_TEN NVARCHAR(100),
    VAI_TRO VARCHAR(20) DEFAULT 'GV_BO_MON', 
    CHUYEN_MON VARCHAR(10) DEFAULT NULL  
);

CREATE TABLE LOP_HOC (
    MA_LOP VARCHAR(10) PRIMARY KEY,
    TEN_LOP NVARCHAR(50)
);

CREATE TABLE MON_HOC (
    MA_MON VARCHAR(10) PRIMARY KEY,
    TEN_MON NVARCHAR(50),
    HE_SO INT DEFAULT 1
);

CREATE TABLE HOC_SINH (
    MA_HOC_SINH VARCHAR(20) PRIMARY KEY,
    HO_TEN NVARCHAR(100) NOT NULL,
    NGAY_SINH DATE NOT NULL,
    GIOI_TINH NVARCHAR(10),
    DIA_CHI NVARCHAR(200),
    MA_LOP VARCHAR(10),
    FOREIGN KEY (MA_LOP) REFERENCES LOP_HOC(MA_LOP) ON UPDATE CASCADE
);

CREATE TABLE BANG_DIEM (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    MA_HOC_SINH VARCHAR(20),
    MA_MON VARCHAR(10),
    DIEM_MIENG FLOAT DEFAULT 0,
    DIEM_15P FLOAT DEFAULT 0,
    DIEM_1_TIET FLOAT DEFAULT 0,
    DIEM_THI FLOAT DEFAULT 0,
    DIEM_TRUNG_BINH FLOAT DEFAULT 0,
    FOREIGN KEY (MA_HOC_SINH) REFERENCES HOC_SINH(MA_HOC_SINH) ON DELETE CASCADE,
    FOREIGN KEY (MA_MON) REFERENCES MON_HOC(MA_MON) ON UPDATE CASCADE,
    UNIQUE KEY DUY_NHAT (MA_HOC_SINH, MA_MON)
);

-- PHẦN 2: DỮ LIỆU CỐ ĐỊNH (MÔN VÀ LỚP)

-- Môn học
INSERT INTO MON_HOC (MA_MON, TEN_MON, HE_SO) VALUES 
('TOAN', 'Toán Học', 2), ('VAN', 'Ngữ Văn', 2), ('NN', 'Ngoại Ngữ', 2),
('LY', 'Vật Lý', 1), ('HOA', 'Hóa Học', 1), ('SINH', 'Sinh Học', 1),
('SU', 'Lịch Sử', 1), ('DIA', 'Địa Lý', 1), ('GDCD', 'Giáo Dục Công Dân', 1),
('TIN', 'Tin Học', 1), ('CN', 'Công Nghệ', 1), ('THE_DUC', 'Thể Dục', 1);

-- Lớp học
INSERT INTO LOP_HOC (MA_LOP, TEN_LOP) VALUES 
('6A', 'Lớp 6A'), ('6B', 'Lớp 6B'), ('6C', 'Lớp 6C'), ('6D', 'Lớp 6D'),
('7A', 'Lớp 7A'), ('7B', 'Lớp 7B'), ('7C', 'Lớp 7C'), ('7D', 'Lớp 7D'),
('8A', 'Lớp 8A'), ('8B', 'Lớp 8B'), ('8C', 'Lớp 8C'), ('8D', 'Lớp 8D'),
('9A', 'Lớp 9A'), ('9B', 'Lớp 9B'), ('9C', 'Lớp 9C'), ('9D', 'Lớp 9D');

-- PHẦN 3: DANH SÁCH GIÁO VIÊN 
-- Admin (Hiệu trưởng) -> VAI_TRO='ADMIN'
INSERT INTO GIAO_VIEN VALUES ('admin', '123', 'Bùi Văn Phúc (Admin)', 'ADMIN', NULL);

-- 1. Tổ Toán (Mã môn: TOAN)
INSERT INTO GIAO_VIEN VALUES 
('T1', '123', 'Hạ Vũ Anh', 'GV_BO_MON', 'TOAN'),
('T2', '123', 'Phan Hồng Anh', 'GV_BO_MON', 'TOAN'),
('T3', '123', 'Nguyễn Thị Vân Anh', 'GV_BO_MON', 'TOAN'),
('T4', '123', 'Nguyễn Đức Cường', 'GV_BO_MON', 'TOAN'),
('T5', '123', 'Hà Thị Mai Dung', 'GV_BO_MON', 'TOAN'),
('T6', '123', 'Cao Văn Dũng', 'GV_BO_MON', 'TOAN');

-- 2. Tổ Tin Học (Mã môn: TIN)
INSERT INTO GIAO_VIEN VALUES 
('Tin1', '123', 'Đỗ Huy Hiếu', 'GV_BO_MON', 'TIN'),
('Tin2', '123', 'Lê Thị Thúy', 'GV_BO_MON', 'TIN'),
('Tin3', '123', 'Hoàng Thị Vân', 'GV_BO_MON', 'TIN'),
('Tin4', '123', 'Bùi Tiến Dũng', 'GV_BO_MON', 'TIN'),
('Tin5', '123', 'Lê Thị Thu Hà', 'GV_BO_MON', 'TIN'),
('Tin6', '123', 'Trịnh Quang Hải', 'GV_BO_MON', 'TIN'),
('Tin7', '123', 'Trịnh Tuấn Kiệt', 'GV_BO_MON', 'TIN'),
('Tin8', '123', 'Nguyễn Hoàng Long', 'GV_BO_MON', 'TIN');

-- 3. Tổ Anh (Mã môn: NN)
INSERT INTO GIAO_VIEN VALUES 
('A1', '123', 'Vũ Thị Kiều Anh', 'GV_BO_MON', 'NN'),
('A2', '123', 'Nguyễn Văn Bắc', 'GV_BO_MON', 'NN'),
('A3', '123', 'Nguyễn Ngọc Diệp Chi', 'GV_BO_MON', 'NN'),
('A4', '123', 'Bùi Ánh Dương', 'GV_BO_MON', 'NN'),
('A5', '123', 'Trần Thu Giang', 'GV_BO_MON', 'NN');

-- 4. Tổ Văn (Mã môn: VAN)
INSERT INTO GIAO_VIEN VALUES 
('V1', '123', 'Đặng Nguyệt Anh', 'GV_BO_MON', 'VAN'),
('V2', '123', 'Dương Tú Anh', 'GV_BO_MON', 'VAN'),
('V3', '123', 'Bùi Thị Kim Ánh', 'GV_BO_MON', 'VAN'),
('V4', '123', 'Nguyễn Thanh Bình', 'GV_BO_MON', 'VAN'),
('V5', '123', 'Khương Thị Thu Cúc', 'GV_BO_MON', 'VAN'),
('V6', '123', 'Nguyễn Lê Dung', 'GV_BO_MON', 'VAN'),
('V7', '123', 'Đỗ Thị Thu Hằng', 'GV_BO_MON', 'VAN'),
('V8', '123', 'Lê Thị Thanh Huyền', 'GV_BO_MON', 'VAN');

-- 5. Tổ Lý (Mã môn: LY)
INSERT INTO GIAO_VIEN VALUES 
('L1', '123', 'Vũ Minh Thư', 'GV_BO_MON', 'LY'),
('L2', '123', 'Nguyễn Thị Thanh Huệ', 'GV_BO_MON', 'LY'),
('L3', '123', 'Đặng Giang Linh', 'GV_BO_MON', 'LY'),
('L4', '123', 'Nguyễn Ngọc Linh', 'GV_BO_MON', 'LY'),
('L5', '123', 'Nguyễn Hải Anh', 'GV_BO_MON', 'LY');

-- 6. Tổ Hóa (Mã môn: HOA)
INSERT INTO GIAO_VIEN VALUES 
('H1', '123', 'Nguyễn Quang Anh', 'GV_BO_MON', 'HOA'),
('H2', '123', 'Đặng Giang Anh', 'GV_BO_MON', 'HOA'),
('H3', '123', 'Đặng Ngọc Anh', 'GV_BO_MON', 'HOA'),
('H4', '123', 'Hồ Linh Anh', 'GV_BO_MON', 'HOA'),
('H5', '123', 'Dương Hoàng Hải', 'GV_BO_MON', 'HOA');

-- 7. Tổ Sinh (Mã môn: SINH)
INSERT INTO GIAO_VIEN VALUES 
('SH1', '123', 'Vũ Thị Trúc', 'GV_BO_MON', 'SINH'),
('SH2', '123', 'Đỗ Minh Ngọc', 'GV_BO_MON', 'SINH'),
('SH3', '123', 'Đặng Hồng Ngọc', 'GV_BO_MON', 'SINH'),
('SH4', '123', 'Giang Thị Bích', 'GV_BO_MON', 'SINH'),
('SH5', '123', 'Đặng Trung Anh', 'GV_BO_MON', 'SINH');

-- 8. Tổ Địa (Mã môn: DIA)
INSERT INTO GIAO_VIEN VALUES 
('D1', '123', 'Đinh Công Hoàn', 'GV_BO_MON', 'DIA'),
('D2', '123', 'Đặng Nguyệt Linh', 'GV_BO_MON', 'DIA'),
('D3', '123', 'Dương Hoàng Linh', 'GV_BO_MON', 'DIA'),
('D4', '123', 'Nguyễn Văn Dương', 'GV_BO_MON', 'DIA'),
('D5', '123', 'Lê Thị Bích', 'GV_BO_MON', 'DIA');

-- 9. Tổ GDCD (Mã môn: GDCD)
INSERT INTO GIAO_VIEN VALUES 
('GD1', '123', 'Dương Ngọc Anh Linh', 'GV_BO_MON', 'GDCD'),
('GD2', '123', 'Nguyễn Hoàng Ninh', 'GV_BO_MON', 'GDCD'),
('GD3', '123', 'Đặng Nguyệt Anh', 'GV_BO_MON', 'GDCD'),
('GD4', '123', 'Nguyễn Thu Hà', 'GV_BO_MON', 'GDCD'),
('GD5', '123', 'Phạm Minh Trang', 'GV_BO_MON', 'GDCD');

-- 10. Tổ Công Nghệ (Mã môn: CN)
INSERT INTO GIAO_VIEN VALUES 
('CN1', '123', 'Lê Quang Huy', 'GV_BO_MON', 'CN'),
('CN2', '123', 'Vũ Thảo My', 'GV_BO_MON', 'CN'),
('CN3', '123', 'Ngô Đức Mạnh', 'GV_BO_MON', 'CN'),
('CN4', '123', 'Trần Hải Yến', 'GV_BO_MON', 'CN'),
('CN5', '123', 'Bùi Thanh Tùng', 'GV_BO_MON', 'CN');

-- 11. Tổ Sử (Mã môn: SU)
INSERT INTO GIAO_VIEN VALUES 
('S1', '123', 'Trần Thị Hòa', 'GV_BO_MON', 'SU'),
('S2', '123', 'Nguyễn Đức Toàn', 'GV_BO_MON', 'SU'),
('S3', '123', 'Phạm Thúy An', 'GV_BO_MON', 'SU'),
('S4', '123', 'Đỗ Minh Phúc', 'GV_BO_MON', 'SU'),
('S5', '123', 'Lưu Ngọc Bích', 'GV_BO_MON', 'SU');

-- 12. Tổ Thể Dục (Mã môn: THE_DUC)
INSERT INTO GIAO_VIEN VALUES 
('TD1', '123', 'Nguyễn Văn Dũng', 'GV_BO_MON', 'THE_DUC'),
('TD2', '123', 'Phạm Thu Hường', 'GV_BO_MON', 'THE_DUC'),
('TD3', '123', 'Trần Quốc Khánh', 'GV_BO_MON', 'THE_DUC'),
('TD4', '123', 'Đào Minh Quân', 'GV_BO_MON', 'THE_DUC'),
('TD5', '123', 'Hoàng Thị Mai', 'GV_BO_MON', 'THE_DUC');
