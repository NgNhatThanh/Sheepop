import axios from 'axios';
import React from 'react';
import { useSearchParams } from 'react-router-dom';

function sendApi(code){
    fetch(`http://localhost:8080/api/v1/auth/oauth2?provider=google&code=${code}`)
    .then(res => res.json())
    .then(re => console.log(re))
    .catch(err => console.log(err))

    var req = {
        "username": "thanh",
        "password": "12345"
    }

    // console.log(JSON.stringify(req));
    

    // fetch('http://localhost:8080/api/v1/auth/login',{
    //     method: "POST",
    //     headers:{
    //         'Content-Type': 'application/json'
    //     },
    //     body: JSON.stringify(req)
    // })
    // .then(res => res.json())
    // .then(res => console.log(res))
}

function HandleRedirect() {
  // Sử dụng useSearchParams để lấy các query parameters
  const [searchParams] = useSearchParams();

  // Lấy giá trị của một query parameter cụ thể
  const code = searchParams.get('code'); // Tham số "code"
  console.log(`dep zai ${code}`)

  return (
    <div>
      <button onClick={() => sendApi(code)}>
        Send Api
      </button>
    </div>
  );
}

export default HandleRedirect;