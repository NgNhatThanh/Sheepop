"use client"

import { useState } from "react";
import React from "react";
import { Link } from 'react-router-dom'
import { FaBell, FaUser} from "react-icons/fa";
import { BASE_API_URL } from "../../constants";
import { fetchWithAuth } from "../../util/AuthUtil";

const Navbar = ({isAuthenticated}) => {
    const [isDropdownOpen, setIsDropdownOpen] = useState(false);

    const handleLogout = async () => {
        await fetchWithAuth(`${BASE_API_URL}/v1/auth/logout`, null, false, {
            method: "POST",
            credentials: "include",
        })
        await localStorage.removeItem("access_token")
        await localStorage.removeItem("userData")
        await localStorage.removeItem("cart")
        window.location.assign("/login")
    }

    return (
        <div className="bg-gradient-to-r from-blue-500 to-blue-700 text-white">
            <div className="container mx-auto flex justify-between items-center py-2 text-sm">
                <div className="flex space-x-4">
                    <a href="/myshop/dashboard">
                        <span>Trung tâm bán hàng</span>
                    </a>
                </div>
                {isAuthenticated ? (
                    <div className="flex space-x-3">
                        <span className="flex items-center"><FaBell className="mr-1" /> Notifications</span>
                        <div
                            className="relative"
                            onMouseEnter={() => setIsDropdownOpen(true)}
                            onMouseLeave={() => setIsDropdownOpen(false)}
                        >
                        <div className="flex gap-1 justify-center items-center">
                            <img src={JSON.parse(localStorage.getItem('userData'))['avatarUrl']} className="w-5 h-5 rounded-full"/>
                            <span className="flex items-center cursor-pointer hover:text-gray-300">{JSON.parse(localStorage.getItem('userData'))['username']}</span>
                        </div>
                        {isDropdownOpen && (
                            <div className="absolute right-0 -mt-0.5 w-40 bg-white text-black shadow-lg rounded-md z-2">
                                <Link to="/account/profile" className="block px-4 py-2 hover:bg-gray-200">Tài khoản</Link>
                                <Link to="/account/orders?type=1" className="block px-4 py-2 hover:bg-gray-200">Đơn hàng của tôi</Link>
                                <a className="block px-4 py-2 hover:bg-gray-200 cursor-pointer" onClick={handleLogout}>Đăng xuất</a>    
                            </div>
                            )}
                        </div>
                    </div>
                ) : (
                    <div className='flex space-x-2'>
                        <a href={`/login?from=${window.location}`}>
                            <span>
                                Đăng nhập
                            </span>
                        </a>
                        <a href={`/register?from=${window.location}`}>
                            <span>
                                Đăng ký
                            </span>
                        </a>
                    </div>
                )}
                
            </div>
        </div>
    );
};

export default Navbar;
