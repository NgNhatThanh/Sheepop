import { useEffect, useState } from "react"
import { FaChevronRight, FaChevronLeft, FaTimes } from "react-icons/fa";

export default function ProductMedia({mediaList}){

    const [selectedMedia, setselectedMedia] = useState(null);
    const [startIndex, setStartIndex] = useState(0);
    const [isModalOpen, setIsModalOpen] = useState(false); 
    const visibleThumbnails = 5; // Số lượng ảnh nhỏ hiển thị

    useEffect(() => {
        setselectedMedia(mediaList[0])
    }, [])

    const handlePrev = () => {
        setStartIndex((prev) => Math.max(0, prev - 1));
    };

    const handleNext = () => {
        setStartIndex((prev) => Math.min(mediaList.length - visibleThumbnails, prev + 1));
    };

    return (
        <div className="product-media w-5/7  p-4">
            <div className="mb-4">
                {selectedMedia ? (
                    <img src={selectedMedia.url} alt="Selected" className="w-full h-120 object-contain rounded-lg shadow-lg" />
                ) : (
                    <p className="text-gray-500 text-center">No image available</p>
                )}
            </div>

            <div className="relative flex items-center">
                <button
                    onClick={handlePrev}
                    className="absolute left-0 p-2 bg-gray-300 rounded-full shadow-md hover:bg-gray-400 disabled:opacity-50"
                    disabled={startIndex === 0}
                >
                <FaChevronLeft size={24} />
                </button>

                <div className="flex overflow-hidden w-full justify-center mx-8 space-x-2">
                {mediaList.slice(startIndex, startIndex + visibleThumbnails).map((media, index) => (
                    <img
                    key={index}
                    src={media.url}
                    alt={`Thumbnail ${index}`}
                    className="w-16 h-16 object-contain rounded-md cursor-pointer border-2 transition-all duration-200 
                                hover:border-blue-500"
                    onMouseEnter={() => setselectedMedia(media)}
                    onClick={() => setIsModalOpen(true)}
                    />
                ))}
                </div>

                <button
                onClick={handleNext}
                className="absolute right-0 p-2 bg-gray-300 rounded-full shadow-md hover:bg-gray-400 disabled:opacity-50"
                disabled={startIndex + visibleThumbnails >= mediaList.length}
                >
                <FaChevronRight size={24} />
                </button>
            </div>

            {isModalOpen && (
                <div className="fixed inset-0 bg-black/50 backdrop-blur-md bg-opacity-75 flex justify-center items-center z-50">
                    <div className="relative">
                        <button
                            className="absolute top-2 right-2 text-white bg-gray-700 p-2 rounded-full hover:bg-gray-600"
                            onClick={() => setIsModalOpen(false)}
                        >
                            <FaTimes size={24} />
                        </button>
                        <img
                            src={selectedMedia?.url}
                            alt="Enlarged"
                            className="max-w-[100vw] max-h-[100vh] object-contain rounded-lg shadow-lg"
                        />
                    </div>
                </div>
            )}
        </div>
    )
    
}