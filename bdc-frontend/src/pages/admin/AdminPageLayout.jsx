import { Navigate, Outlet } from "react-router-dom"
import Navbar from './Navbar'
import Sidebar from './Sidebar'

export default function AdminPageLayout({ isAuthenticated }){
    if(!isAuthenticated) return <Navigate to={`/login?from=${window.location}`}/>

    return (
        <div className="bg-gray-100">
            <Navbar/>
            <div className="flex">
                <Sidebar/>
                <div className={`w-full min-h-screen mt-12 transition-all duration-300 p-4 ml-58`}>
                    <Outlet/>
                </div>
            </div>
        </div>
    )
}