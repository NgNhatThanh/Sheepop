import { Link } from 'react-router-dom'
import { BsShopWindow } from "react-icons/bs";
import { IoChatboxEllipses } from "react-icons/io5";

export default function ShopSection({ shop }){

    return (
        <div className="w-300 bg-white mx-auto p-4 rounded-sm flex mb-4">
            <div className="ml-2 p-2 pr-7 flex gap-5 border-r border-gray-200">
                <Link to='#'>
                    <img src={shop.avatarUrl} className="w-20 h-20 rounded-full"/>
                </Link>
                <div>
                    <p className="text-xl mb-3">{shop.name}</p>
                    <div className="flex gap-2">
                        <button className='flex gap-2 justify-center items-center p-2 border-1 border-blue-600 bg-blue-100 rounded-sm text-blue-800 font-semibold cursor-pointer hover:bg-blue-200'>
                            <IoChatboxEllipses /> Nhắn tin
                        </button>
                        <Link to='#'>
                            <button className="flex gap-2 justify-center items-center p-2 border-1 rounded-sm border-gray-400 cursor-pointer hover:bg-gray-100">
                                <BsShopWindow /> Xem cửa hàng
                            </button>
                        </Link>
                    </div>
                </div>
            </div>

            <div className='w-2/5 grid grid-cols-2 gap-x-15 ml-7 items-center'>
                    <div className='flex justify-between'>
                        <p className='text-gray-500'>Đánh giá</p>
                        <p className='text-blue-700'>{shop.totalReviews}</p>
                    </div>

                    <div className='flex justify-between'>
                        <p className='text-gray-500'>Tham gia vào</p>
                        <p className='text-blue-700'>{new Date(shop.createdAt).toLocaleDateString()}</p>
                    </div>

                    <div className='flex justify-between'>
                        <p className='text-gray-500'>Tb.Đánh giá</p>
                        <p className='text-blue-700'>{shop.averageRating.toFixed(1)}</p>   
                    </div>

                    <div className='flex justify-between'>
                        <p className='text-gray-500'>Sản phẩm</p>
                        <p className='text-blue-700'>{shop.totalProducts}</p>
                    </div>
            </div>
        </div>
    )
}