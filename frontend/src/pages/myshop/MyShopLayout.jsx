import { Navigate, Outlet } from "react-router-dom"
import { useState } from 'react'
import Navbar from "./Navbar"
import Sidebar from "./Sidebar"

export default function MyShopLayout({isAuthenticated}){
    if(!isAuthenticated) return <Navigate to={`/login?from=${window.location}`}/>
    
    const [sidebarOpen, setSidebarOpen] = useState(true)

    const toggleSidebar = () => {
        setSidebarOpen(prev => !prev)
    }

    return (
        <div className="bg-gray-100">
            <Navbar/>
            <div className="flex">
                <Sidebar isOpen={sidebarOpen} toggle={toggleSidebar}/>
                <div className={`w-full min-h-screen mt-12 transition-all duration-300 p-4 ${sidebarOpen ? 'ml-58' : 'ml-20'}`}>
                    <Outlet/>
                </div>
            </div>
        </div>
    )
}