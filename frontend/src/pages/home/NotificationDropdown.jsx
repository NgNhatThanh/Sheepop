import React, { useState, useRef, useEffect, useContext } from "react";
import { FaBell, FaCheck } from "react-icons/fa";
import { IoMdClose } from "react-icons/io";
import { BASE_API_URL } from "../../constants";
import { fetchWithAuth } from "../../util/AuthUtil";
import { toast } from "react-toastify";
import SocketContext from "../common/WebsocketProvider";

export default function NotificationDropdown(){

  const { isConnected, subscribeToChanel } = useContext(SocketContext)

  const [isOpen, setIsOpen] = useState(false);
  const [activeScope, setActiveScope] = useState("buyer");
  const [notifications, setNotifications] = useState({
    buyer: [],
    shop: []
  });
  const limit = 7
  const [offset, setOffset] = useState({
    buyer: 0,
    shop: 0
  })
  const [loading, setLoading] = useState(false);
  const [hasMore, setHasMore] = useState({ buyer: true, shop: true });
  const [unreadCount, setUnreadCount] = useState(0);
  const dropdownRef = useRef(null);
  
  const markAsRead = (id) => {
    if(id){
      const isRead = notifications[activeScope].some(noti => noti.id === id && noti.read);
      if(isRead) return
    }

    fetchWithAuth(`${BASE_API_URL}/v1/notification/mark_as_read${id ? '?notiId=' + id : ''}`, "/", true, {
      method: 'POST'
    })
      .then(res => res.json())
      .then(res => {
        if(res.message){
          toast.error("Có lỗi")
        }
        else{
          if(!id){
            setUnreadCount(0)
            setNotifications(prev => ({
              ...prev,
              [activeScope]: prev[activeScope].map(cur => ({
                ...cur,
                read: true
              }))
            }))
          }
        }
      })
  } 
  const handleScroll = (e) => {
    const bottom = e.target.scrollHeight - e.target.scrollTop <= e.target.clientHeight + 50;
    if (bottom && !loading && hasMore[activeScope]) {
      fetchNotifications  (activeScope);
    }
  };

  const fetchNotifications = (scope) => {
    scope = scope.toLowerCase()
    setLoading(true)
    fetchWithAuth(`${BASE_API_URL}/v1/notification/get_list?scope=${scope}&offset=${offset[scope]}&limit=${limit}`)
      .then(res => res.json())
      .then(res => {
        if(res.message){
          toast.error("Có lỗi xảy ra khi tải thông báo")
        }
        else{
          setOffset(prev => ({
            ...prev,
            [scope]: res.nextOffset
          }))
          setNotifications(prev => ({
            ...prev,
            [scope]: [...(prev[scope] || []), ...res.content]
          }))
          if(res.nextOffset - offset[scope] < limit){
            setHasMore(prev => ({
              ...prev,
              [scope]: false
            }))
          }
        }
      })
      .finally(() => setLoading(false))
  }

  const countUnread = () => {
    fetchWithAuth(`${BASE_API_URL}/v1/notification/count_unread`)
      .then(res => res.json())
      .then(res => setUnreadCount(res))
  }


  useEffect(() => {
    countUnread()
    fetchNotifications("shop")
    fetchNotifications("buyer")

    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside)
      // stompClient.deactivate();
    };
  }, []);

  useEffect(() => {
    const username = JSON.parse(localStorage.getItem('userData'))['username']
    subscribeToChanel(`/user/${username}/notify`, (newNoti) => {
      toast.info("Có thông báo mới")
      const scope = newNoti.scope.toLowerCase()
      setNotifications((prev) => ({
        ...prev,
        [scope]: [newNoti, ...(prev[scope] || [])]
      }));
      setUnreadCount(prev => prev + 1)
    });
  }, [isConnected])

  return (
    <div className="relative" ref={dropdownRef}>
      <button
        className="flex cursor-pointer gap-2 relative transition-all duration-200 rounded-full hover:text-gray-300"
        onClick={() => setIsOpen(!isOpen)}
      >
        <div className="relative">
          <FaBell size={20} />
          {unreadCount > 0 && (
            <span className="absolute -top-1 -right-1 flex items-center justify-center min-w-[16px] h-4 px-1 bg-red-500 text-[10px] font-bold rounded-full">
              {unreadCount}
            </span>
          )}
        </div>
      </button>

      {isOpen && (
        <div className="absolute flex flex-col h-120 right-0 mt-2 w-80 sm:w-96 bg-white rounded-lg shadow-lg border border-gray-200 z-50 animate-slide-in">
          {/* Header */}
          <div className="flex items-center justify-between p-4 border-b border-gray-100">
            <h3 className="font-bold text-gray-800">Thông báo</h3>
            <button 
              onClick={() => setIsOpen(false)}
              className="text-gray-400 hover:text-gray-600"
            >
              <IoMdClose size={18} />
            </button>
          </div>

          {/* Tabs */}
          <div className="flex border-b border-gray-100">
            <button
              className={`cursor-pointer flex-1 py-3 font-medium text-sm ${
                activeScope === "buyer"
                  ? "text-blue-500 border-b-2 "
                  : "text-gray-500 hover:text-gray-700"
              }`}
              onClick={() => setActiveScope("buyer")}
            >
              Sheepop
            </button>
            <button
              className={`cursor-pointer flex-1 py-3 font-medium text-sm ${
                activeScope === "shop"
                  ? "text-blue-500 border-b-2"
                  : "text-gray-500 hover:text-gray-700"
              }`}
              onClick={() => setActiveScope("shop")}
            >
              <div className="flex items-center justify-center gap-1">
                <span>Cửa hàng</span>
              </div>
            </button>
          </div>

          {unreadCount > 0 && (
            <div className="flex justify-center items-center px-4 py-2 bg-gray-50">
              <button
                className="cursor-pointer text-xs hover:underline flex items-center gap-1"
                onClick={() => markAsRead()}
              >
                <FaCheck size={12} />
                <p className="text-blue-500">Đánh dấu tất cả là đã đọc</p>
              </button>
            </div>
          )}

          <div 
            className="flex-grow overflow-y-auto"
            onScroll={handleScroll}
          >
            {notifications[activeScope].length > 0 ? (
              <div>
                {notifications[activeScope].map((notification) => (
                  <div
                    key={notification.id}
                    className={`cursor-pointer p-2 flex justify-between border-b border-gray-100 hover:bg-gray-100 cursor-pointer ${
                      notification.read ?  "" : "bg-blue-50"
                    }`}
                    onClick={() => {
                      markAsRead(notification.id)
                      window.location.assign(notification.redirectUrl)
                    }}
                  >
                    <div className="flex gap-2">
                      <img
                        src={notification.thumbnailUrl}
                        alt="notification-thumbnail"
                        className="h-15 w-15 rounded-sm"
                      />
                      <div className="flex flex-col gap-2">
                        <p className={`text-sm ${notification.read ? 'text-gray-600' : 'text-gray-900 font-medium'} truncate`}>
                          {notification.itemCount > 0 ? notification.content.replace('%d', notification.itemCount) : notification.content}
                        </p>
                        <p className="text-xs text-gray-500 whitespace-nowrap">
                          {new Date(notification.createdAt).toLocaleString()}
                        </p>
                      </div>
                    </div>
                    {!notification.read && (
                      <div className="ml-2 flex-shrink-0">
                        <div className="h-2 w-2 rounded-full bg-blue-500"></div>
                      </div>
                    )}
                  </div>
                ))}
                {loading && (
                  <div className="flex justify-center p-4">
                    <div className="w-6 h-6 border-2 rounded-full animate-spin"></div>
                  </div>
                )}
                {!hasMore[activeScope] && notifications[activeScope].length > 0 && (
                  <div className="text-center p-4 text-sm text-gray-500">
                    Đã hết thông báo!
                  </div>
                )}
              </div>
            ) : (
              <div className="flex flex-col items-center justify-center py-12">
                {loading ? (
                  <div className="w-6 h-6 border-2  rounded-full animate-spin"></div>
                ) : (
                  <>
                    <FaBell size={30} className="text-gray-300 mb-2" />
                    <p className="text-gray-500">Không có thông báo!</p>
                  </>
                )}
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};