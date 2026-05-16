-- ============================================================
-- CATALOG SERVICE - import.sql
-- 28 sản phẩm khớp với ảnh thực tế trong coolstore-fe/src/assets/
-- ============================================================

-- ======= Thời trang Nam (8 sản phẩm) =======
INSERT INTO catalog (itemId, title, category, description, price, image) VALUES
('329299', 'Áo Thun taskkill /F /IM java.exeQuarkus', 'Thoi trang nam',
 'Áo thun nam chất liệu cotton 100%, in logo Quarkus nổi bật. Thoáng mát, thấm hút mồ hôi tốt, phù hợp mặc hằng ngày.',
 100000.00, '329299.jpg');

INSERT INTO catalog (itemId, title, category, description, price, image) VALUES
('329199', 'Áo Thun Kubernetes', 'Thoi trang nam',
 'Áo thun nam với thiết kế in chữ "Pronounced Kubernetes". Chất vải mềm mịn, co giãn 4 chiều, cực kỳ thoải mái.',
 9.00, '329199.jpg');

INSERT INTO catalog (itemId, title, category, description, price, image) VALUES
('1', 'Balo Laptop Fjallraven 15 inch', 'Thoi trang nam',
 'Balo cao cấp Fjallraven - Foldsack No.1, vừa laptop 15 inch. Chất liệu bền, thiết kế tinh tế, ngăn đệm chống sốc cho laptop. Hoàn hảo cho đi làm và du lịch.',
 109.95, '81fPKd-2AYL._AC_SL1500_.jpg');

INSERT INTO catalog (itemId, title, category, description, price, image) VALUES
('2', 'Áo Thun Nam Slim Fit Premium', 'Thoi trang nam',
 'Áo thun nam dáng slim fit cao cấp, cổ tròn, tay dài contrast. Chất vải nhẹ nhàng, thoáng mát, phù hợp đi làm và đi chơi.',
 22.30, '71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg');

INSERT INTO catalog (itemId, title, category, description, price, image) VALUES
('3', 'Áo Khoác Nam Cotton Outdoor', 'Thoi trang nam',
 'Áo khoác nam chất cotton cao cấp, phù hợp mùa xuân/thu/đông. Thiết kế nhiều túi tiện lợi, thích hợp leo núi, cắm trại, du lịch.',
 55.99, '71li-ujtlUL._AC_UX679_.jpg');

INSERT INTO catalog (itemId, title, category, description, price, image) VALUES
('4', 'Quần Jean Nam Slim Casual', 'Thoi trang nam',
 'Quần jean nam dáng slim, chất liệu denim co giãn 4 chiều. Thiết kế tối giản, dễ phối đồ, phù hợp nhiều dịp khác nhau.',
 15.99, '71YXzeOuslL._AC_UY879_.jpg');

INSERT INTO catalog (itemId, title, category, description, price, image) VALUES
('444434', 'Áo Thun Nữ Red Hat', 'Thoi trang nu',
 'Áo thun nữ Red Hat Impact, chất cotton mềm mịn, form dáng nữ tính. In logo Red Hat nổi bật, màu sắc trẻ trung.',
 9.00, '444434.jpg');

-- ======= Thời trang Nữ (7 sản phẩm) =======
INSERT INTO catalog (itemId, title, category, description, price, image) VALUES
('15', 'Áo Khoác Trượt Tuyết 3-in-1 Nữ', 'Thoi trang nu',
 'Áo khoác nữ 3-in-1 chống tuyết, có thể tháo rời lớp lót. Chất liệu 100% Polyester, cổ đứng, có nhiều túi khóa kéo tiện lợi.',
 56.99, '51Y5NI-I5jL._AC_UX679_.jpg');

INSERT INTO catalog (itemId, title, category, description, price, image) VALUES
('16', 'Áo Khoác Da Moto Nữ có Mũ', 'Thoi trang nu',
 'Áo khoác da tổng hợp phong cách moto, có mũ tháo rời. Chất liệu PU cao cấp, 2 túi trước, đường may chắc chắn.',
 29.95, '81XH0e8fefL._AC_UY879_.jpg');

INSERT INTO catalog (itemId, title, category, description, price, image) VALUES
('17', 'Áo Khoác Mưa Nữ Có Sọc', 'Thoi trang nu',
 'Áo khoác mưa nữ nhẹ nhàng, thiết kế có sọc nổi bật. Có mũ điều chỉnh được, 2 túi bên, vừa thời trang vừa thực dụng.',
 39.99, '71HblAHs5xL._AC_UY879_-2.jpg');

INSERT INTO catalog (itemId, title, category, description, price, image) VALUES
('18', 'Áo Thun Nữ Cổ V Basic', 'Thoi trang nu',
 'Áo thun nữ cổ V trơn, chất 95% Rayon 5% Spandex. Co giãn tốt, ôm dáng nhẹ, phù hợp mặc trong và ngoài.',
 9.85, '71z3kpMAYsL._AC_UY879_.jpg');

INSERT INTO catalog (itemId, title, category, description, price, image) VALUES
('19', 'Áo Thun Nữ Thoáng Khí Thể Thao', 'Thoi trang nu',
 'Áo thun nữ 100% Polyester, công nghệ thoát ẩm cao cấp. Nhẹ nhàng, thoáng khí, phù hợp tập thể thao và đi chơi.',
 7.95, '51eg55uWmdL._AC_UX679_.jpg');

INSERT INTO catalog (itemId, title, category, description, price, image) VALUES
('20', 'Áo Thun Nữ Cotton In Chữ', 'Thoi trang nu',
 'Áo thun nữ 95% Cotton 5% Spandex, in chữ thời trang. Chất vải mềm có độ co giãn, cổ V thanh lịch, phù hợp đi làm và đi chơi.',
 12.99, '61pHAEJ4NML._AC_UX679_.jpg');

-- ======= Trang sức (4 sản phẩm) =======
INSERT INTO catalog (itemId, title, category, description, price, image) VALUES
('5', 'Vòng Tay Rồng Vàng Bạc John Hardy', 'Trang suc',
 'Vòng tay cao cấp từ bộ sưu tập Legends của John Hardy, thiết kế hình rồng Naga. Chất liệu vàng và bạc kết hợp tinh tế, biểu tượng của tình yêu và sự bảo hộ.',
 695.00, '71pWzhdJNwL._AC_UL640_QL65_ML3_.jpg');

INSERT INTO catalog (itemId, title, category, description, price, image) VALUES
('6', 'Nhẫn Kim Cương Micropave Vàng Đặc', 'Trang suc',
 'Nhẫn vàng đặc micropave đính kim cương nhỏ. Đổi trả trong 30 ngày. Thiết kế và bán bởi Hafeez Center, xuất xứ Mỹ.',
 168.00, '61sbMiUnoGL._AC_UL640_QL65_ML3_.jpg');

INSERT INTO catalog (itemId, title, category, description, price, image) VALUES
('7', 'Nhẫn Hứa Hôn Công Chúa Vàng Trắng', 'Trang suc',
 'Nhẫn đính hôn công chúa mạ vàng trắng, đính hột xoàn nhân tạo. Quà tặng ý nghĩa cho ngày Valentine, kỷ niệm, đám cưới.',
 9.99, '71YAIFU48IL._AC_UL640_QL65_ML3_.jpg');

INSERT INTO catalog (itemId, title, category, description, price, image) VALUES
('8', 'Bông Tai Đôi Hoa Hồng Vàng Inox', 'Trang suc',
 'Bông tai đôi hình ống làm từ thép không gỉ 316L mạ vàng hồng. Thiết kế hoa văn tinh tế, chất lượng cao, không gỉ sét.',
 10.99, '51UDEzMJVpL._AC_UL640_QL65_ML3_.jpg');

-- ======= Điện tử (6 sản phẩm) =======
INSERT INTO catalog (itemId, title, category, description, price, image) VALUES
('9', 'Ổ Cứng Di Động WD 2TB USB 3.0', 'Dien tu',
 'Ổ cứng di động WD Elements 2TB, cổng USB 3.0 tốc độ cao. Tương thích Windows, dễ cắm dùng ngay, thiết kế gọn nhẹ.',
 64.00, '61IBBVJvSDL._AC_SY879_.jpg');

INSERT INTO catalog (itemId, title, category, description, price, image) VALUES
('10', 'SSD Nội Bộ SanDisk 1TB SATA III', 'Dien tu',
 'SSD nội bộ SanDisk PLUS 1TB, tốc độ đọc/ghi 535/450 MB/s. Nâng cấp tuyệt vời để máy tính khởi động nhanh hơn gấp nhiều lần.',
 109.00, '61U7T1koQqL._AC_SX679_.jpg');

INSERT INTO catalog (itemId, title, category, description, price, image) VALUES
('11', 'SSD Silicon Power 256GB 3D NAND', 'Dien tu',
 'SSD Silicon Power 256GB, công nghệ 3D NAND flash. Hỗ trợ TRIM, Garbage Collection, RAID, ECC. Form factor 2.5 inch siêu mỏng 7mm.',
 109.00, '71kWymZ+c+L._AC_SX679_.jpg');

INSERT INTO catalog (itemId, title, category, description, price, image) VALUES
('12', 'Ổ Cứng Gaming WD 4TB PS4', 'Dien tu',
 'Ổ cứng ngoài WD 4TB chuyên dành cho PS4. Cắm và chơi ngay, mở rộng kho game không giới hạn. Bảo hành nhà sản xuất 3 năm.',
 114.00, '61mtL65D4cL._AC_SX679_.jpg');

INSERT INTO catalog (itemId, title, category, description, price, image) VALUES
('13', 'Màn Hình Acer SB220Q 21.5 inch FHD IPS', 'Dien tu',
 'Màn hình Acer 21.5 inch Full HD 1920x1080, tấm nền IPS góc nhìn rộng 178°. Tần số 75Hz, thời gian phản hồi 4ms, viền siêu mỏng.',
 599.00, '81QpkIctqPL._AC_SX679_.jpg');

INSERT INTO catalog (itemId, title, category, description, price, image) VALUES
('14', 'Samsung 49 inch CHG90 Curved Gaming 144Hz', 'Dien tu',
 'Màn hình gaming Samsung 49 inch cong Super Ultrawide 32:9, QLED, HDR. Tần số 144Hz, 1ms, loại bỏ hoàn toàn blur và ghosting.',
 999.99, '81Zt42ioCgL._AC_SX679_.jpg');

-- ======= Phụ kiện (5 sản phẩm) =======
INSERT INTO catalog (itemId, title, category, description, price, image) VALUES
('165613', 'Tất Len Cao Cấp', 'Phu kien',
 'Tất len đan tay cao cấp, giữ ấm tốt. Chất liệu len mềm mịn, không gây ngứa, phù hợp cho mùa đông.',
 4.15, '165613.jpg');

INSERT INTO catalog (itemId, title, category, description, price, image) VALUES
('165614', 'Bình Nước Quarkus H2Go', 'Phu kien',
 'Bình nước Quarkus H2Go dung tích 600ml, chất liệu inox không gỉ 2 lớp cách nhiệt. Giữ lạnh 24h, giữ nóng 12h.',
 14.45, '165614.jpg');

INSERT INTO catalog (itemId, title, category, description, price, image) VALUES
('165954', 'Balo Du Lịch Patagonia 28L', 'Phu kien',
 'Balo Patagonia Refugio 28L, thiết kế gọn gàng chắc chắn. Ngăn laptop riêng, chống thấm nước, dây đeo êm ái.',
 6.00, '165954.jpg');

INSERT INTO catalog (itemId, title, category, description, price, image) VALUES
('444435', 'Mũ Lưỡi Trai Quarkus Twill', 'Phu kien',
 'Mũ lưỡi trai Quarkus vải twill cao cấp, có khóa điều chỉnh. In logo Quarkus tinh tế, phong cách trẻ trung.',
 13.00, '444435.jpg');

INSERT INTO catalog (itemId, title, category, description, price, image) VALUES
('444437', 'Miếng Dán Che Webcam Nanobloc', 'Phu kien',
 'Miếng che webcam laptop siêu mỏng Nanobloc, bảo vệ riêng tư. Keo dán đặc biệt không để lại vết, dùng được cho mọi loại laptop.',
 2.75, '444437.jpg');