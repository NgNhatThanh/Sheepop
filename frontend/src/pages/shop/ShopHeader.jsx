import React from 'react';
import { FaUserPlus, FaComment } from 'react-icons/fa';
import { FiShoppingBag, FiUsers, FiStar, FiClock } from 'react-icons/fi';
import { fetchWithAuth } from '../../util/AuthUtil'
import { BASE_API_URL } from '../../constants';
import { toast } from 'react-toastify';
import { useDispatch } from "react-redux";
import { setUserId } from "../../redux/chatSlice";

export default function ShopHeader({shopInfo}){

  const dispatch = useDispatch();

  const handleUpdateFollow = (follow) => {
    fetchWithAuth(`${BASE_API_URL}/v1/user/${follow ? 'follow' : 'unfollow'}?shopId=${shopInfo.id}`, window.location, true, {
      method: 'POST'
    })
      .then(res => res.json())
      .then(res => {
        if(res.message){
          toast.error(res.message)
        }
        else{
          window.location.reload()
        }
      })
      .catch(e => {
        console.log(e)
        toast.error("Có lỗi xảy ra, vui lòng thử lại sau")
      })
  }

  return (
    <div className="w-full">
      <div className="flex flex-col md:flex-row gap-6 items-start md:items-center">
        <div className="relative">
          <img
            src={shopInfo.avatarUrl}
            alt={shopInfo.name}
            className="w-24 h-24 rounded-full object-cover border-4 border-white shadow-md"
          />
          <div className="absolute -bottom-2 left-1/2 transform -translate-x-1/2 bg-blue-600 text-white text-xs px-2 py-0.5 rounded-full whitespace-nowrap">
            Shop
          </div>
        </div>
        
        <div className="flex-1">
          <div className="mb-2">
            <h1 className="text-2xl font-bold text-gray-800">{shopInfo.name}</h1>
            {/* <p className="text-sm text-gray-500">{onlineStatus}</p> */}
          </div>
          
          <div className="flex flex-wrap gap-x-8 gap-y-2">
            <div className="flex items-center gap-2 mb-2">
              <span className="inline-flex items-center text-xs md:text-sm text-gray-500 gap-1">
                <FiShoppingBag className="w-4 h-4" />
                Sản Phẩm:
              </span>
              <span className="font-medium text-gray-800">{shopInfo.productCount}</span>
            </div>
            
            <div className="flex items-center gap-2 mb-2">
              <span className="inline-flex items-center text-xs md:text-sm text-gray-500 gap-1">
                <FiUsers className="w-4 h-4" />
                Người Theo Dõi:
              </span>
              <span className="font-medium text-gray-800">{shopInfo.followerCount}</span>
            </div>
            
            <div className="flex items-center gap-2 mb-2">
              <span className="inline-flex items-center text-xs md:text-sm text-gray-500 gap-1">
                <FiStar className="w-4 h-4" />
                Đánh Giá:
              </span>
              <span className="font-medium text-gray-800">{shopInfo.averageRating.toFixed(1)} ({shopInfo.totalReviews} Đánh Giá)</span>
            </div>
            
            <div className="flex items-center gap-2 mb-2">
              <span className="inline-flex items-center text-xs md:text-sm text-gray-500 gap-1">
                <FiClock className="w-4 h-4" />
                Tham Gia:
              </span>
              <span className="font-medium text-gray-800">{new Date(shopInfo.createdAt).toLocaleDateString()}</span>
            </div>
          </div>
        </div>
        
        <div className="flex gap-2 mt-2 md:mt-0">
          <button 
            className="cursor-pointer border border-gray-300 hover:border-blue-500 hover:text-blue-500 px-5 py-1.5 rounded font-medium transition-all duration-200 flex items-center gap-1"
            onClick={() => handleUpdateFollow(shopInfo.following === false)}  
          >
            <FaUserPlus size={16} />
            <span>{shopInfo.following ? 'Hủy theo dõi' : 'Theo dõi'}</span>
          </button>
          <button 
            className="cursor-pointer bg-blue-500 hover:bg-blue-600 text-white px-5 py-1.5 rounded font-medium transition-all duration-200 shadow-sm hover:shadow flex items-center gap-1"
            onClick={() => dispatch(setUserId(shopInfo.userId))}
          >
            <FaComment size={16} />
            <span>Chat</span>
          </button>
        </div>
      </div>
    </div>
  );
};
