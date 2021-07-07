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


## 3. DESIGN DOCUMENT
      Contents
      Document Revision History	
      Task management	
      1.	Component diagram	
      2.	Database	
      3.	Class diagram	
      4.	Tasks’ features	


Task management
1.	Component diagram
  ![image](https://user-images.githubusercontent.com/30902275/124681458-c6946d00-def2-11eb-8d42-dac689b63cad.png)
2.	Class diagram
  ![image](https://user-images.githubusercontent.com/30902275/124681602-16733400-def3-11eb-8b0c-f6bedd6bef5a.png)
3.  Create a new task
  ![image](https://user-images.githubusercontent.com/30902275/124681819-900b2200-def3-11eb-8f57-e48165b21a93.png)
4.  Start a task
  ![image](https://user-images.githubusercontent.com/30902275/124681884-b6c95880-def3-11eb-923b-743ba7a348e5.png)
5.  Stop a task
  ![image](https://user-images.githubusercontent.com/30902275/124681909-c779ce80-def3-11eb-9ed8-b10380b295a4.png)

   



