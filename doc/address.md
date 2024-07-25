# Address API Spec

## Get All

Endpoint : `GET /api/addresses`

Request Header :

- Authorization: Bearer {token}

Request Param :

- city : String, optional
- province : String, optional
- country : String, optional
- postal_code : String, optional
- page : Int, mandatory, default 0
- size : Int, mandatory, default 10

Response Body (Success) : 
```json
{
  "data": [
    {
      "id": 1,
      "city": "Jakarta",
      "postal_code": "12345",
      "province": "DKI Jakarta",
      "country": "Indonesia"
    }
  ],
  "size": 10,
  "current_page": 0,
  "total_page": 1
}
```

## Get By ID

Endpoint : `GET /api/addresses/{id}`

Request Header :

- Authorization: Bearer {token}

Response Body (Success) : 
```json
{
    "id": 1,
    "city": "Jakarta",
    "postal_code": "12345",
    "province": "DKI Jakarta",
    "country": "Indonesia"
}
```

## Create

Endpoint : `POST /api/addresses`

Request Header :

- Authorization: Bearer {token}

Request Body : 
```json
{
  "city": "Jakarta",
  "postal_code": "12345",
  "province": "DKI Jakarta",
  "country": "Indonesia"
}
```

Response Body (Success) : 
```json
{
    "id": 1,
    "city": "Jakarta",
    "postal_code": "12345",
    "province": "DKI Jakarta",
    "country": "Indonesia"
}
```

## Update

Endpoint : `PUT /api/addresses/{id}`

Request Header :

- Authorization: Bearer {token}

Request Body : 
```json
{
  "city": "Jakarta",
  "postal_code": "12345",
  "province": "DKI Jakarta",
  "country": "Indonesia"
}
```

Response Body (Success) : 
```json
{
    "id": 1,
    "city": "Jakarta",
    "postal_code": "12345",
    "province": "DKI Jakarta",
    "country": "Indonesia"
}
```

## Delete

Endpoint : `DELETE /api/addresses/{id}`

Request Header :

- Authorization: Bearer {token}
