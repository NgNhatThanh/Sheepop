import { useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import {BASE_API_URL} from "../../constants/index.js"

function sendApi(code){
    fetch(`${BASE_API_URL}/auth/oauth2/login?provider=google&code=${code}`, {
      credentials: "include"
    })
    .then(res => {
      if(res.status != 200){
        alert("Something wrong, please try again!")
        window.location.assign('/login')
      }
      else{
        res.json()
          .then(data => {
            localStorage.setItem("access_token", data.token)
            const payload = JSON.parse(atob(data.token.split('.')[1]))
            const userData = {
              "username": payload.sub
            }
            localStorage.setItem('userData', JSON.stringify(userData))
          })
        window.location.assign('/')
      }
    }
    )
    .catch(err => {
      alert("Something wrong, please try again!")
      window.location.assign('/login')
    })
}

function HandleRedirect() {
  const [searchParams] = useSearchParams();
  const code = searchParams.get('code'); // Tham số "code"

  useEffect(() => sendApi(code))

  return (
    <p> Redirecting... </p>
  );
}

export default HandleRedirect;