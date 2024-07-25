# Contact API Spec

## Create

Endpoint : `POST /api/contacts`

Request Header :
- Authorization : Bearer TOKEN

Request Body : 
```json
{
  "name": "Dyas Irvan",
  "phone": "08123456789",
  "email": "dyass@yopmail.com",
  "street": "Jln jalan",
  "addressId": 1
}
```

## Get All

Endpoint : `GET /api/contacts`

Request Header :
- Authorization : Bearer TOKEN

Request Param :
- name : String, optional
- phone : String, optional
- email : String, optional
- street : String, optional
- page : Int, mandatory, default 0
- size : Int, mandatory, default 10

Response Body (Success) : 
```json
{
    "data": [
        {
            "id": 16,
            "name": "dyass",
            "phone": "082345851333",
            "email": "dyas@yopmail.com",
            "street": "jalan",
            "address": {
                "id": 82,
                "city": "jakarta",
                "province": "jakarta",
                "country": "indonesia",
                "postalCode": "123456"
            },
            "user": {
                "id": 72,
                "email": "dyas@yopmail.com"
            }
        }
    ],
    "currentPage": 0,
    "totalPage": 1,
    "size": 10
}
```

## Get By ID

Endpoint : `GET /api/contacts/{id}`

Request Header :
- Authorization : Bearer TOKEN

Response Body (Success) : 
```json
{
  "id": 16,
  "name": "dyass",
  "phone": "082345851333",
  "email": "dyas@yopmail.com",
  "street": "jalan",
  "address": {
    "id": 82,
    "city": "jakarta",
    "province": "jakarta",
    "country": "indonesia",
    "postalCode": "123456"
  },
  "user": {
    "id": 72,
    "email": "dyas@yopmail.com"
  }
}
```

## Update

Endpoint : `PATCH /api/contacts/{id}`

Request Header :
- Authorization : Bearer TOKEN


Request Body :
```json
{
  "name": "Dyas Irvan",
  "phone": "08123456789",
  "email": "dyass@yopmail.com",
  "street": "Jln jalan",
  "addressId": 1
}
```

Response Body (Success) : 
```json
{
  "id": 16,
  "name": "dyass",
  "phone": "082345851333",
  "email": "dyas@yopmail.com",
  "street": "jalan",
  "address": {
    "id": 82,
    "city": "jakarta",
    "province": "jakarta",
    "country": "indonesia",
    "postalCode": "123456"
  },
  "user": {
    "id": 72,
    "email": "dyas@yopmail.com"
  }
}
```

## Delete

Endpoint : `DELETE /api/contacts/{id}`

Request Header :
- Authorization : Bearer TOKEN