-- ============================================================
-- INVENTORY SERVICE - import.sql
-- Tồn kho cho 28 sản phẩm khớp với catalog
-- SỬA: Thêm ON CONFLICT DO NOTHING để tránh lỗi khi restart
-- ============================================================

-- Thời trang Nam
INSERT INTO INVENTORY (id, itemId, link, location, quantity) VALUES
(1, '329299', 'http://maps.google.com/?q=Ha+Noi', 'Hà Nội', 150)
ON CONFLICT (id) DO NOTHING;

INSERT INTO INVENTORY (id, itemId, link, location, quantity) VALUES
(2, '329199', 'http://maps.google.com/?q=Ho+Chi+Minh', 'TP.HCM', 200)
ON CONFLICT (id) DO NOTHING;

INSERT INTO INVENTORY (id, itemId, link, location, quantity) VALUES
(3, '1', 'http://maps.google.com/?q=Da+Nang', 'Đà Nẵng', 85)
ON CONFLICT (id) DO NOTHING;

INSERT INTO INVENTORY (id, itemId, link, location, quantity) VALUES
(4, '2', 'http://maps.google.com/?q=Ha+Noi', 'Hà Nội', 320)
ON CONFLICT (id) DO NOTHING;

INSERT INTO INVENTORY (id, itemId, link, location, quantity) VALUES
(5, '3', 'http://maps.google.com/?q=Ho+Chi+Minh', 'TP.HCM', 145)
ON CONFLICT (id) DO NOTHING;

INSERT INTO INVENTORY (id, itemId, link, location, quantity) VALUES
(6, '4', 'http://maps.google.com/?q=Hai+Phong', 'Hải Phòng', 260)
ON CONFLICT (id) DO NOTHING;

INSERT INTO INVENTORY (id, itemId, link, location, quantity) VALUES
(7, '444434', 'http://maps.google.com/?q=Ha+Noi', 'Hà Nội', 180)
ON CONFLICT (id) DO NOTHING;

-- Thời trang Nữ
INSERT INTO INVENTORY (id, itemId, link, location, quantity) VALUES
(8, '15', 'http://maps.google.com/?q=Da+Lat', 'Đà Lạt', 95)
ON CONFLICT (id) DO NOTHING;

INSERT INTO INVENTORY (id, itemId, link, location, quantity) VALUES
(9, '16', 'http://maps.google.com/?q=Ho+Chi+Minh', 'TP.HCM', 175)
ON CONFLICT (id) DO NOTHING;

INSERT INTO INVENTORY (id, itemId, link, location, quantity) VALUES
(10, '17', 'http://maps.google.com/?q=Ha+Noi', 'Hà Nội', 220)
ON CONFLICT (id) DO NOTHING;

INSERT INTO INVENTORY (id, itemId, link, location, quantity) VALUES
(11, '18', 'http://maps.google.com/?q=Can+Tho', 'Cần Thơ', 410)
ON CONFLICT (id) DO NOTHING;

INSERT INTO INVENTORY (id, itemId, link, location, quantity) VALUES
(12, '19', 'http://maps.google.com/?q=Nha+Trang', 'Nha Trang', 380)
ON CONFLICT (id) DO NOTHING;

INSERT INTO INVENTORY (id, itemId, link, location, quantity) VALUES
(13, '20', 'http://maps.google.com/?q=Ho+Chi+Minh', 'TP.HCM', 450)
ON CONFLICT (id) DO NOTHING;

-- Trang sức
INSERT INTO INVENTORY (id, itemId, link, location, quantity) VALUES
(14, '5', 'http://maps.google.com/?q=Ha+Noi', 'Hà Nội', 12)
ON CONFLICT (id) DO NOTHING;

INSERT INTO INVENTORY (id, itemId, link, location, quantity) VALUES
(15, '6', 'http://maps.google.com/?q=Ho+Chi+Minh', 'TP.HCM', 28)
ON CONFLICT (id) DO NOTHING;

INSERT INTO INVENTORY (id, itemId, link, location, quantity) VALUES
(16, '7', 'http://maps.google.com/?q=Ha+Noi', 'Hà Nội', 65)
ON CONFLICT (id) DO NOTHING;

INSERT INTO INVENTORY (id, itemId, link, location, quantity) VALUES
(17, '8', 'http://maps.google.com/?q=Da+Nang', 'Đà Nẵng', 90)
ON CONFLICT (id) DO NOTHING;

-- Điện tử
INSERT INTO INVENTORY (id, itemId, link, location, quantity) VALUES
(18, '9', 'http://maps.google.com/?q=Ha+Noi', 'Hà Nội', 73)
ON CONFLICT (id) DO NOTHING;

INSERT INTO INVENTORY (id, itemId, link, location, quantity) VALUES
(19, '10', 'http://maps.google.com/?q=Ho+Chi+Minh', 'TP.HCM', 54)
ON CONFLICT (id) DO NOTHING;

INSERT INTO INVENTORY (id, itemId, link, location, quantity) VALUES
(20, '11', 'http://maps.google.com/?q=Ha+Noi', 'Hà Nội', 120)
ON CONFLICT (id) DO NOTHING;

INSERT INTO INVENTORY (id, itemId, link, location, quantity) VALUES
(21, '12', 'http://maps.google.com/?q=Ho+Chi+Minh', 'TP.HCM', 38)
ON CONFLICT (id) DO NOTHING;

INSERT INTO INVENTORY (id, itemId, link, location, quantity) VALUES
(22, '13', 'http://maps.google.com/?q=Da+Nang', 'Đà Nẵng', 25)
ON CONFLICT (id) DO NOTHING;

INSERT INTO INVENTORY (id, itemId, link, location, quantity) VALUES
(23, '14', 'http://maps.google.com/?q=Ha+Noi', 'Hà Nội', 8)
ON CONFLICT (id) DO NOTHING;

-- Phụ kiện
INSERT INTO INVENTORY (id, itemId, link, location, quantity) VALUES
(24, '165613', 'http://maps.google.com/?q=Ha+Noi', 'Hà Nội', 500)
ON CONFLICT (id) DO NOTHING;

INSERT INTO INVENTORY (id, itemId, link, location, quantity) VALUES
(25, '165614', 'http://maps.google.com/?q=Ho+Chi+Minh', 'TP.HCM', 230)
ON CONFLICT (id) DO NOTHING;

INSERT INTO INVENTORY (id, itemId, link, location, quantity) VALUES
(26, '165954', 'http://maps.google.com/?q=Ha+Noi', 'Hà Nội', 75)
ON CONFLICT (id) DO NOTHING;

INSERT INTO INVENTORY (id, itemId, link, location, quantity) VALUES
(27, '444435', 'http://maps.google.com/?q=Ho+Chi+Minh', 'TP.HCM', 300)
ON CONFLICT (id) DO NOTHING;

INSERT INTO INVENTORY (id, itemId, link, location, quantity) VALUES
(28, '444437', 'http://maps.google.com/?q=Ha+Noi', 'Hà Nội', 1200)
ON CONFLICT (id) DO NOTHING;