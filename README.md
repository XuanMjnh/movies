# movie-streaming-platform

Mini đồ án website xem phim bản quyền, xây dựng bằng **Java 17 + Spring Boot + Spring MVC + Spring Security + Spring Data JPA + MySQL + Thymeleaf + Bootstrap 5**.

## 1) Tổng quan

Dự án mô phỏng một nền tảng streaming mini với các luồng chính:

- Đăng ký, đăng nhập, đăng xuất, quên mật khẩu cơ bản
- Phân quyền `GUEST / USER / ADMIN`
- Danh sách phim, lọc, sắp xếp, phân trang
- Trang chi tiết phim, rating, bình luận, lịch sử xem
- Ví người dùng, nạp tiền qua VNPAY, lịch sử giao dịch
- Mua gói thành viên `Free / Standard / Premium`
- Chặn nội dung `STANDARD / PREMIUM` khi user chưa đủ quyền
- Dashboard admin và các màn CRUD quản trị

## 2) Stack sử dụng

- Java 17
- Spring Boot 3.2.5
- Spring MVC
- Spring Security
- Spring Data JPA / Hibernate 6
- MySQL 8+
- Thymeleaf
- Bootstrap 5
- Maven
- spring-dotenv
- Local file uploads (`/uploads/**`)

## 3) Cấu trúc thư mục

```text
movie-streaming-platform/
├─ pom.xml
├─ README.md
├─ .env                        # local only, không commit
├─ uploads/                    # local uploaded files
└─ src/
   └─ main/
      ├─ java/com/streaming/movieplatform/
      │  ├─ MovieStreamingPlatformApplication.java
      │  ├─ config/
      │  ├─ controller/
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
      └─ resources/
         ├─ application.properties
         ├─ static/
         │  ├─ css/
         │  ├─ images/
         │  ├─ js/
         │  └─ uploads/        # local static path
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

## 4) Cấu hình môi trường

Ứng dụng đọc biến môi trường từ file `.env` thông qua:

```properties
spring.config.import=optional:file:.env[.properties]
```

File `.env` cần được đặt ở thư mục gốc project. Spring Boot sẽ tự nạp file này khi chạy bằng IDE hoặc Maven nếu `working directory` trỏ đúng về thư mục gốc.

### Mẫu `.env` cho MySQL local

```dotenv
DB_URL=jdbc:mysql://localhost:3306/movie_streaming_platform?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Bangkok&characterEncoding=UTF-8
DB_USERNAME=root
DB_PASSWORD=123456
DB_DRIVER=com.mysql.cj.jdbc.Driver

VNPAY_ENABLED=false
VNPAY_TMN_CODE=
VNPAY_HASH_SECRET=
VNPAY_PAY_URL=https://sandbox.vnpayment.vn/paymentv2/vpcpay.html
VNPAY_RETURN_URL=http://localhost:8080/payment/vnpay/return
VNPAY_LOCALE=vn
VNPAY_ORDER_TYPE=other
VNPAY_EXPIRE_MINUTES=15
```

### Mẫu `.env` cho MySQL managed service như Aiven

```dotenv
DB_URL=jdbc:mysql://your-host:28077/defaultdb?sslMode=REQUIRED&serverTimezone=Asia/Bangkok&characterEncoding=UTF-8
DB_USERNAME=your-username
DB_PASSWORD=your-password
DB_DRIVER=com.mysql.cj.jdbc.Driver

```

File `.env` đã được ignore trong `.gitignore`.

## 5) Lưu ý quan trọng về database

Hiện tại project đang để:

```properties
spring.jpa.hibernate.ddl-auto=none
spring.sql.init.mode=never
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```

Nghĩa là ứng dụng **không tự tạo bảng** và **không tự chạy SQL init** lúc khởi động.

Repo hiện tại **không còn** `schema.sql` và `data.sql`, vì vậy database cần được chuẩn bị sẵn trước khi khởi động ứng dụng:

- Đã có schema/tables phù hợp với các entity
- Có dữ liệu nền để kiểm thử đầy đủ luồng phim, gói thành viên và dashboard

Nếu database trống, ứng dụng có thể kết nối thành công nhưng sẽ lỗi khi bắt đầu truy vấn dữ liệu.

## 6) Chạy dự án

### Yêu cầu

- Java 17
- Maven 3.9+ hoặc chạy trực tiếp bằng IDE
- MySQL 8+ hoặc managed MySQL compatible

### Các bước

1. Tạo và chuẩn bị database trước.
2. Tạo file `.env` ở thư mục gốc project.
3. Chạy project bằng IDE hoặc Maven:

```bash
mvn spring-boot:run
```

Sau khi khởi động thành công, ứng dụng mặc định phục vụ tại:

```text
http://localhost:8080
```

## 7) Dữ liệu seed hiện có

`DataSeeder` được kích hoạt khi startup. Thành phần này **không tạo schema**, nhưng sẽ seed một số dữ liệu demo nếu các bảng đã tồn tại.

Những gì `DataSeeder` đang làm:

- Tạo role `ROLE_GUEST`, `ROLE_USER`, `ROLE_ADMIN` nếu chưa tồn tại
- Tạo tài khoản:
  - `admin@example.com / admin123`
  - `user@example.com / user123`
- Tạo ví cho user demo
- Nạp số dư demo `300000` cho user demo khi cần
- Tạo một giao dịch nạp tiền mẫu cho user demo
- Nếu database đã có `SubscriptionPlan` tên `Standard`, sẽ tạo một subscription hết hạn để test flow nâng cấp
- Nếu database đã có phim và tập phim, sẽ tạo:
  - favorite mẫu
  - lịch sử xem mẫu
  - bình luận mẫu
  - rating mẫu

## 8) Tài khoản test

### Admin

- Email: `admin@example.com`
- Password: `admin123`

### User

- Email: `user@example.com`
- Password: `user123`

## 9) Luồng test chính

### A. Đăng nhập user

1. Truy cập `/login`
2. Đăng nhập bằng `user@example.com / user123`
3. Kiểm tra các màn:
   - `/user/profile`
   - `/user/favorites`
   - `/user/history`
   - `/user/wallet`

### B. Nạp tiền qua VNPAY

1. Cấu hình đầy đủ các biến `VNPAY_*` trong `.env`
2. Public endpoint IPN qua HTTPS
3. Truy cập `/user/wallet/deposit`
4. Tạo giao dịch nạp tiền và chuyển sang cổng VNPAY
5. Kiểm tra `Return URL` và `IPN URL`

Các endpoint chính:

```text
POST /user/wallet/deposit
GET  /payment/vnpay/return
GET  /payment/vnpay/ipn
```

### C. Mua gói thành viên

1. Đăng nhập `user@example.com / user123`
2. Truy cập `/subscription/plans`
3. Mua gói `Standard` hoặc `Premium`
4. Kiểm tra:
   - số dư ví bị trừ
   - lịch sử giao dịch được tạo
   - trạng thái gói hiện tại tại `/user/subscription/current`

### D. Quản trị

1. Đăng nhập `admin@example.com / admin123`
2. Truy cập `/admin`
3. Kiểm tra dashboard và các màn CRUD:
   - `/admin/movies`
   - `/admin/users`
   - `/admin/genres`
   - `/admin/plans`
   - `/admin/banners`
   - `/admin/comments`
   - `/admin/transactions`
   - `/admin/vouchers`

## 10) Upload file

Ảnh `banner` và `avatar` mới được upload trực tiếp vào thư mục local `uploads/`.

Backend nhận file `MultipartFile`, lưu file vào thư mục con như `uploads/banners` hoặc `uploads/avatars`, rồi lưu đường dẫn `/uploads/**` vào database. Vì vậy:

- Dữ liệu mới trong DB sẽ là đường dẫn local dạng `/uploads/**`
- Ảnh được phục vụ trực tiếp từ thư mục local qua `WebMvcConfig`
- Không cần cấu hình dịch vụ upload ảnh cloud

Biến cấu hình liên quan:

```properties
app.upload-dir=uploads
```

## 11) Cấu hình VNPAY

Cấu hình trong `.env`:

```dotenv
VNPAY_ENABLED=true
VNPAY_TMN_CODE=YOUR_TMN_CODE
VNPAY_HASH_SECRET=YOUR_HASH_SECRET
VNPAY_PAY_URL=https://sandbox.vnpayment.vn/paymentv2/vpcpay.html
VNPAY_RETURN_URL=https://your-domain/payment/vnpay/return
VNPAY_LOCALE=vn
VNPAY_ORDER_TYPE=other
VNPAY_EXPIRE_MINUTES=15
```

Lưu ý:

- `Return URL` dùng để hiển thị kết quả thanh toán
- Ví chỉ được cộng tiền sau khi hệ thống nhận `IPN` hợp lệ
- Khi kiểm thử trên máy local, `IPN URL` cần được public qua HTTPS, ví dụ bằng ngrok

### Thông tin test VNPAY sandbox

Thông tin dưới đây chỉ dùng cho môi trường sandbox khi kiểm thử luồng thanh toán:

- Ngân hàng: `NCB`
- Số thẻ: `9704198526191432198`
- Tên chủ thẻ: `NGUYEN VAN A`
- Ngày phát hành: `07/15`
- Mật khẩu OTP: `123456`

## 12) Ghi chú triển khai

- Repo hiện chưa có Maven Wrapper, vì vậy môi trường chạy bằng terminal cần có Maven cài sẵn
- Nếu IntelliJ không đọc `.env`, `Working directory` của Run Configuration cần trỏ về thư mục gốc project
- Với Aiven hoặc dịch vụ MySQL managed khác, tham số `sslMode=REQUIRED` cần được giữ nguyên
- Để demo đầy đủ dữ liệu phim, gói thành viên và banner, database cần được import bộ dữ liệu nền trước
