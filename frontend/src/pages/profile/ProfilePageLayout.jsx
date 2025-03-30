import { Outlet } from "react-router-dom";
import ProfileSidebar from "./SideBar";

export default function ProfilePageLayout(){

    return (
        <div className="flex min-h-screen bg-gray-100 max-w-6xl mx-auto p-4 gap-6">
            <ProfileSidebar/>
            <Outlet/>
        </div>
    )

}