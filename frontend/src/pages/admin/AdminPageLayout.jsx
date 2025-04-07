import { Navigate, Outlet } from "react-router-dom"
import Navbar from './Navbar'
import Sidebar from './Sidebar'
import { fetchWithAuth } from "../../util/AuthUtil"
import { BASE_API_URL } from "../../constants"
import { ToastContainer } from "react-toastify"

export default function AdminPageLayout({ isAuthenticated }){
    if(!isAuthenticated) return <Navigate to={`/login?from=${window.location}`}/>

    fetchWithAuth(`${BASE_API_URL}/v1/admin/ping`)
        .catch(() => window.location.assign("/error?error=UNAUTHORIZED"))

    return (
        <div className="bg-gray-100">
            <Navbar/>
            <div className="flex">
                <Sidebar/>
                <div className={`w-full min-h-screen mt-12 transition-all duration-300 p-4 ml-58`}>
                    <Outlet/>
                </div>
            </div>

            <ToastContainer
                position="bottom-right"
            />
        </div>
    )
}