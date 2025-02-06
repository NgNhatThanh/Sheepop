"use client"

import React from "react";
import { BASE_API_URL } from "../../constants";

export default function HomePage({isAuthenticated}){
    const accessToken = localStorage.getItem("access_token")
    const handleLogout = async () => {
        await fetch(`${BASE_API_URL}/auth/logout`, {
            method: "POST",
            credentials: "include",
            headers:{
                "Authorization": `Bearer ${accessToken}`
            }
        })
        localStorage.removeItem("access_token")
        window.location.reload()
    }
    if(isAuthenticated){
        // const data = JSON.parse(userData)
        return (
            <div>
                <a href="/myshop/dashboard">
                    Your shop
                </a>
                {/* <p> {data.username} </p> */}
                <button onClick={handleLogout}>
                    Logout
                </button>
            </div>
        )
    }
    else{
        return (
            <div>
                <a href="/login">
                    <button>
                        Log in
                    </button>
                </a>
            </div>
            
        )
    }
}