import random
def generate_data():
    id = i  # Tăng giá trị id theo thứ tự tăng dần
    address = 'K9'
    create_time = '2023-10-11 15:08:55.530284'
    email = f'taolaxuanloc{id}@gmail.com'  # Tăng số trước @gmail trong email
    image = None  # Giá trị None
    name = f'Trần Xuân Lộc {id}'  # Tăng số sau tên trong cột name
    password = '$2a$10$y/N7nfY2jizCTSAv/yPLvOcsV292VkPzClqjhZF0nF5TvA7NqKemG'
    phone = '0906303905'
    provider = 'Local'
    username = f'locxuan{i}'  # Tăng giá trị username theo thứ tự tăng dần
    is_otp_verified = 1
    otp = str(random.randint(100000, 999999))  # Tăng số ở otp

    # Trả về dữ liệu
    return (id, address, create_time, email, image, name, password, phone, provider, username, is_otp_verified, otp)

# Kết nối và thực thi câu lệnh INSERT INTO với cơ sở dữ liệu MySQL của bạn
# ...

# Tạo 100 người dùng
for i in range( 698,1001):
    user_data = generate_data()  # Tạo dữ liệu người dùng mới
    # Kiểm tra giá trị của image và thay thế bằng 'null' nếu là None
    image = 'null' if user_data[4] is None else f"'{user_data[4]}'"
    sql = f"INSERT INTO `nhahang`.`user` (`id`, `address`, `create_time`, `email`, `image`, `name`, `password`, `phone`, `provider`, `username`, `is_otp_verified`, `otp`) VALUES ({user_data[0]}, '{user_data[1]}', '{user_data[2]}', '{user_data[3]}', {image}, '{user_data[5]}', '{user_data[6]}', '{user_data[7]}', '{user_data[8]}', '{user_data[9]}', {user_data[10]}, '{user_data[11]}');"

    # In ra câu lệnh SQL
    print(sql)


    