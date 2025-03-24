"use client"

import { useState } from "react";
import React from "react";
import { Link } from 'react-router-dom'
import { logout } from "../../util/AuthUtil";
import NotificationDropdown from "./NotificationDropdown";
import { WebsocketProvider } from "../common/WebsocketProvider";

const Navbar = ({isAuthenticated}) => {
    const [isOptionsDropdownOpen, setIsOptionsDropdownOpen] = useState(false);

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
                        <WebsocketProvider>
                            <NotificationDropdown/>
                        </WebsocketProvider>
                        
                        <div
                            className="relative"
                            onMouseEnter={() => setIsOptionsDropdownOpen(true)}
                            onMouseLeave={() => setIsOptionsDropdownOpen(false)}
                        >
                            <div className="flex gap-1 justify-center items-center">
                                <img src={JSON.parse(localStorage.getItem('userData'))['avatarUrl']} className="w-5 h-5 rounded-full"/>
                                <span className="flex items-center cursor-pointer hover:text-gray-300">{JSON.parse(localStorage.getItem('userData'))['username']}</span>
                            </div>
                            {isOptionsDropdownOpen && (
                                <div className="absolute right-0 -mt-0.5 w-40 bg-white text-black shadow-lg rounded-md z-2">
                                    <Link to="/account/profile" className="block px-4 py-2 hover:bg-gray-200">Tài khoản</Link>
                                    <Link to="/account/orders?type=1" className="block px-4 py-2 hover:bg-gray-200">Đơn hàng của tôi</Link>
                                    <a className="block px-4 py-2 hover:bg-gray-200 cursor-pointer" onClick={() => logout()}>Đăng xuất</a>    
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
