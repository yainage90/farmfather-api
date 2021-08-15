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

response
```json
{
	"id": string,
	"email": string,
	"nickName": string,
	"introduce": string,
	"profile": string,
	"favoriteCourses": array
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

response
```json
{
	"jwt": string,
	"user": {
		"id": string,
		"email": string,
		"nickName": string,
		"introduce": string,
		"profile": string,
		"favoriteCourses": array
	}
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

response
```json
{
	"id": string,
	"email": string,
	"nickName": string,
	"introduce": string,
	"profile": string,
	"favoriteCourses": array
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

response
```json
{
	"imageUrl"	
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

response
```json
{
	"id": string,
	"email": string,
	"nickName": string,
	"introduce": string,
	"profile": string,
	"favoriteCourses": array
}
```