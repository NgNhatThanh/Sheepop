import { createContext, useState } from "react";

const ChatContext = createContext()

export function ChatProvider({ children }){

    const [expand, setExpand] = useState(false)
    const [firstExpand, setFirstExpand] = useState(false)
    const [recentRoomList, setRecentRoomList] = useState([])
    const [curChatroom, setCurChatroom] = useState(null)
    const [newMessage, setNewMessage] = useState(null)
    const [unreadRooms, setUnreadRooms] = useState([])
    const [replySignalMessage, setReplySignalMessage] = useState(null)

    return (
        <ChatContext.Provider value={{ expand, setExpand, 
                                        firstExpand, setFirstExpand,
                                        recentRoomList, setRecentRoomList, 
                                        newMessage, setNewMessage,
                                        replySignalMessage, setReplySignalMessage,
                                        curChatroom, setCurChatroom,
                                        unreadRooms, setUnreadRooms}}>
            {children}
        </ChatContext.Provider>
    )

}

export default ChatContext;