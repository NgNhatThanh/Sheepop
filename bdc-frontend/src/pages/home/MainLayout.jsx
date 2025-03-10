import NavBar from "./NavBar";
import Header from "./Header";
import Footer from "./Footer";
import { Outlet } from "react-router-dom";
import React from "react";

export default function MainLayout({isAuthenticated}){
    return(
        <div className="flex flex-col min-h-screen bg-gray-100">
            <NavBar isAuthenticated={isAuthenticated}/>
            <Header isAuthenticated={isAuthenticated}/>
            <Outlet/>
            <Footer/>
        </div>
    )
}