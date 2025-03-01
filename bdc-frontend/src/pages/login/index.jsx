"use client"

import React from "react"
import { useState } from "react"
import {BASE_API_URL, GOOGLE_LOGIN_URL} from "../../constants/index.js"
import { setUserData } from "../../util/AuthUtil.js"
import { FaGoogle } from "react-icons/fa";
import { Navigate, useSearchParams, Link } from "react-router-dom"

export default function LoginPage({isAuthenticated}) {

  const [searchParams] = useSearchParams()

  const from = searchParams.get("from")

  if(isAuthenticated == true){
    return <Navigate to={from ? `/${from}` : '/'} />
  }

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

    fetch(`${BASE_API_URL}/v1/auth/login`,{
      method: "POST",
      credentials: "include",
      headers:{
          'Content-Type': 'application/json'
      },
      body: JSON.stringify(loginDto)
    })
    .then(res => {
      if(!res.ok){
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
            setUserData(data.token)
            if(data.admin) window.location.assign("/admin")
          })
          window.location.assign(from ? from : '/')
      }
    })
    .catch(err => {
      console.log(err)
      setError("Something wrong, try again later!")
    })
  }

  const openGoogleLoginPage = () => {
    window.location.href = GOOGLE_LOGIN_URL
  }

  return (
    <div className="flex items-center justify-center min-h-screen bg-[linear-gradient(120deg,#a1c4fd_0%,#c2e9fb_100%)]">
      <div className="w-full max-w-md p-8 bg-white rounded-2xl shadow-lg">
        <a href="/" className="flex justify-center mb-2">
          <img src="/logo.svg"/>
        </a>

        <h2 className="text-2xl font-bold text-center mb-6">Đăng Nhập</h2>

        <p className="text-red-500">{error}</p>

        <form className="space-y-4">
          <div>
            <label className="block text-gray-700">Tên đăng nhập</label>
            <input
              type="text"
              placeholder="Nhập username"
              className="w-full px-4 py-2 mt-1 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              onChange={e => setUsername(e.target.value.trim())}
            />
          </div>

          <div>
            <label className="block text-gray-700">Mật khẩu</label>
            <input
              type="password"
              placeholder="Nhập mật khẩu"
              className="w-full px-4 py-2 mt-1 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              onChange={e => setPassword(e.target.value.trim())}
            />
          </div>

          <button
            type="submit"
            className="w-full bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 rounded-lg transition duration-200 cursor-pointer"
            onClick={handleSubmit}
          >
            Đăng Nhập
          </button>
        </form>

        <div className="flex items-center my-4">
          <hr className="flex-1 border-gray-300" />
          <span className="px-2 text-gray-500 text-sm">hoặc</span>
          <hr className="flex-1 border-gray-300" />
        </div>

        <button
          className="w-full flex items-center justify-center bg-white border border-gray-300 hover:bg-gray-100 text-gray-700 font-semibold py-2 rounded-lg shadow-sm transition duration-200 cursor-pointer"
          onClick={openGoogleLoginPage}
        >
          <FaGoogle style={{margin: "5px"}}/>     
          Đăng nhập với Google   
        </button>

        <p className="text-center text-gray-600 mt-4">
          Chưa có tài khoản?{" "}
          <Link to={`/register${from ? `?from=${from}` : ''}`} className="text-blue-500 hover:underline">
            Đăng ký
          </Link>
        </p>
        
      </div>
    </div>
  );
}
