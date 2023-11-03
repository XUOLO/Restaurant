#!/usr/bin/env python
# coding: utf-8

# In[15]:


import numpy as np
import pandas as pd
import scipy.stats
import tensorflow as tf
from tensorflow import keras
from tensorflow.keras import layers
from pathlib import Path
import matplotlib.pyplot as plt
import seaborn as sns
df = pd.read_csv('C:/Users/admin/Downloads/ratings.csv')
  


 

product_names=pd.read_csv('C:/Users/admin/Downloads/products.csv')
product_data = pd.merge(df,product_names,on='productId')
product_data=product_data.drop(['genres'],axis=1)
 

 

trend =pd.DataFrame(product_data.groupby('title')['rating'].mean())
trend['total number of ratings']=pd.DataFrame(product_data.groupby('title')['rating'].count())
 

 

plt.figure(figsize=(10,4))
ax=plt.barh(trend['rating'].round(),trend['total number of ratings'],color='g')
 

 

plt.figure(figsize=(10,4))
ax=plt.subplot()
ax.bar(trend.head(25).index,trend['total number of ratings'].head(25),color='g')
ax.set_xticklabels(trend.index,rotation=40,fontsize='12',horizontalalignment="right")
ax.set_title("Toltal number")
 

 

user_ids= df["userId"].unique().tolist()
user2user_encode = {x: i for i,x in enumerate(user_ids)}
userencode2user = {i: x for i, x in enumerate(user_ids)}
product_ids= df["productId"].unique().tolist()
product2product_encode = {x: i for i,x in enumerate(product_ids)}
product_encode2product = {i: x for i, x in enumerate(product_ids)}
df["user"] = df["userId"].map(user2user_encode)
df["product"] = df["productId"].map(product2product_encode)
num_users=len(user2user_encode)
num_products = len(product_encode2product)

df["rating"] = df["rating"].values.astype(np.float32)

min_rating = min(df["rating"])
max_rating= max(df["rating"])

 


 


df =df.sample(frac=1, random_state=42)
x= df[["user","product"]].values

y= df["rating"].apply(lambda x:(x - min_rating)/(max_rating- min_rating)).values

train_indices=int(0.75* df.shape[0])
x_train,x_val,y_train,y_val=(
    x[:train_indices],
    x[train_indices:],
    y[:train_indices],
    y[train_indices:],
)


 


EMBEDDING_SIZE = 50

class RecommenderNet(keras.Model):
    def __init__(self, num_users, num_products, embedding_size, **kwargs):
        super(RecommenderNet, self).__init__(**kwargs)
        self.num_users = num_users
        self.num_products = num_products
        self.embedding_size = embedding_size
        self.user_embedding = layers.Embedding(
            num_users,
            embedding_size,
            embeddings_initializer="he_normal",
            embeddings_regularizer=keras.regularizers.l2(1e-6),
        )
        self.user_bias = layers.Embedding(num_users, 1)
        self.product_embedding = layers.Embedding(
            num_products,
            embedding_size,
            embeddings_initializer="he_normal",
            embeddings_regularizer=keras.regularizers.l2(1e-6),
        )
        self.product_bias = layers.Embedding(num_products, 1)

    def call(self, inputs):
        user_vector = self.user_embedding(inputs[:, 0])
        user_bias = self.user_bias(inputs[:, 0])
        product_vector = self.product_embedding(inputs[:, 1])
        product_bias = self.product_bias(inputs[:, 1])
        dot_user_product = tf.tensordot(user_vector, product_vector, 2)
        x = dot_user_product + user_bias + product_bias
        return tf.nn.sigmoid(x)

 


 


model = RecommenderNet(num_users, num_products, EMBEDDING_SIZE)
model.compile(
    keras.optimizers.Adam(learning_rate=0.0001),
    loss="mean_squared_error",
    metrics=["mean_absolute_error", "mean_squared_error"]
)

 





history = model.fit(x_train, y_train, batch_size=128, epochs=5, verbose=1, validation_data=(x_val, y_val))

train_loss = history.history['loss']
val_loss = history.history['val_loss']

train_acc = history.history['mean_absolute_error']
val_acc = history.history['val_mean_absolute_error']
 

final_train_loss = train_loss[-1]
final_val_loss = val_loss[-1]
final_train_acc = train_acc[-1]
final_val_acc = val_acc[-1]
print(f'Final Training Loss: {final_train_loss}')
print(f'Final Validation Loss: {final_val_loss}')
print(f'Final Training MAE: {final_train_acc}')
print(f'Final Validation MAE: {final_val_acc}')




# Tạo một danh sách chứa tất cả các ID người dùng
user_ids = df["userId"].unique().tolist()

# Tạo một DataFrame để lưu kết quả
result_df = pd.DataFrame(columns=["userId", "recommended_product_ids"])

# Lặp qua từng người dùng và ghi kết quả vào DataFrame
for user_id in user_ids:
    products_watched_by_user = df[df.userId == user_id]
    product_df = pd.read_csv('C:/Users/admin/Downloads/products.csv')

    products_not_watched = product_df[
        ~product_df["productId"].isin(products_watched_by_user.productId.values)
    ]["productId"]
    products_not_watched = list(
        set(products_not_watched).intersection(set(product2product_encode.keys()))
    )
    products_not_watched = [[product2product_encode.get(x)] for x in products_not_watched]
    user_encoder = user2user_encode.get(user_id)
    user_product_array = np.hstack(
        ([[user_encoder]] * len(products_not_watched), products_not_watched)
    )
    ratings = model.predict(user_product_array).flatten()
    top_ratings_indices = ratings.argsort()[-5:][::-1]
    recommend_product_ids = [
        product_encode2product.get(products_not_watched[x][0]) for x in top_ratings_indices
    ]

    # Thêm kết quả vào DataFrame
    for product_id in recommend_product_ids:
        result_df = result_df._append({"userId": user_id, "recommended_product_ids": product_id}, ignore_index=True)

# # Lưu DataFrame vào tệp CSV
result_df.to_csv('C:/Users/admin/Downloads/recommendation.csv', index=False)

# In thông báo khi hoàn thành
print("Recommendations for all users saved to recommendations.csv")
