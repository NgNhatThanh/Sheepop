import { useState, useEffect, useContext } from "react"
import { fetchWithAuth } from "../../../util/AuthUtil"
import { BASE_API_URL } from "../../../constants"
import ChatContext from "./ChatProvider"
import { GoDotFill } from "react-icons/go";
import { useDispatch, useSelector } from "react-redux";
import { setUserId } from "../../../redux/chatSlice";

export default function Sidebar(){

    const dispatch = useDispatch()
    const { newMessage } = useContext(ChatContext)
    const chatCtx = useContext(ChatContext)
    const chatWithUserId = useSelector(state => state.chat.userId)
    const roomLimit = 10
    const [roomOffset, setRoomOffset] = useState(0)

    const fetchRecentChatroom = (reset = false) => {
        fetchWithAuth(`${BASE_API_URL}/v1/chat/get_chatroom_list?offset=${roomOffset}&limit=${roomLimit}`, window.location.pathname, true)
            .then(res => res.json())
            .then(res => {
                if(res.message){

                }   
                else{
                    if(reset) chatCtx.setRecentRoomList(res.content)
                    else chatCtx.setRecentRoomList(prev => [...prev, ...res.content])
                    setRoomOffset(res.nextOffset)
                }
            })
    }

    const processNewMsg = async () => {
        var existedRoom = false
        await chatCtx.setRecentRoomList(prev => {
            const updatedRooms = prev.map(r => {
                if (r.id === newMessage.chatroomId) {
                    chatCtx.setUnreadRooms(urs => {
                        const exists = urs.some(room => room.id === newMessage.chatroomId);
                        if (!exists) {
                            return [...urs, r];
                        }
                        return urs; 
                    });
                    console.log("Sdfds")
                    existedRoom = true;
                    return {
                        ...r,
                        lastMessage: newMessage,
                        read: chatCtx.curChatroom && chatCtx.curChatroom.id === r.id ? r.read : false,
                    };
                }
                return r;
            });
    
            return updatedRooms;
        })

        if (!existedRoom) {
            fetchRecentChatroom(true)
        }
    }

    useEffect(() => {
        if(!newMessage || !chatCtx.firstExpand) return
        processNewMsg()
    }, [newMessage])

    useEffect(() => {
        if(!chatCtx.firstExpand) {
            chatCtx.setFirstExpand(true)
            fetchRecentChatroom();
        }

        
    }, [])

    return (
        <div className="h-full w-60 border-r border-gray-300 shadow-sm">
            {chatCtx.recentRoomList.length > 0 ? (
                <div className="flex flex-col w-full">
                    {chatCtx.recentRoomList.map(room => (
                        <div 
                            key={room.id}
                            className={`cursor-pointer border-b border-gray-300 flex items-center h-15 
                                ${chatWithUserId === room.receiver.id ? 'bg-gray-200' : 'hover:bg-gray-100'}`}
                            onClick={() => {
                                chatCtx.setUnreadRooms(prev => prev.filter(r => r.id !== room.id))
                                chatCtx.setRecentRoomList(prev => prev.map(r => {
                                    if(r.id === room.id) r.read = true
                                    return r
                                }))
                                dispatch(setUserId(room.receiver.id))
                            }}
                        >
                            <div className="p-2">
                                <img 
                                    src={room.receiver.thumbnailUrl}
                                    className="rounded-full w-12 border-1 border-gray-300"
                                />
                            </div>
                            <div className="p-2 pl-0 flex-grow flex flex-col justify-between">
                                <div className="flex items-center justify-between">
                                    <p className="text-sm font-semibold line-clamp-1 overflow-hidden">{room.receiver.shopName}</p>
                                    {!room.read && <GoDotFill className="text-blue-500"/>}
                                </div>
                                {room.lastMessage && (
                                    <div className="flex items-center justify-between">
                                        <p className="text-sm max-w-1/2 line-clamp-1 overflow-hidden">
                                            {room.lastMessage.type === 'TEXT' ? room.lastMessage.content : 'Ảnh'}
                                        </p>
                                        <p className="text-xs text-gray-500">{new Date(room.lastMessage.createdAt).toLocaleDateString()}</p>
                                    </div>
                                )}
                            </div>
                        </div>
                    ))}
                </div>
            ): (
                <div className="p-2">
                    Không có tin nhắn
                </div>
            )}
        </div>
    )
}