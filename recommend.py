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
# Đọc dữ liệu từ file CSV
df = pd.read_csv('C:/Users/admin/Desktop/ml-latest-small/productRatings.csv')
# print(df)
 


# In[ ]:


movie_names=pd.read_csv('C:/Users/admin/Desktop/ml-latest-small/products.csv')
movie_data = pd.merge(df,movie_names,on='movieId')
movie_data=movie_data.drop(['genres','timestamp'],axis=1)
# print(movie_data)


# In[ ]:


trend =pd.DataFrame(movie_data.groupby('title')['rating'].mean())
trend['total number of ratings']=pd.DataFrame(movie_data.groupby('title')['rating'].count())
# print(trend)


# In[2]:


plt.figure(figsize=(10,4))
ax=plt.barh(trend['rating'].round(),trend['total number of ratings'],color='g')
# plt.show()


# In[3]:


plt.figure(figsize=(10,4))
ax=plt.subplot()
ax.bar(trend.head(25).index,trend['total number of ratings'].head(25),color='g')
ax.set_xticklabels(trend.index,rotation=40,fontsize='12',horizontalalignment="right")
ax.set_title("Toltal number")
# plt.show()


# In[4]:


user_ids= df["userId"].unique().tolist()
user2user_encode = {x: i for i,x in enumerate(user_ids)}
userencode2user = {i: x for i, x in enumerate(user_ids)}
movie_ids= df["movieId"].unique().tolist()
movie2movie_encode = {x: i for i,x in enumerate(movie_ids)}
movie_encode2movie = {i: x for i, x in enumerate(movie_ids)}
df["user"] = df["userId"].map(user2user_encode)
df["movie"] = df["movieId"].map(movie2movie_encode)
num_users=len(user2user_encode)
num_movies = len(movie_encode2movie)

df["rating"] = df["rating"].values.astype(np.float32)

min_rating = min(df["rating"])
max_rating= max(df["rating"])

# print(
#     "Number of users:{}, Number movies: {}, min rate:{}, max rate: {}".format(num_users, num_movies, min_rating,max_rating)
    
# )


# In[5]:


df =df.sample(frac=1, random_state=42)
x= df[["user","movie"]].values

y= df["rating"].apply(lambda x:(x - min_rating)/(max_rating- min_rating)).values

train_indices=int(0.75* df.shape[0])
x_train,x_val,y_train,y_val=(
    x[:train_indices],
    x[train_indices:],
    y[:train_indices],
    y[train_indices:],
)


# In[6]:


EMBEDDING_SIZE = 50

class RecommenderNet(keras.Model):
    def __init__(self, num_users, num_movies, embedding_size, **kwargs):
        super(RecommenderNet, self).__init__(**kwargs)
        self.num_users = num_users
        self.num_movies = num_movies
        self.embedding_size = embedding_size
        self.user_embedding = layers.Embedding(
            num_users,
            embedding_size,
            embeddings_initializer="he_normal",
            embeddings_regularizer=keras.regularizers.l2(1e-6),
        )
        self.user_bias = layers.Embedding(num_users, 1)
        self.movie_embedding = layers.Embedding(
            num_movies,
            embedding_size,
            embeddings_initializer="he_normal",
            embeddings_regularizer=keras.regularizers.l2(1e-6),
        )
        self.movie_bias = layers.Embedding(num_movies, 1)

    def call(self, inputs):
        user_vector = self.user_embedding(inputs[:, 0])
        user_bias = self.user_bias(inputs[:, 0])
        movie_vector = self.movie_embedding(inputs[:, 1])
        movie_bias = self.movie_bias(inputs[:, 1])
        dot_user_movie = tf.tensordot(user_vector, movie_vector, 2)
        x = dot_user_movie + user_bias + movie_bias
        return tf.nn.sigmoid(x)

 


# In[7]:


model = RecommenderNet(num_users, num_movies, EMBEDDING_SIZE)
model.compile(
    keras.optimizers.Adam(learning_rate=0.001),
    loss="mean_squared_error",
    metrics=["mean_absolute_error", "mean_squared_error"]
)

history = model.fit(
    x=x_train,
    y=y_train,
    batch_size=64,
    epochs=5,
    verbose=1,
    validation_data=(x_val, y_val)
)


# In[8]:


# model.summary()
import sys
parameter = int(sys.argv[1])

user_id = parameter
movies_watched_by_user = df[df.userId==user_id]
# print(movies_watched_by_user)

# In[10]:


movie_df =pd.read_csv('C:/Users/admin/Desktop/ml-latest-small/products.csv')

movies_not_watched = movie_df[
    ~movie_df["movieId"].isin(movies_watched_by_user.movieId.values)
]["movieId"]
movies_not_watched = list(
    set(movies_not_watched).intersection(set(movie2movie_encode.keys()))
)
movies_not_watched =[[movie2movie_encode.get(x)] for x in movies_not_watched]
user_encoder= user2user_encode.get(user_id)
user_movie_array = np.hstack(
    ([[user_encoder]]* len(movies_not_watched),movies_not_watched)
) 
ratings = model.predict(user_movie_array).flatten()
top_ratings_indices = ratings.argsort()[-5:][:: -1]
recommend_movie_ids=[
    movie_encode2movie.get(movies_not_watched[x][0]) for x in top_ratings_indices
]
print("show for user:{}".format(user_id) )

print("top 5")
recommended_movies = movie_df[movie_df["movieId"].isin(recommend_movie_ids)]
result = ""
for row in recommended_movies.itertuples():
    result += f"{row.movieId}\n"

print(result)


# In[ ]:





# In[ ]:





# In[ ]:




