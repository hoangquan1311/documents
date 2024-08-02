1. [Cách thức hoạt động](#cách-thức-hoạt-động)

## Cách thức hoạt động

Khi người dùng đăng nhập thì tên người dùng sẽ được lưu vào localstorage ở FE để hiển thị tên khi submit tên cùng với đó được điều hướng đến trang rooms

![FE1 ](image/fe1.png)

Tại đây có thể xóa, hoặc tạo một phòng tùy ý.
![FE9 ](image/fe9.png)

BE sẽ lưu người dùng và quản lý đến khi hết phiên chat

![BE1 ](image/be1.png)

Khi người dùng gửi một tin nhắn

![FE3 ](image/fe3.png)
![FE6 ](image/fe6.png)

Tín nhắn được gửi qua rabbitmq với exchange, routing key dạng DirectExchange và message

![BE6 ](image/be2.png)
![BE10 ](image/be10.png)

Sau khi tin nhắn được nhận bắn qua websocket với destination = "/topic/messages"
![BE11 ](image/be11.png)

Hiển thị tin nhắn và các tin phía trước và real-time các tin nhắn
![FE5 ](image/fe5.png)
![FE6 ](image/fe6.png)

Khi người dùng thoát sẽ thông báo ở FE và khi đó BE sẽ setTye = "leave"
![FE7 ](image/fe7.png)
![BE5 ](image/be5.png)

Hiển thị thông báo ở FE
![FE8 ](image/fe8.png)