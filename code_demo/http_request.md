# post 

## node.js

### post json

```js
router.get('/testsend', function (req, res, next) {

  //var url = 'http://120.27.249.122:3001/eventRcv';
  var url = 'http://127.0.0.1:3001/eventRcv'
  request.post(url, {
    json: {
      todo: 'Buy the milk'
    }
  }, (error, res, body) => {
    if (error) {
      console.error(error)
      return
    }
    console.log(`statusCode: ${res.statusCode}`)
    console.log(body)
  })

  res.sendStatus(200);
});
```

### post

```js
 var options = {
    url: "http://127.0.0.1:3001/testpost",
    qs: {//query
    },
    headers: {
    },//req.headers
    form: {// form-data
      face_avatar: '???',
      app_key: getAppKey(),
      sign: getSign(),
      timestamp: getTimestamp()
    }  //req.body
  };

  request.post(options, function (error, response, body) {

    console.info('response:' + JSON.stringify(response));
    //console.info("statusCode:" + response.statusCode)
    //console.info('body: ' + body);
  });
  res.send(200);
```

### post file

```js
...
```

## python

```python
#post json
import requests
a = {"mobilephone":"18611000001","pwd":"xxxxxxxxxxxx"}
url = "http://127.0.0.1:3001/eventRcv"
#消息头指定
headers = {'Content-Type': 'application/json;charset=UTF-8'}
#发送post请求 json参数直接为一个字典数据。
res = requests.request("post",url,json=a,headers=headers)
print(res.status_code)
print(res.text)

#get
uri = "/api/v2/department"
timestamp = getTimestamp()
sign = getSign(timestamp)
url = "" + uri
data = {
    "app_key": app_key,
    "sign": sign,
    "timestamp": timestamp
}
result = requests.get(url, params=data)
print(result.text)

#post file
timestamp = getTimestamp()
    sign = getSign(timestamp)
    path = " ".jpg"
    url = "" + uri
    data = {
        "remark": "",
        "mobile": "",
        "groups": "",
        "icNumber": user[3],
        "jobNumber": user[2],
        "name": user[1],
        "departmentId": 5,
        "position":  GetType(user[5]),
        "gender": user[6],
        "app_key": app_key,
        "sign": sign,
        "timestamp": timestamp
    }
    if os.path.exists(path) == True:
        files = {
            "avatarFile": (str.split(path, "/")[-1], open(path, "rb"), "image/jpeg")
        }
        result = requests.post(url, data=data, files=files)
```