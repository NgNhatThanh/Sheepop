import NavBar from "./NavBar";
import Header from "./Header";
import Footer from "./Footer";
import { Outlet } from "react-router-dom";
import React from "react";
import MiniChat from "./chat/MiniChat";
import {ChatProvider} from "./chat/ChatProvider";
import { WebsocketProvider } from "../common/WebsocketProvider";

export default function MainLayout({isAuthenticated}){
    return(
        <div className="flex flex-col min-h-screen bg-gray-100">
            <div className="sticky top-0 z-10">
                <NavBar isAuthenticated={isAuthenticated}/>
                <Header isAuthenticated={isAuthenticated}/>
            </div>
            <Outlet/>
            <Footer/>
            {isAuthenticated && (
                <WebsocketProvider>
                    <ChatProvider>
                        <MiniChat/>
                    </ChatProvider> 
                </WebsocketProvider>
            )} 
        </div>
    )
}