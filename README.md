# movie-streaming-platform

Mini đồ án website xem phim bản quyền hiện đại, xây dựng bằng **Java 17 + Spring Boot + Spring MVC + Spring Security + Spring Data JPA + MySQL + Thymeleaf + Bootstrap 5**.

## 1) Tổng quan

Dự án mô phỏng một nền tảng streaming mini với đầy đủ các luồng:

- Đăng ký / đăng nhập / đăng xuất / quên mật khẩu cơ bản
- Phân quyền `GUEST / USER / ADMIN`
- Trang chủ streaming giao diện dark theme hiện đại
- Danh sách phim, lọc đa điều kiện, phân trang, sắp xếp
- Chi tiết phim, trailer, badge quyền xem, bình luận, rating sao
- Player HTML5 riêng, lưu tiến độ xem, continue watching
- Yêu thích, lịch sử xem, hồ sơ cá nhân, đổi mật khẩu
- Ví tiền, nạp tiền qua VNPAY, lịch sử giao dịch
- Mua gói thành viên `Free / Standard / Premium`
- Chặn xem phim `STANDARD / PREMIUM` khi user chưa đủ quyền
- Dashboard admin, quản lý phim, tập phim, user, genre, banner, plan, comment, transaction

## 2) Stack sử dụng

- Java 17
- Spring Boot 3.2.x
- Spring MVC
- Spring Security
- Spring Data JPA / Hibernate
- MySQL 8+
- Maven
- Thymeleaf
- Bootstrap 5
- HTML5 / CSS3 / JavaScript
- BCryptPasswordEncoder
- Jakarta Validation

## 3) Cấu trúc thư mục

```text
movie-streaming-platform/
├─ pom.xml
├─ README.md
├─ src/main/java/com/streaming/movieplatform/
│  ├─ MovieStreamingPlatformApplication.java
│  ├─ config/
│  ├─ controller/
│  │  ├─ admin/
│  │  ├─ auth/
│  │  ├─ movie/
│  │  └─ user/
│  ├─ dto/
│  ├─ entity/
│  ├─ enums/
│  ├─ exception/
│  ├─ repository/
│  ├─ security/
│  ├─ service/
│  ├─ service/impl/
│  ├─ util/
│  └─ validation/
└─ src/main/resources/
   ├─ application.properties
   ├─ schema.sql
   ├─ data.sql
   ├─ static/
   │  ├─ css/
   │  ├─ js/
   │  └─ uploads/
   └─ templates/
      ├─ admin/
      ├─ auth/
      ├─ fragments/
      ├─ home/
      ├─ movie/
      ├─ subscription/
      ├─ user/
      └─ wallet/
```

## 4) Cách chạy dự án

### Bước 1: Tạo database MySQL

```sql
CREATE DATABASE movie_streaming_platform CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Bước 2: Sửa thông tin kết nối DB

Mở file:

```properties
src/main/resources/application.properties
```

Sửa đúng tài khoản MySQL của bạn:

```properties
spring.datasource.username=root
spring.datasource.password=123456
```

### Bước 3: Chạy project

Chạy bằng IDE hoặc Maven:

```bash
mvn spring-boot:run
```

Sau khi khởi động thành công, truy cập:

```text
http://localhost:8080
```

## 5) Cơ chế init dữ liệu

Dự án dùng **2 lớp khởi tạo dữ liệu**:

1. `schema.sql` tạo toàn bộ bảng, khóa ngoại, index.
2. `data.sql` nạp dữ liệu domain mẫu: quốc gia, thể loại, diễn viên, đạo diễn, gói thành viên, 15 phim, tập phim, banner.
3. `DataSeeder` tạo tài khoản demo bằng **BCrypt** khi ứng dụng khởi động:
   - `admin@example.com / admin123`
   - `user@example.com / user123`

Ngoài ra `DataSeeder` còn tạo:

- ví mặc định cho user demo
- số dư demo để test mua gói
- giao dịch nạp tiền mẫu
- lịch sử subscription mẫu đã hết hạn
- favorite / comment / rating mẫu
- history mẫu để test continue watching

## 6) Tài khoản test

### Admin

- Email: `admin@example.com`
- Password: `admin123`

### User

- Email: `user@example.com`
- Password: `user123`

## 7) Luồng test chính

### A. Test đăng nhập user

1. Vào `/login`
2. Đăng nhập bằng `user@example.com / user123`
3. Kiểm tra navbar đã hiển thị menu user
4. Vào `/user/profile`, `/user/favorites`, `/user/history`, `/user/wallet`

### B. Test xem phim free

1. Đăng nhập user demo hoặc xem bằng guest
2. Vào `/movies`
3. Mở phim `Saigon Heatwave` hoặc `Whispering Tides`
4. Bấm `Xem ngay`
5. Player phải phát video bình thường

### C. Test chặn phim premium khi chưa đủ quyền

1. Đăng nhập `user@example.com / user123`
2. Vì user demo chỉ có số dư và lịch sử gói cũ đã hết hạn, access hiện tại là `FREE`
3. Vào phim `Quantum Heist`, `Aurora Station` hoặc `Code Zero`
4. Bấm `Xem ngay`
5. Màn hình player sẽ bị khóa và hiện CTA nâng cấp gói

### D. Test nạp tiền qua VNPAY

1. Cấu hình VNPAY trong `src/main/resources/application.properties`:

```properties
app.payment.vnpay.enabled=true
app.payment.vnpay.tmn-code=YOUR_TMN_CODE
app.payment.vnpay.hash-secret=YOUR_HASH_SECRET
app.payment.vnpay.return-url=https://your-domain/payment/vnpay/return
```

2. Public endpoint IPN bằng HTTPS và khai báo với VNPAY:

```text
GET /payment/vnpay/ipn
```

3. Vào `/user/wallet/deposit`
4. Nhập `100000` hoặc `300000`, hệ thống sẽ redirect sang VNPAY
5. Sau khi VNPAY gọi IPN thành công, hệ thống sẽ:
   - cập nhật `wallet_transactions` từ `PENDING` sang `SUCCESS`
   - cập nhật `payment_transactions`
   - cộng tiền vào `wallet.balance`

### E. Test mua gói premium

1. Đăng nhập `user@example.com / user123`
2. Vào `/user/wallet` kiểm tra số dư (DataSeeder đã nạp sẵn 300000)
3. Vào `/subscription/plans`
4. Mua gói `Premium`
5. Hệ thống sẽ:
   - kiểm tra đủ tiền trong ví
   - trừ số dư ví
   - tạo / cập nhật `user_subscriptions`
   - tạo `wallet_transactions` loại `SUBSCRIPTION_PURCHASE`
6. Vào `/user/subscription/current` để xem gói hiện tại và số ngày còn lại

### F. Test xem phim premium sau khi mua

1. Sau khi mua Premium, vào lại `Quantum Heist`
2. Bấm `Xem ngay`
3. Player phải mở bình thường
4. Khi pause hoặc thoát tab, hệ thống sẽ gọi `/user/history/progress` để lưu tiến độ
5. Vào `/user/history` sẽ thấy mục continue watching

### G. Test yêu thích

1. Vào chi tiết một phim bất kỳ
2. Bấm `Thêm yêu thích`
3. Vào `/user/favorites`
4. Phim phải xuất hiện trong danh sách

### H. Test bình luận và rating

1. Đăng nhập user
2. Vào trang chi tiết phim
3. Gửi bình luận
4. Gửi rating 1-5 sao
5. Refresh lại trang, rating trung bình của phim sẽ được cập nhật

### I. Test admin

1. Đăng nhập `admin@example.com / admin123`
2. Vào `/admin`
3. Kiểm tra dashboard tổng quan
4. Vào các mục:
   - `/admin/movies`
   - `/admin/users`
   - `/admin/genres`
   - `/admin/plans`
   - `/admin/banners`
   - `/admin/comments`
   - `/admin/transactions`
5. Thử thêm / sửa / xóa để xác nhận CRUD hoạt động

## 8) Upload file

Ảnh upload được lưu tại thư mục local:

```text
uploads/
```

và được map ra public URL:

```text
/uploads/**
```

## 9) Cấu hình VNPAY

Dự án đã tích hợp luồng VNPAY theo mô hình chuẩn:

- Tạo URL thanh toán tại server
- Redirect khách hàng sang VNPAY
- Nhận `Return URL` để hiển thị kết quả
- Nhận `IPN URL` để cập nhật ví và trạng thái giao dịch

Các endpoint chính:

```text
POST /user/wallet/deposit
GET  /payment/vnpay/return
GET  /payment/vnpay/ipn
```

Lưu ý khi test local: VNPAY cần gọi được `IPN URL` qua HTTPS. Bạn có thể dùng ngrok hoặc deploy tạm ra môi trường public.

## 10) Ghi chú triển khai

- Player dùng HTML5 video với URL MP4 public mẫu để dễ test.
- Dự án ưu tiên tính đồng bộ và khả năng chạy nhanh cho đồ án.
- Phần `forgot password` là phiên bản cơ bản, không dùng email token thực tế.
- Nếu cần nâng cấp sản phẩm thật, nên bổ sung:
  - email verification / reset token
  - cloud storage cho media
  - payment gateway thật
  - logging / audit / test cases
  - caching / Redis / CDN
  - streaming HLS / DRM / subtitle

## 11) Điểm nổi bật cho demo đồ án

- Có dữ liệu lớn vừa đủ để trình bày đẹp khi demo
- Có phân quyền thực sự bằng Spring Security
- Có business flow đầy đủ: **nạp tiền → mua gói → mở quyền xem premium**
- Có admin dashboard và CRUD quản trị
- UI dark theme đẹp hơn mức bài tập cơ bản
