#!/bin/sh

set -v

ORDER_ID=$(curl -s -X POST http://localhost:8080/orders/ -d '{"riderId":"63770803-38f4-4594-aec2-4c74918f7165","price":"123.45","route":[{"address":"Київ, вулиця Полярна, 17А","lat":50.51980052414157,"lng":30.467197278948536},{"address":"Київ, вулиця Новокостянтинівська, 18В","lat":50.48509161169076,"lon":30.485170724431292}]}' -H 'Content-Type: application/json' | jq -r .orderId)
sleep 1s

curl -s -X GET http://localhost:8080/orders/$ORDER_ID | jq
echo

curl -s -X PATCH http://localhost:8080/orders/$ORDER_ID -d '{"status":"ACCEPTED","driverId":"2c068a1a-9263-433f-a70b-067d51b98378","version":1}' -H 'Content-Type: application/json'
sleep 1s

curl -s -X GET http://localhost:8080/orders/$ORDER_ID | jq
echo

curl -s -X PATCH http://localhost:8080/orders/$ORDER_ID -d '{"status":"COMPLETED","version":2}' -H 'Content-Type: application/json'
sleep 1s

curl -s -X GET http://localhost:8080/orders/$ORDER_ID | jq
echo

curl -s -X PATCH http://localhost:8080/orders/$ORDER_ID -d '{"status":"CANCELLED","version":3}' -H 'Content-Type: application/json'
sleep 1s

curl -s -X GET http://localhost:8080/orders/$ORDER_ID | jq
echo

curl -s -X GET http://localhost:8080/orders/$ORDER_ID/events | jq
echo