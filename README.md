## Stack sử dụng

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

## Cấu trúc thư mục

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

## Tài khoản test

### Admin

- Email: `admin@example.com`
- Password: `admin123`

### User

- Email: `user@example.com`
- Password: `user123`


### Thông tin test VNPAY sandbox

Thông tin dưới đây chỉ dùng cho môi trường sandbox khi kiểm thử luồng thanh toán:

- Ngân hàng: `NCB`
- Số thẻ: `9704198526191432198`
- Tên chủ thẻ: `NGUYEN VAN A`
- Ngày phát hành: `07/15`
- Mật khẩu OTP: `123456`
