# Guest API Spec

## Register

Endpoint : `POST /api/register`

Request Body : 
```json
{
  "email": "dyas@gmail.com",
  "password": "12345678"
}
```

Response Body (Success) : Tidak ada

Response Body (Error) : 
```json
{
  "status": "BAD_REQUEST",
  "message": "Validation error",
  "details": [
    "email: Email is already used"
  ]
}
```

## Login

Endpoint : `POST /api/login`

Request Body :
```json
{
  "email": "dyas@gmail.com",
  "password": "12345678"
}
```

Response Body (Success) :
```json
{
  "token": "TOKEN"
}
```

Response Body (Error) :
```json
{
  "status": "BAD_REQUEST",
  "message": "invalid email or password",
  "details": null
}
```