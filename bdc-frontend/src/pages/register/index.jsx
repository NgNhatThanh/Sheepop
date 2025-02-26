"use client"

import React, { useState } from "react"
import { BASE_API_URL, GOOGLE_LOGIN_URL } from "../../constants"
import { setUserData } from "../../util/AuthUtil"
import { FaGoogle } from "react-icons/fa";
import { Navigate, useSearchParams, Link } from "react-router-dom"


export default function RegisterPage({isAuthenticated}) {

    const [searchParams] = useSearchParams()

    const from = searchParams.get('from')

    if(isAuthenticated == true){
        return <Navigate to={from ? `/${from}` : '/'} />
    }

    const [fullname, setFullname] = useState("")
    const [username, setUsername] = useState("")
    const [password, setPassword] = useState("")
    const [passwordRetype, setPasswordRetype] = useState("")
    const [phoneNumber, setPhoneNumber] = useState("")
    const [dob, setDob] = useState("")
    const [error, setError] = useState("")

    const handleSubmit = async (e) => {
        e.preventDefault()
        setError("")
        if (!fullname || !username || !password || !passwordRetype || !phoneNumber || !dob) {
            setError("Vui lòng điền đầy đủ thông tin!")
            return
        }
        if (password !== passwordRetype) {
            setError("Mật khẩu nhập lại không khớp!")
            return
        }
        const regDto = { fullName: fullname, username, password, phoneNumber, dob }

        fetch(`${BASE_API_URL}/v1/auth/register`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(regDto),
            credentials: "include"
        })
        .then(res => {
            if (res.status !== 200) {
                res.json().then(data => {
                    setError(res.status === 400 ? data.message : "Đã có lỗi xảy ra, thử lại sau!")
                })
            } else {
                res.json().then(data => {
                    localStorage.setItem("access_token", data.token)
                    setUserData(data.token)
            })
                window.location.assign(from ? from : '/')
            }
        })
        .catch(() => setError("Đã có lỗi xảy ra, thử lại sau!"))
    }

    const openGoogleLoginPage = () => {
       window.location.href = GOOGLE_LOGIN_URL
    }

    return (
        <div className="flex items-center justify-center min-h-screen bg-[linear-gradient(120deg,#a1c4fd_0%,#c2e9fb_100%)] p-10">
            <div className="w-full max-w-md p-8 bg-white rounded-2xl shadow-lg">
                <a href="/" className="flex justify-center mb-2">
                    <img src="/logo.svg"/>
                </a>

                <h2 className="text-2xl font-bold text-center mb-6">Đăng Ký</h2>

                {error && <p className="text-red-500 text-center mb-4">{error}</p>}

                <form className="space-y-4" onSubmit={handleSubmit}>
                <Input label="Họ và Tên" type="text" value={fullname} setValue={setFullname} />
                <Input label="Tên đăng nhập" type="text" value={username} setValue={setUsername} />
                <Input label="Số điện thoại" type="text" value={phoneNumber} setValue={setPhoneNumber} />
                <Input label="Ngày sinh" type="date" value={dob} setValue={setDob} />
                <Input label="Mật khẩu" type="password" value={password} setValue={setPassword} />
                <Input label="Nhập lại mật khẩu" type="password" value={passwordRetype} setValue={setPasswordRetype} />

                <button type="submit" className="w-full bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 rounded-lg transition duration-200 cursor-pointer">
                    Đăng Ký
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
                Đã có tài khoản?{" "}
                <Link to={`/login${from ? `?from=${from}` : ''}`} className="text-blue-500 hover:underline">
                    Đăng nhập
                </Link>
                </p>
            </div>
        </div>
    )
    }

    function Input({ label, type, value, setValue }) {
    return (
        <div>
            <label className="block text-gray-700">{label}</label>
            <input
                type={type}
                value={value}
                onChange={(e) => setValue(e.target.value)}
                className="w-full px-4 py-2 mt-1 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
            />
        </div>
    )
}
