import { FaStar, FaRegStar, FaStarHalfAlt } from "react-icons/fa";
import { BASE_API_URL } from "../../constants";
import { useEffect, useState } from "react";
import { Link } from 'react-router-dom'
import { AiOutlineLike } from "react-icons/ai";

export default function ReviewSection({ product }){

    const [loading, setLoading] = useState(false)
    const [reviews, setReviews] = useState(null)
    const [summary, setSummary] = useState(null)
    const [page, setPage] = useState(0)
    const [currentFilter, setCurrentFilter] = useState(0)
    const [showMedia, setShowMedia] = useState({})
    const limit = 7;

    const fetchReview = (rating, filterType) => {
        setLoading(true)
        fetch(`${BASE_API_URL}/v1/review/get_review_list
            ?productId=${product.id}
                &rating=${rating ? rating : 0}
                &filterType=${filterType ? filterType : 0}
                &page=${page}&limit=${limit}`.replace(/\s+/g, ''))
            .then(res => res.json())
            .then(res => {
                setSummary(res.summary)
                setReviews(res.reviews)
                setLoading(false)
            })
            .catch(err => console.log(err))
    }

    useEffect(() => {
        fetchReview(0, 0)
    }, [])

    return (
        <div className="w-300 bg-white rounded-sm mx-auto mb-4 p-6">
            <h2 className="text-xl font-semibold mb-3">
                ĐÁNH GIÁ SẢN PHẨM
            </h2>
            <div className="flex p-4 bg-blue-50 rounded-xs border-1 border-blue-300">
                <div className="mr-10">
                    <h1 className="text-blue-700 text-3xl font-bold text-center mb-2">{product.averageRating.toFixed(1)} / 5</h1>
                    <div className="flex">
                        {[1, 2, 3, 4, 5].map((index) => {
                            if (index <= product.averageRating) {
                                return <FaStar key={index} className="text-blue-700 text-2xl" />;
                            } else if (index - 0.5 <= product.averageRating) {
                                return <FaStarHalfAlt key={index} className="text-blue-700 text-2xl" />;
                            } else {
                                return <FaRegStar key={index} className="text-gray-400 text-2xl" />;
                            }
                        })}
                    </div>
                </div>

                <div className="w-full flex gap-2 items-center">
                    <button 
                        className={`w-1/8 p-1 bg-white cursor-pointer ${currentFilter === 0 ? 'border-2 border-cyan-600 text-cyan-700' : 'border-1 border-blue-300'} rounded-xs hover:bg-gray-50`}
                        onClick={() => {
                            setCurrentFilter(0)
                            fetchReview(0, 0)
                        }}
                    >
                        Tất cả
                    </button>

                    {[5, 4, 3, 2, 1].map((rating, index) => (
                        <button 
                            key={rating}
                            className={`min-w-26 p-1 bg-white cursor-pointer ${currentFilter === index + 1 ? 'border-2 border-cyan-600 text-cyan-700' : 'border-1 border-blue-300'} rounded-xs hover:bg-gray-50`}
                            onClick={() => {
                                setCurrentFilter(index + 1)
                                fetchReview(rating, null)
                            }}
                        >
                            {rating} sao{summary && ` (${summary.countRatings[rating - 1]})`}
                        </button>
                    ))}

                    <button 
                        className={`min-w-1/8 p-1 bg-white cursor-pointer ${currentFilter === 6 ? 'border-2 border-cyan-600 text-cyan-700' : 'border-1 border-blue-300'} rounded-xs hover:bg-gray-50`}
                        onClick={() => {
                            setCurrentFilter(6)
                            fetchReview(null, 1)                        
                        }}
                    >
                        Có bình luận{summary && ` (${summary.countWithContent})`}
                    </button>

                    <button 
                        className={`min-w-1/8 p-1 bg-white cursor-pointer ${currentFilter === 7 ? 'border-2 border-cyan-600 text-cyan-700' : 'border-1 border-blue-300'} rounded-xs hover:bg-gray-50`}
                        onClick={() => {
                            setCurrentFilter(7)
                            fetchReview(null, 2)
                        }}
                    >
                        Có ảnh/video{summary && ` (${summary.countWithMedia})`}
                    </button>
                </div>
            </div>

            <div className={`${loading && 'bg-gray-100'}`}>
                {reviews && (reviews.length > 0 ? (<>{reviews.map((review, index) => (
                    <div key={index} className="w-full flex gap-4 border-b border-gray-200 p-5">
                        <Link to="#">
                            <img src={review.reviewer.avatarUrl} className="w-10 h-10 rounded-full"/>
                        </Link>
                        <div className="flex flex-col gap-2">
                            <Link to="#">
                                <p className="text-sm">{review.reviewer.username}</p>
                            </Link>
                            <div className="flex">
                                {[1, 2, 3, 4, 5].map((index) => {
                                    if (index <= review.rating) {
                                        return <FaStar key={index} className="text-blue-700 text-sm" />;
                                    } else {
                                        return <FaRegStar key={index} className="text-gray-400 text-sm" />;
                                    }
                                })}
                            </div>
                            <div className="flex items-center">
                                <p className="text-xs text-gray-500 ">{new Date(review.createdAt).toLocaleString()}</p>
                                {review.item.attributes.length > 0 && <p className="ml-1 mr-1 text-gray-500">|</p>}
                                <p className="flex items-center">{review.item.attributes.map((attr, index) => <span key={index} className="text-xs text-gray-500">{attr.value}{index !== review.item.attributes.length - 1 && ', '}</span>)}</p>
                            </div>
                            <p>{review.content}</p>
                            <div className="flex gap-3">
                                {review.mediaList.map((media, mediaIdx) => (
                                    <img 
                                        key={mediaIdx}
                                        src={media.url} 
                                        className={`w-18 h-18 object-fill cursor-zoom-in ${showMedia[index] === mediaIdx && 'p-1 border-2 border-blue-700'}`}
                                        onClick={() => setShowMedia({
                                            ...showMedia,
                                            [index]: showMedia[index] === mediaIdx ? null : mediaIdx
                                        })}    
                                    />
                                ))}
                            </div>
                            {showMedia[index] !== null && showMedia[index] !== undefined && (
                                <img src={reviews[index].mediaList[showMedia[index]].url} className="w-100 h-100"/>
                            )}

                            <div className="flex items-center">
                                <AiOutlineLike className="cursor-pointer"/>
                                {review.reactCount > 0 && <p>{review.reactCount}</p>}
                            </div>
                        </div>
                        <div className="flex flex-col justify-end ml-auto">
                            <button className="text-gray-400 text-sm cursor-pointer">Báo cáo</button>
                        </div>
                    </div>
                ))}</>) : (
                    <div className="w-full h-100 flex flex-col items-center justify-center">
                        <img src="https://res.cloudinary.com/daxt0vwoc/image/upload/v1740336932/7d900d4dc402db5304b2_ik1dc7.png"/>
                        <p>Không có đánh giá nào</p>
                    </div>
                ))}
            </div>
        </div>
    )
}