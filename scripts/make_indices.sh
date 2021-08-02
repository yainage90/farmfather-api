#!/usr/bin/bash

#course
curl -XPUT 'http://ec2-13-209-181-246.ap-northeast-2.compute.amazonaws.com:9200/course?pretty' \
-u "${ES_USER}:${ES_PASSWORD}" \
-H 'Content-Type: application/json' \
-d \
'{
	"settings": {
		"number_of_shards": 3,
    "number_of_replicas": 1
	},
	"mappings": {
		"properties": {
			"id": {
				"type": "keyword"
			},
			"title": {
				"type": "text"
			},
			"subTitle": {
				"type": "text"
			},
			"mentorId": {
				"type": "keyword"
			},
			"status": {
				"type": "keyword"
			},
			"numRating": {
				"type": "integer"
			},
			"starAvg": {
				"type": "integer"
			},
			"price": {
				"type": "integer"
			},
			"register": {
				"type": "integer"
			},
			"thumbnail": {
				"type": "keyword"
			},
			"detail": {
				"type": "text"
			},
			"created": {
				"type": "date",
				"format": "yyyy-MM-dd HH:mm:ss"
			},
			"updated": {
				"type": "date",
				"format": "yyyy-MM-dd HH:mm:ss"
			},
			"chapters": {
				"type": "nested",
				"properties": {
					"id": {
						"type": "keyword"
					},
					"title": {
						"type": "keyword"
					},
					"lectures": {
						"type": "nested",
						"properties": {
							"id": {
								"type": "keyword"
							},
							"title": {
								"type": "keyword"
							}
						}
					}
				}
			}
		}
	}
}'

#user
curl -XPUT 'http://ec2-13-209-181-246.ap-northeast-2.compute.amazonaws.com:9200/user?pretty' \
-u "${ES_USER}:${ES_PASSWORD}" \
-H 'Content-Type: application/json' \
-d \
'{
	"settings": {
		"number_of_shards": 3,
    "number_of_replicas": 1
	},
	"mappings": {
		"properties": {
			"id": {
				"type": "keyword"
			},
			"email": {
				"type": "keyword"
			},
			"nickName": {
				"type": "keyword"
			},
			"password": {
				"type": "keyword"
			},
			"introduce": {
				"type": "text"
			},
			"image": {
				"type": "keyword"
			},
			"favoriteCourses": {
				"type": "nested",
				"properties": {
					"id": {
						"type": "keyword"
					}
				}
			}
		}
	}
}'

