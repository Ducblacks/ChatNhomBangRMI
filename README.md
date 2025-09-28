<h2 align="center">
    <a href="https://dainam.edu.vn/vi/khoa-cong-nghe-thong-tin">
    🎓 Faculty of Information Technology (DaiNam University)
    </a>
</h2>
<h2 align="center">
    CHAT NHÓM BẰNG GIAO THỨC RMI
</h2>
<div align="center">
    <p align="center">
        <img src="docs/aiotlab_logo.png" alt="AIoTLab Logo" width="170"/>
        <img src="docs/fitdnu_logo.png" alt="AIoTLab Logo" width="180"/>
        <img src="docs/dnu_logo.png" alt="DaiNam University Logo" width="200"/>
    </p>

[![AIoTLab](https://img.shields.io/badge/AIoTLab-green?style=for-the-badge)](https://www.facebook.com/DNUAIoTLab)
[![Faculty of Information Technology](https://img.shields.io/badge/Faculty%20of%20Information%20Technology-blue?style=for-the-badge)](https://dainam.edu.vn/vi/khoa-cong-nghe-thong-tin)
[![DaiNam University](https://img.shields.io/badge/DaiNam%20University-orange?style=for-the-badge)](https://dainam.edu.vn)

</div>

## 📖 1. Giới thiệu
Chat nhóm bằng RMI (Remote Method Invocation) là một ứng dụng cho phép nhiều người dùng trên các máy tính khác nhau kết nối và trao đổi tin nhắn theo thời gian thực thông qua mạng. RMI trong Java cho phép gọi các phương thức từ xa (remote method) giống như gọi phương thức cục bộ, nhờ đó client và server có thể giao tiếp trực tiếp qua các interface được định nghĩa sẵn.<br>

Thành phần chính:<br>
Server RMI: quản lý danh sách client, nhận và phát tán tin nhắn tới tất cả các client trong nhóm.<br>
Client RMI: kết nối đến server, gửi tin nhắn và nhận tin nhắn từ các client khác.<br>
Interface (Remote Interface): định nghĩa các phương thức từ xa (gửi tin, nhận tin, đăng ký client...).<br>

Cách hoạt động:<br>
Client đăng ký với server → server lưu lại thông tin client.<br>
Khi client gửi tin nhắn → server nhận tin → gửi lại cho tất cả client khác.<br>
Các client hiển thị tin nhắn trong giao diện (GUI/console).<br>

## 🔧 2. Ngôn ngữ lập trình sử dụng: [![Java](https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=java&logoColor=white)](https://www.java.com/)
- **Java RMI (Remote Method Invocation)** – giao tiếp Client - Server.  
- **Java Swing** – xây dựng giao diện client.  
- **Eclipse IDE / NetBeans IDE** – phát triển và chạy chương trình. 

## 🚀 3. Hình ảnh các chức năng chính

### 🔹 Giao diện server khi khởi chạy thành công
![Server UI] (docs/Images/Server_running.png)

### 🔹 Chat nhóm và danh sách online
![Client UI] (docs/Images/Clients_running.png)

## 🚀 4.  Các bước cài đặt

### Bước 1: Chuẩn bị môi trường
- Cài đặt **Java JDK 8+**.  
- Cài đặt **Eclipse IDE** hoặc **NetBeans IDE**.  
- Clone project từ GitHub về máy:  
  ```bash
          https://github.com/Ducblacks/ChatNhomBangRMI.git
### Bước 2: Khởi động Server
- Mở project trong Eclipse/NetBeans.
- Chạy file ChatServer.java để khởi động server.

### Bước 3: Khởi động Client
- Chạy file ChatClient.java (có thể mở nhiều cửa sổ client).
- Nhập tên người dùng khi chương trình yêu cầu.
- Kết nối tới server
### Bước 4: Bắt đầu chat nhóm 🎉
- Nhập tin nhắn vào ô chat và nhấn Enter hoặc Send để gửi.

- Tin nhắn sẽ được broadcast đến tất cả các client đang tham gia.

- Danh sách người dùng sẽ tự động cập nhật khi có người tham gia hoặc thoát.

## 5. Liên hệ
Nguyễn Việt Đức <br>
SDT: 0329159721 <br>
Emai: nguyenvietduc71221@gmail.com <br>
© 2025 **NguyenVietDuc**, Faculty of Information Technology, DaiNam University. All rights reserved.

---
