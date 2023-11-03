import random

# Danh sách các cặp (user_id, product_id) đã rating
rated_pairs = set()
# Danh sách các cặp (user_id, rating_value) đã rating
rated_user_ratings = set()

def generate_data(id_counter):
    # Tạo dữ liệu ngẫu nhiên
    rating_value = random.choice([1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5])
    product_id = random.randint(1, 22)
    
   
    role = 2
    
 
    
    # Tăng giá trị id_counter
    id_counter += 1
    
    # Trả về dữ liệu
    return (id_counter, role )

# Tạo dữ liệu người dùng
id_counter = 547
for user_id in range(850,1001):
    # Tạo ngẫu nhiên số lượng product_id mà user_id sẽ đánh giá (từ 1 đến 5)
    num_ratings = random.randint(1, 5)
    
    for _ in range(num_ratings):
        user_data = generate_data(id_counter)  # Tạo dữ liệu người dùng mới
        id_counter = user_data[0]  # Cập nhật giá trị id_counter
        sql = f"INSERT INTO `nhahang`.`user_role` (`user_id`, `role_id`) VALUES ({user_data[0]}, {user_data[1]} );"
 
        # Thực thi câu lệnh SQL
        # ...
        print(sql)