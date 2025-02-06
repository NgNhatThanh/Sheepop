"use client"

import React, { useState } from "react"
import { BASE_API_URL } from "../../constants"

export default function RegisterPage(){

    const [fullname, setFullname] = useState("")
    const [username, setUsername] = useState("")
    const [password, setPassword] = useState("")
    const [passwordRetype, setPasswordRetype] = useState("")
    const [phoneNumber, setPhoneNumber] = useState("")
    const [email, setEmail] = useState("")
    const [dob, setDob] = useState("")
    const [error, setError] = useState("")


    const handleSubmit = async (e) => {
        e.preventDefault()
        setError("")
        if(!fullname || !username || !password || !passwordRetype || !phoneNumber || !email || !dob){
            setError("Please fill in all fields!")
            return
        }
        if(password != passwordRetype){
            setError("Passwords aren't match!")
            return
        }
        const regDto = {
            "fullName": fullname,
            "username": username,
            "password": password,
            "phoneNumber": phoneNumber,
            "email": email,
            "dob": dob
        }

        fetch(`${BASE_API_URL}/auth/register`,{
            method: "POST",
            headers:{
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(regDto),
            credentials: "include"
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

    return(
        <div>
            <p>{error}</p>
            <form style={{display: "flex", flexDirection: "column", gap: "10px" }}>
                <label>
                    Fullname
                    <input 
                        id="fullname"
                        type="text"
                        value={fullname}
                        onChange={e => setFullname(e.target.value)}
                        required
                    />
                </label>

                <label>
                    Username
                    <input 
                        id="username"
                        type="text"
                        value={username}
                        onChange={e => setUsername(e.target.value.trim())}
                        required
                    />
                </label>

                <label>
                    Phone number
                    <input 
                        id="phoneNumber"
                        type="text"
                        value={phoneNumber}
                        onChange={e => setPhoneNumber(e.target.value.trim())}
                        required
                    />
                </label>

                <label>
                    Email
                    <input 
                        id="email"
                        type="email"
                        value={email}
                        onChange={e => setEmail(e.target.value.trim())}
                        required
                    />
                </label>

                <label>
                    Birthday
                    <input 
                        id="dob"
                        type="date"
                        value={dob}
                        onChange={e => setDob(e.target.value)}
                        required
                    />
                </label>

                <label>
                    Password
                    <input 
                        id="password"
                        type="password"
                        value={password}
                        onChange={e => setPassword(e.target.value.trim())}
                        required
                    />
                </label>

                <label>
                    Retype password
                    <input 
                        id="password-retype"
                        type="password"
                        value={passwordRetype}
                        onChange={e => setPasswordRetype(e.target.value.trim())}
                        required
                    />
                </label>

                <button type="submit" onClick={handleSubmit}>
                    Register
                </button>
            </form>
            <a href="https://accounts.google.com/o/oauth2/auth?client_id=952950371733-a8t3ggkh8lmrqjavc54vd33qe7mg7ljp&redirect_uri=http://localhost:3000/redirect/auth&response_type=code&scope=email%20profile">
                <button>
                    Register with Google
                </button>
            </a>
            <a href="/login">Login</a>
        </div>
    )
}