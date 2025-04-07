import { useState, useEffect, useContext } from "react";
import { IoMdChatbubbles } from "react-icons/io";
import ChatContext from "./ChatProvider";
import { useSelector } from "react-redux";

import Header from "./Header";
import Sidebar from "./Sidebar";
import ChatZone from "./ChatZone";
import SocketContext from "../../common/WebsocketProvider";
import { ToastContainer } from "react-toastify";
import { fetchWithAuth } from "../../../util/AuthUtil";
import { BASE_API_URL } from "../../../constants";


export default function MiniChat(){
   
    const { isConnected, subscribeToChanel } = useContext(SocketContext)
    const chatCtx = useContext(ChatContext)
    const chatWithUserId = useSelector((state) => state.chat.userId);

    const getUnreadRooms = () => {
        fetchWithAuth(`${BASE_API_URL}/v1/chat/get_unread_rooms`)
            .then(res => res.json())
            .then(res => chatCtx.setUnreadRooms(res))
    }

    useEffect(() => {
        getUnreadRooms()
    }, [])

    useEffect(() => {   
        if(chatWithUserId){
            if(!chatCtx.expand) chatCtx.setExpand(true)
        }
    }, [chatWithUserId])

    useEffect(() => {
        const username = JSON.parse(localStorage.getItem('userData'))['username']
        subscribeToChanel(`/user/${username}/chat.queue`, (mes) => {
            chatCtx.setUnreadRooms(urs => {
                const exists = urs.some(room => room.id === mes.chatroomId);
                if (!exists) {
                    return [...urs, { id: mes.chatroomId }];
                }
                return urs; 
            });
            chatCtx.setNewMessage(mes)
        })
        subscribeToChanel(`/user/${username}/chat.reply`, (mes) => {
            chatCtx.setReplySignalMessage(mes)
        })
    }, [isConnected])

    return (
        <div className="fixed bottom-0 right-2 z-10">
            {!chatCtx.expand ? (
                <div 
                    className="relative flex items-center justify-evenly cursor-pointer text-white bg-blue-500 w-25 h-12 rounded-t-sm"
                    onClick={() => chatCtx.setExpand(true)}    
                >
                     {chatCtx.unreadRooms.length > 0 && 
                        <p 
                            className="absolute -top-2 -right-2 flex items-center justify-center 
                                    w-6 h-6 text-xs font-bold text-white bg-blue-500 rounded-full border border-white"
                        >
                            {chatCtx.unreadRooms.length}
                        </p>
                    }
                    <IoMdChatbubbles className="text-2xl"/>
                    <p className="font-semibold text-xl">Chat</p>
                </div>
            ) : (
                <div 
                    className="bg-white flex flex-col shadow-2xl rounded-sm w-160 h-145"
                >
                    <Header/>
                    <div className="flex-grow flex">
                        <Sidebar/>
                        <ChatZone/>
                    </div>
                </div>
            )}
            <ToastContainer
                position="bottom-right"
            />
        </div>
    )
}