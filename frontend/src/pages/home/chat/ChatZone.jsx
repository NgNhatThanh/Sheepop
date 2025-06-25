import { useSelector } from "react-redux";
import { useEffect, useContext, useState } from "react";
import { FaImage } from "react-icons/fa6";
import { IoMdSend } from "react-icons/io";
import { useDispatch } from "react-redux";
import { setUserId } from "../../../redux/chatSlice";
import ChatContext from "./ChatProvider";
import { fetchWithAuth } from '../../../util/AuthUtil'
import { uploadImage } from "../../../util/UploadUtil";
import { BASE_API_URL } from "../../../constants";
import SocketContext from "../../common/WebsocketProvider";
import { toast } from "react-toastify";
import { FaTimes } from "react-icons/fa";

export default function ChatZone(){
    
    const dispatch = useDispatch()

    const msgLimit = 15
    const [msgOffset, setMsgOffset] = useState(0)

    const [modalImg, setModalImg] = useState(null)
    
    const { stompClient } = useContext(SocketContext)
    const { newMessage, replySignalMessage, setExpand, curChatroom, setCurChatroom } = useContext(ChatContext)
    const chatWithUserId = useSelector((state) => state.chat.userId);
    const [imageUrls, setImageUrls] = useState([])
    const [textContent, setTextContent] = useState("")
    const [messages, setMessages] = useState([])    
    const [loading, setLoading] = useState(false)
    const [hasMoreMsg, setHasMoreMsg] = useState(true)
    const curUsername = JSON.parse(localStorage.getItem('userData'))['username']

    const getChatroom = (userId) => {
        fetchWithAuth(`${BASE_API_URL}/v1/chat/get_chatroom?userId=${userId}`, window.location.pathname, true)
            .then(res => res.json())
            .then(res => {
                if(res.message){

                }
                else{
                    setCurChatroom(res)
                }
            })
    }

    const getMessages = (chatroomId) => {
        setLoading(true)
        fetchWithAuth(`${BASE_API_URL}/v1/chat/get_messages?chatroomId=${chatroomId}&offset=${msgOffset}&limit=${msgLimit}`)
            .then(res => res.json())
            .then(res => {
                if(res.message){
                    toast.error(res.message)
                }
                else{
                    setMessages(prev => [...(prev || []), ...res.content])
                    if(res.nextOffset - msgOffset < msgLimit) setHasMoreMsg(false)
                    setMsgOffset(res.nextOffset)
                }
            })
            .finally(() => setLoading(false))
    }

    const handleScroll = (e) => {
        // clientHeight: chieu cao thuc te cua div
        // scrollHeight: chieu cao can scroll
        // scrollTop: vi tri hien tai (< 0)
        const top = Math.abs(e.target.scrollTop - e.target.clientHeight) >= e.target.scrollHeight - 50;
        if (top && !loading && hasMoreMsg) {
            getMessages(curChatroom.id);
        }
    };

    const handleUploadImg = async (images) => {
        for(var img of images){
            const url = await uploadImage(img)
            console.log(url)
            if(url) setImageUrls(prev => [...prev, url])
        }
    }

    useEffect(() => {
        if(!curChatroom) return
        getMessages(curChatroom.id)
    }, [curChatroom])

    useEffect(() => {
        if(!newMessage) return
        if(curChatroom === null || newMessage.chatroomId !== curChatroom.id) return
        setMessages(prev => [newMessage, ...(prev || [])])
    }, [newMessage])

    useEffect(() => {
        if(!replySignalMessage) return
        setMessages(prev => prev.map(msg => {
            if(msg.status && msg.status === 'sending') msg.status = 'sent'
            return msg
        }))
    }, [replySignalMessage])

    useEffect(() => {
        if (chatWithUserId !== null) {
            setExpand(true)
            setTextContent("")
            setImageUrls([])
            setHasMoreMsg(true)
            setMsgOffset(0)
            setMessages([])
            getChatroom(chatWithUserId)
        }
    }, [chatWithUserId]);

    useEffect(() => {
        return () => dispatch(setUserId(null))
    }, [])

    const sendMessage = () => {
        var msgs = []
        if(imageUrls.length > 0){
            msgs = imageUrls.map(url => {
                return {
                    content: url,
                    chatroomId: curChatroom.id,
                    type: 'MEDIA'
                }
            })
        }
        if(textContent) msgs.push({
            content: textContent,
            chatroomId: curChatroom.id,
            type: 'TEXT'
        })
        
        const displayMsgs = msgs.map(msg => {
            return {
                ...msg,
                status: 'sending',
                senderUsername: curUsername,
                createdAt: new Date()
            }
        })
        setMessages(prev => [...displayMsgs, ...(prev || [])])
        msgs.forEach(msg => {
            stompClient.send(`/app/send_message`, {}, JSON.stringify(msg))
        })
        setTextContent("")
        setImageUrls([])
        console.log("send msg")
    }

    return (
        <div className="flex-grow flex flex-col h-full bg-gray-200">
            {curChatroom === null ? (
                <div>

                </div>
            ): (
                <div className="flex flex-col">
                    <div className="h-10 bg-white flex items-center justify-between">
                        <div className="p-2 flex gap-2 items-center">
                            <img
                                src={curChatroom.receiver.thumbnailUrl}
                                className="w-8 border-1 border-gray-300 rounded-full"
                            />
                            <div className="max-w-60">
                                <p
                                    className="line-clamp-1 overflow-hidden"
                                >
                                    {curChatroom.receiver.shopName}
                                </p>
                            </div>
                        </div>

                        <div className="p-2 text-sm text-gray-500 cursor-pointer hover:text-gray-700">
                            <a
                                target="_blank"
                                href={`/shop/${curChatroom.receiver.username}`}
                            >
                                Xem shop
                            </a>
                        </div>
                    </div>

                <div 
                    className="h-90 mt-auto flex flex-col-reverse overflow-y-auto space-y-2 space-y-reverse p-4 [&::-webkit-scrollbar]:w-1
                        [&::-webkit-scrollbar-track]:rounded-full
                        [&::-webkit-scrollbar-track]:bg-gray-100
                        [&::-webkit-scrollbar-thumb]:rounded-full
                        [&::-webkit-scrollbar-thumb]:bg-gray-400"
                    onScroll={handleScroll}
                >
                    {messages.length > 0 &&
                        messages.map((msg, index) => {
                            var isNewDay = false
                            var currentDate
                            if(index < messages.length - 1){
                                const prevMsg = messages[index + 1]; 
                                currentDate = new Date(msg.createdAt).toLocaleDateString();
                                const prevDate = prevMsg ? new Date(prevMsg.createdAt).toLocaleDateString() : null;
                                isNewDay = currentDate !== prevDate; 
                            }
                            else if(index === messages.length - 1){
                                isNewDay = true
                                currentDate = new Date(msg.createdAt).toLocaleDateString();
                            }
                            return (
                            <div key={msg.id}>

                                {isNewDay && (
                                    <div className="text-center text-xs text-gray-500 my-2">
                                        {currentDate}
                                    </div>
                                )}
                                <div
                                    key={index}
                                    className={`flex ${msg.senderUsername === curUsername ? "justify-end" : "justify-start"}`}
                                >
                                    <div
                                        className={`max-w-xs rounded-sm shadow-md ${
                                            msg.senderUsername === curUsername
                                                ? "bg-blue-500 text-white"
                                                : "bg-white text-gray-900"
                                        }`}
                                        title={new Date(msg.createdAt).toLocaleString()}
                                    >
                                        {msg.type === "TEXT" ? (
                                            <p className="px-3 py-2 text-sm">{msg.content}</p>
                                        ) : msg.type === "MEDIA" && msg.content ? (
                                            <img
                                                src={msg.content}
                                                alt="Media content"
                                                className="cursor-pointer p-1 w-45 rounded-sm"
                                                onClick={() => setModalImg(msg.content)}
                                            />
                                        ) : null}
                                    </div>
                                </div>
                                {msg.status && (<p className="text-right text-xs text-gray-400">{msg.status === 'sending' ? 'Đang gửi' : 'Đã gửi'}</p>)}
                            </div>
                        )})}
                </div>


                <div className="mb-0 h-35 bg-white flex flex-col">
                    {imageUrls.length > 0 && (
                        <div className="flex h-12 p-1 gap-2 overflow-x-auto w-full">
                            {imageUrls.map((url) => (
                                <div key={url} className="relative">
                                    <img
                                        src={url}
                                        className="h-10 w-10 rounded-sm"
                                    />
                                    <button
                                        onClick={() => {
                                            setImageUrls(prev => prev.filter(u => u !== url)) 
                                        }}
                                        className="cursor-pointer absolute top-0 right-0 bg-black/50 text-white text-xs rounded-full w-4 h-4 flex items-center justify-center hover:bg-black"
                                    >
                                        ×
                                    </button>
                                    
                                </div>
                            ))}
                        </div>
                    )}
                    <div className="p-2 mt-auto">
                        <textarea
                            value={textContent}
                            placeholder="Nhập tin nhắn"
                            className="w-full h-7 resize-none overflow-y-auto"
                            onChange={e => setTextContent(e.target.value)} 
                            onKeyDown={e => {
                                if(e.key === 'Enter' ) {
                                    e.preventDefault()
                                    if(textContent || imageUrls.length > 0) sendMessage()
                                }
                            }}
                            rows={1}
                        />
                    </div>
                    <div className="flex justify-between mt-auto p-3 pt-0 mt-0">
                        <div>
                            <input 
                                className="hidden"
                                type="file"
                                multiple
                                accept="image/*"
                                id="uploadImage"
                                onChange={e => handleUploadImg(e.target.files)}
                            />
                            <label 
                                htmlFor="uploadImage"
                                className="cursor-pointer"
                            >
                                <FaImage />
                            </label>
                        </div>
                        

                        <div
                            className={`${textContent || imageUrls.length > 0 ? 'cursor-pointer text-blue-700': 'cursor-not-allowed text-gray-400'}`}
                            onClick={(textContent || imageUrls.length > 0) ? (() => sendMessage()) : (() => {})}
                        >
                            <IoMdSend />
                        </div>
                    </div>
                </div>

                {modalImg && (
                    <div className="fixed inset-0 bg-black/50 backdrop-blur-md bg-opacity-75 flex justify-center items-center z-20">
                        <div className="relative">
                            <button
                                className="cursor-pointer fixed top-2 right-2 text-white bg-gray-700 p-2 rounded-full hover:bg-gray-600"
                                onClick={() => setModalImg(null)}
                            >
                                <FaTimes size={24} />
                            </button>
                            <img
                                src={modalImg}
                                alt="Enlarged"
                                className="max-w-[100vw] max-h-[80vh] object-contain rounded-lg shadow-lg"
                            />
                        </div>
                    </div>
                )}
                </div>
            )}
        </div>
    )

}