# Sheepop

## 1. Introduction
An E-Commerce website that follow C2C (Customer-to-Customer) model, where a buyer can alse be a seller.

### Getting started with Docker compose

1. Get the latest source code
2. **[Optional]** Fill information to [.env_sample](https://github.com/NgNhatThanh/Sheepop/blob/main/backend/src/main/resources/.env_sample) file, and change it's name to `.env` (Base on feature you want, uploading feature is important if you want to create products)
3. Open terminal at the root folder of the project, run `docker compose up -d`, and wait for some minutes
4. Access the website via `http://localhost:5173`, you already have an admin account `admin/admin`

## 2. Key features

- **Basic authentication:** login, register, forgot password, change password
- **Order stuffs** 
- **Online banking**
- **For seller:** 
  - Manage shop's products, orders
- **Realtime chat and notifications**

## 3. Technologies

**BackEnd**

- Spring boot
- Websocket
- Maven

**FrontEnd**

- ReactJS
- TailwindCSS

**Database**
- MongoDB
- Redis
- ElasticSearch

**Authentication**
- JWT
- Oauth2 with Google

**Storage**
- Cloudinary for storing images

**External Service**
- VNPay for online banking
- GHN for calculating shipping fee



## 4. Screenshots
  Home page
  ![Home page](images/1.png)
  Product detail page
  ![Product page](images/2.png)
  User's orders
  ![User's orders](images/3.png)
  Chat
  ![Chat](images/4.png)
  Seller's page
  ![Shop's page](images/5.png)
  Admins' page
  ![Admin's page](images/6.png)

## 5. Diagrams

System architecture
![System architecture](images/system_architecture.png)

## 6. Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.
