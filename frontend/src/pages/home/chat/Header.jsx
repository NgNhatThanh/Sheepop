import { BsArrowDownSquare } from "react-icons/bs";
import { useContext } from "react";
import ChatContext from "./ChatProvider";

export default function Header(){

    const chatCtx = useContext(ChatContext)
    
    return (
        <div className="flex items-center justify-between h-10 border-b border-gray-300 shadow-sm">
            <div className="flex items-center gap-2">
                <p className="font-semibold text-xl ml-4 text-blue-500">
                    Chat
                </p>
                <span className="text-blue-500">
                    {chatCtx.unreadRooms.length > 0 ? `(${chatCtx.unreadRooms.length})` : ''}
                </span>
            </div>
            <button
                className="cursor-pointer mr-4"
                title="Ẩn cửa sổ chat"
                onClick={() => {
                    chatCtx.setExpand(false)
                }}
            >
                <BsArrowDownSquare />
            </button>
        </div>
    )
}