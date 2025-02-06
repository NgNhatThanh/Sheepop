import { Navigate, Outlet } from "react-router-dom"
import Navbar from "./Navbar"
import Sidebar from "./Sidebar"

export default function MyShopLayout({isAuthenticated}){
    if(!isAuthenticated) return <Navigate to='/login'/>
    
    return (
        <>
            <Navbar/>
            <div style={{display: "flex", width: "100%"}}>
                <Sidebar/>
                <Outlet/>
            </div>
        </>
    )
}