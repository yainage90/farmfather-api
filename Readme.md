## 1. 엔드포인트(endpoint)

### 1) auth(인증)

#### a. 회원가입

```javascript
POST /api/register
```

body
```json
{
	"email": string,
	"password": string,
	"nickName": string
}
```

#### b. 로그인

```javascript
POST /api/authenticate
```

body
```json
{
	"email": string,
	"password": string
}
```

#### c. 닉네임 변경

```javascript
POST /api/nickName
```

headers
```json
{
	"jwt": string
}
```

body
```json
{
	"nickName": string
}
```

#### d. 프로필 사진 변경

```javascript
POST /api/user/profile
```

headers
```json
{
	"jwt": string
}
```

body(formdata)
```formdata
{
	"profileImage": multipart-file	
}
```

#### e. 회원 조회

```javascript
GET /api/user/<유저ID>
```

headers
```json
{
	"jwt": string
}
```

<br>

### 2) course(수업)

#### a. 수업 정보 조회

```javascript
GET /api/course/<수업ID>
```

headers
```json
{
	"jwt": string
}
```

#### b. 전체 수업 목록 조회

```javascript
GET /api/course/all
```

headers
```json
{
	"jwt": string
}
```

#### c. 내가 만든 수업 목록 조회

```javascript
GET /api/course/my
```

headers
```json
{
	"jwt": string
}
```

#### d. 생성

```javascript
POST /api/course
```

headers
```json
{
	"jwt": string
}
```

body
```json
{
	"title": string
}
```

#### e. 수정

```javascript
PUT /api/course/update
```

headers
```json
{
	"jwt": string
}
```

body
```json
{
	"id": string,
	"title": string,
	"learns": array<string>,
	"status": string(ready/pending),
	"price": integer,
	"detail": string,
	"chapters": [
		{
			"id": string,
			"title": string,
			"lectures": [
				{
					"id": string,
					"title": string,
					"videoId": string
				},
				...
			]
		},
		...
	]
}
```

#### f. 썸네일 변경

```javascript
POST /api/course/<수업ID>/thumbnail
```

headers
```json
{
	"jwt": string
}
```

body(formdata)
```
thumbnailImage - multipartfile
```

#### g. 수업 삭제

```javascript
DELETE /api/course/<수업ID>
```

headers
```json
{
	"jwt": string
}
```

#### h. 리뷰 저장

```javascript
PUT /api/course/<수업ID>/rating/save
```

headers
```json
{
	"jwt": string
}
```

body
```json
{
	"star": integer,
	"comment": string
}
```

#### i. 리뷰 삭제

```javascript
DELETE /api/course/<수업ID>/rating/<리뷰ID>
```

headers
```json
{
	"jwt": string
}
```

#### h. 질문 저장

```javascript
PUT /api/course/<수업ID>/qna/save
```

headers
```json
{
	"jwt": string
}
```

body
```json
{
	"star": integer,
	"comment": string
}
```

#### i. 질문 삭제

```javascript
DELETE /api/course/<수업ID>/qna/<질문ID>
```

headers
```json
{
	"jwt": string
}
```

body
```json
{
	"title": string,
	"question": string
}
```
