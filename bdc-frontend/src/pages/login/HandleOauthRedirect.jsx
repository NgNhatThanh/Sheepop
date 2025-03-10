import { useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import {BASE_API_URL} from "../../constants/index"
import { setUserData } from '../../util/AuthUtil';

function sendApi(code){
    fetch(`${BASE_API_URL}/v1/auth/oauth2/login?provider=google&code=${code}`, {
      method: "POST",
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
            setUserData(data.token)
          })
        const from = localStorage.getItem('from')
        localStorage.removeItem('from')
        window.location.assign(from ? from : '/')
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
  console.log(code)

  useEffect(() => sendApi(code))

  return (
    <p> Redirecting... </p>
  );
}

export default HandleRedirect;