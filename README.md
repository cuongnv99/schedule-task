# scheduled-task
spring-boot scheduled task manager with database and kafka.

## 1. Note
- Dùng `h2` profile cho `maven` build lifecycle (sẽ trigger spring profile `h2`)

  Ví dụ:
  
  `mvn clean spring-boot:run -Ph2`
  
  `mvn clean install -Ph2`

## 2. Rule
- Check out `develop` trước khi làm -> tên branch: `feature/<tên-feature>`
- Các commit bắt đầu bằng [tên developer], ví dụ `[nvcuong] làm linh tinh`
- Làm xong tạo `Merge Request` assign cho người review
- Nếu có comment thì fix và đẩy tiếp commit lên branch đã tạo, assign cho người review
