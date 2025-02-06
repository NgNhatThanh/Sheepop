"use client"

import React from "react"
import { useState } from "react"
import {BASE_API_URL} from "../../constants/index.js"

export default function LoginPage(){
  
  const [username, setUsername] = useState("")
  const [password, setPassword] = useState("")
  const [error, setError] = useState("")

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError("")
    if (!username || !password) {
      setError("Please fill in all fields")
      return
    }

    const loginDto = {
      "username": username,
      "password": password
    }

    fetch(`${BASE_API_URL}/auth/login`,{
      method: "POST",
      credentials: "include",
      headers:{
          'Content-Type': 'application/json'
      },
      body: JSON.stringify(loginDto)
    })
    .then(res => {
      if(res.status != 200){
        const status = res.status
        res.json()
          .then(data => {
            if(status == 400) setError(data.message)
            else setError("Something wrong, try again later!")
          })
      }
      else{
        res.json()
          .then(data => {
            localStorage.setItem('access_token', data.token)
            const payload = JSON.parse(atob(data.token.split('.')[1]))
            const userData = {
              "username": payload.sub
            }
            localStorage.setItem('userData', JSON.stringify(userData))
          })
        window.location.assign('/')
      }
    })
    .catch(err => {
      setError("Something wrong, try again later!")
    })
  }


  return (
    <div>
      {error && <p>{error}</p>}
      <form>
        <label id="username">
          Username
          <input
            id="username"
            type="text"
            value={username}
            onChange={e => setUsername(e.target.value.trim())}
            required
          />
        </label>

        <label id="password">
          Password
          <input
            id="password"
            type="password"
            value={password}
            onChange={e => setPassword(e.target.value.trim())}
            required
          />
        </label>

        <button type="submit" onClick={handleSubmit}>
          Login
        </button>
      </form>

      <a href="https://accounts.google.com/o/oauth2/auth?client_id=952950371733-a8t3ggkh8lmrqjavc54vd33qe7mg7ljp&redirect_uri=http://localhost:3000/redirect/auth&response_type=code&scope=email%20profile">
        <button>
          Log in with Google
        </button>
      </a>

      <a href="/register">
        Register
      </a>
    </div>
  )
}