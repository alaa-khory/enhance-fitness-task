Steps:

1- Setup the DB (POSTGRES, MongoDB) from the following drive and find "DB.rar":
https://drive.google.com/drive/folders/1rjlui0RePr1ZjVPpHfsVMxLASaolQgjC?usp=drive_link
You may need to change the username and password respective to your local db credentials

2- run the rabbitmq server locally on port "15672"

3-download the API collection from the same drive URL and find "enhance-fitness.postman_collection.rar":
https://drive.google.com/drive/folders/1rjlui0RePr1ZjVPpHfsVMxLASaolQgjC?usp=drive_link

APIs are:
1- "http://localhost:8080/authenticate" POST takes the following (user credentials) and will give the jwt token:
   {
    "username": "test",
    "password": "Test@1234"
  }
  
2- "http://localhost:8080/users/registerUser" POST takes the following (user details) but doesn't take jwt and will create a user in role-based in the db:
   {
    "name": "test",
    "username": "test",
    "password": "Test@1234",
    "email": "test@example.com",
    "userType": "VENDOR" //ADMIN, USER, VENDOR
   }

3- "http://localhost:8080/users/{userId}/orders" POST takes the following (order details) takes the jwt in a role-based mechanism that will create an order in both DBs with rollback and event-based mechanism:
  {
      "order": {
          "date": "2024-10-20T14:48:00",
          "items": "Items",
          "totalAmount": 230.00
      },
      "orderMetaData": {
          "preferences": "Second hand",
          "orderNotes": "warranty",
          "specialFlags": true
      }
  }

4- "http://localhost:8080/users/{userId}/orders" GET takes headers and jwt token in role-based mechanism that will return the list of orders for a specific user
