1. [Cách thức hoạt động](#cách-thức-hoạt-động)

## Cách thức hoạt động

Khi người dùng đăng nhập thì tên người dùng sẽ được lưu vào localstorage ở FE để hiển thị tên khi submit tên cùng với đó được điều hướng đến trang /chat

![FE1 ](image/fe1.png)
![FE2 ](image/fe2.png)

BE sẽ lưu người dùng và quản lý đến khi hết phiên chat

![BE1 ](image/be1.png)

Khi người dùng gửi một tin nhắn

![FE3 ](image/fe3.png)
![FE4 ](image/fe4.png)

Tín nhắn được gửi qua redis với key = "xin chao" và value = "message"

![BE2 ](image/be2.png)
![BE3 ](image/be3.png)

Sau khi tin nhắn được nhận bắn qua websocket với destination = "/topic/messages"
![BE4 ](image/be3.png)

Hiển thị tin nhắn và các tin phía trước và real-time các tin nhắn
![FE5 ](image/fe5.png)
![FE6 ](image/fe6.png)

Khi người dùng thoát sẽ thông báo ở FE và khi đó BE sẽ setTye = "leave"
![FE7 ](image/fe7.png)
![BE5 ](image/be5.png)

Hiển thị thông báo ở FE
![FE8 ](image/fe8.png)