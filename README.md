# DB2API

Define an API spec to access to relational database (like mysql)

Use SpringBoot

## Examples

### Query

```bash
curl --location --request POST 'http://localhost:8081/db2api' \
--header 'Authorization: Basic YWRtaW46MTIzNA==' \
--header 'Content-Type: application/json' \
--data-raw '{
    "query": {
        "table": "test_table",
        "fields": ["id", "name"],
        "where": {
            "not": {
                "eq": [
                    {"field": "id"},
                    {"value": 5}
                ]
            }
        },
        "group":["id"],
        "having":{
            "gt":[{"field":"id"},{"value":10}]
        }
    }
}'
```

### Update & Delete

```bash
curl --location --request POST 'http://localhost:8081/db2api' \
--header 'Authorization: Basic YWRtaW46MTIzNA==' \
--header 'Content-Type: application/json' \
--data-raw '{
    "batch": [
        {
            "query": {
                "table": "test_table",
                "fields": ["id", "name"],
                "where": {
                    "eq": [{"field": "id"}, {"value": 1}]
                }
            }
        },
        {
            "update": {
                "table": "test_table",
                "set": {
                    "name": "hello"
                },
                "where": {
                    "eq": [{"field": "id"}, {"value": 1}]    
                }
            }
        },
        {
            "query": {
                "table": "test_table",
                "fields": ["id", "name"],
                "where": {
                    "eq": [{"field": "id"}, {"value": 1}]
                }
            }
        }
    ]
}'
```

### Insert & Delete

```bash
curl --location --request POST 'http://localhost:8081/db2api' \
--header 'Authorization: Basic YWRtaW46MTIzNA==' \
--header 'User-Agent: Apifox/1.0.0 (https://apifox.com)' \
--header 'Content-Type: application/json' \
--data-raw '{
    "batch": [
        {
            "delete": {
                "table": "test_table",
                "where": {
                    "in": [
                        {
                            "field": "id"
                        },
                        {
                            "values": [
                                300,
                                301
                            ]
                        }
                    ]
                }
            }
        },
        {
            "insert": {
                "table": "test_table",
                "fields": [
                    "id",
                    "name"
                ],
                "values": [
                    [
                        300,
                        "h1"
                    ],
                    [
                        301,
                        "h2"
                    ]
                ]
            }
        },
        {
            "query": {
                "table": "test_table",
                "fields": [
                    "id",
                    "name"
                ]
            }
        }
    ]
}'
```