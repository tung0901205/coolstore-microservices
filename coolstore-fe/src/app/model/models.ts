// ============================================================
// PRODUCT MODEL
// ============================================================
export interface Product {
  itemId: string;
  title: string;
  desc: string;
  price: number;
  quantity: number;
  image: string;
  category: string;
}

// ============================================================
// CART MODEL
// ============================================================
export interface Cart {
  items: CartItem[];
}

export interface CartItem {
  itemId: string;
  name: string;
  price: number;
  quantity: number;
  product: string; // filename ảnh
  image?: string;
}

// ============================================================
// USER / AUTH MODEL
// ============================================================
export interface NguoiDung {
  id: number;
  username: string;
  email: string;
  fullName: string;
  role: 'USER' | 'ADMIN';
  active: boolean;
  createdAt: string;
}

export interface DangNhapResponse {
  token: string;
  loaiToken: string;
  thoiHanGiay: number;
  nguoiDung: NguoiDung;
}

export interface KetQua<T> {
  thanhCong: boolean;
  thongBao: string;
  duLieu: T;
}

// ============================================================
// ORDER MODEL
// ============================================================
export interface DonHang {
  id: string;
  userId: number;
  tenNguoiDat: string;
  email: string;
  diaChiGiaoHang: string;
  soDienThoai: string;
  tongTien: number;
  trangThaiThanhToan: string;
  trangThaiThanhToanText: string;
  trangThaiDonHang: string;
  trangThaiDonHangText: string;
  phuongThucThanhToan: string;
  maGiaoDichVnpay?: string;
  ngayTao: string;
  ngayThanhToan?: string;
  ghiChu?: string;
  cacMatHang: MatHangDonHang[];
}

export interface MatHangDonHang {
  id: number;
  itemId: string;
  tenSanPham: string;
  hinhAnh: string;
  donGia: number;
  soLuong: number;
  thanhTien: number;
}

// ============================================================
// INVENTORY MODEL
// ============================================================
export interface TonKho {
  id: number;
  itemId: string;
  location: string;
  quantity: number;
  link: string;
}
